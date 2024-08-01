package com.example.uread.core.presentation

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.NavHostController
import androidx.room.Room
import coil.compose.AsyncImage
import com.example.uread.R
import com.example.uread.books.data.datasource.local.AppDatabase
import com.example.uread.books.data.datasource.local.Book
import com.example.uread.books.presentation.book_shelf.ShelfPageScreen
import com.example.uread.core.presentation.components.Shelves
import com.example.uread.util.Navigation
import com.example.uread.util.SharedPreferencesUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.shared.util.ErrorException
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.getOrThrow
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.toAbsoluteUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import java.io.File





@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferencesUtil = SharedPreferencesUtil()
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val shelves = remember { mutableStateListOf("All Books") }
    val pagerState = rememberPagerState { shelves.size }
    var isExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val backgroundColor by animateColorAsState(
        if (isExpanded) Color.Black.copy(alpha = 0.6f) else Color.Transparent, label = ""
    )
    val coroutineScope = rememberCoroutineScope()

    // Create database instance
    val database = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "book-database").build()
    }
    val bookDao = remember { database.bookDao() }

    val httpClient = DefaultHttpClient()
    val assetRetriever = AssetRetriever(context.contentResolver, httpClient)
    val publicationParser = DefaultPublicationParser(context, httpClient, assetRetriever, null)
    val publicationOpener = PublicationOpener(publicationParser)

    // Helper function to save cover bitmap to file
    fun saveCoverToFile(bitmap: Bitmap, uri: String): String {
        val file = File(context.filesDir, "covers/${uri.hashCode()}.png")
        file.parentFile?.mkdirs()
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file.absolutePath
    }

    suspend fun extractBookInfo(publication: Publication?, documentFile: DocumentFile): Book {
        val coverBitmap = publication?.cover()
        val coverPath = coverBitmap?.let { saveCoverToFile(it, documentFile.uri.toString()) }

        return Book(
            uri = documentFile.uri.toString(),
            title = publication?.metadata?.title ?: documentFile.name ?: "Unknown Title",
            authors = publication?.metadata?.authors?.joinToString(",") { it.name } ?: "",
            description = publication?.metadata?.description,
            coverPath = coverPath,
            lastModified = documentFile.lastModified()
        )
    }

    suspend fun getBookInfo(documentFile: DocumentFile): Book = withContext(Dispatchers.IO) {
        try {
            val url = documentFile.uri.toAbsoluteUrl()
            val asset = url?.let { assetRetriever.retrieve(it).getOrElse { throw ErrorException(it) } }
            val publication = asset?.let { publicationOpener.open(it, allowUserInteraction = false).getOrElse { throw ErrorException(it) } }
            extractBookInfo(publication, documentFile)
        } catch (e: Exception) {
            Book(
                uri = documentFile.uri.toString(),
                title = documentFile.name ?: "Unknown",
                authors = "",
                description = null,
                coverPath = null,
                lastModified = documentFile.lastModified()
            )
        }
    }

    fun getBooksFromDirectory(context: Context, uri: Uri): List<DocumentFile> {
        val booksList = mutableListOf<DocumentFile>()
        val treeUri = DocumentFile.fromTreeUri(context, uri)
        treeUri?.listFiles()?.forEach { documentFile ->
            if (documentFile.isFile && documentFile.name?.endsWith(".epub", ignoreCase = true) == true) {
                booksList.add(documentFile)
            }
        }
        return booksList
    }


    suspend fun processBookUpdates(booksList: List<DocumentFile>, existingBooks: List<Book>) {
        val lastUpdateTime = sharedPreferencesUtil.getLastUpdateTime(context)
        val updatedBooks = mutableListOf<Book>()

        booksList.forEach { documentFile ->
            val fileUri = documentFile.uri.toString()
            val lastModified = documentFile.lastModified()
            val existingBook = existingBooks.find { it.uri == fileUri }

            if (existingBook != null && lastModified > existingBook.lastModified) {
                val updatedBook = getBookInfo(documentFile)
                bookDao.insertBook(updatedBook)
                updatedBooks.add(updatedBook)
            }
        }

        withContext(Dispatchers.Main) {
            books = books.filter { book -> book.uri !in updatedBooks.map { it.uri } } + updatedBooks
        }

        sharedPreferencesUtil.saveLastUpdateTime(context, System.currentTimeMillis())
    }

    suspend fun loadBooks(uri: Uri) {
        coroutineScope.launch(Dispatchers.IO) {
            // Load existing books from the database on a background thread
            val existingBooks = bookDao.getAllBooks()

            withContext(Dispatchers.Main) {
                // Update UI on the main thread
                books = existingBooks
                isLoading = false
            }

            // Perform the update check
            val booksList = getBooksFromDirectory(context, uri)
            val existingUris = existingBooks.map { it.uri }.toSet()
            val documentUris = booksList.map { it.uri.toString() }.toSet()

            // Remove books that no longer exist on the device
            val booksToRemove = existingBooks.filter { it.uri !in documentUris }
            booksToRemove.forEach { book ->
                bookDao.deleteBookByUri(book.uri)
            }

            // Find new books
            val newBooks = booksList.filter { !existingUris.contains(it.uri.toString()) }
            val updatedBooks = mutableListOf<Book>()

            // Process new books
            newBooks.forEach { documentFile ->
                val book = getBookInfo(documentFile)
                bookDao.insertBook(book)
                updatedBooks.add(book)
            }

            // Update the UI with changes
            withContext(Dispatchers.Main) {
                books = (existingBooks - booksToRemove.toSet()) + updatedBooks
            }

            // Process updates for existing books
            processBookUpdates(booksList, existingBooks)

            // Save the last update time
            sharedPreferencesUtil.saveLastUpdateTime(context, System.currentTimeMillis())
        }
    }



    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            coroutineScope.launch {
                loadBooks(it)
                sharedPreferencesUtil.saveDirectoryUri(context, it.toString())
                sharedPreferencesUtil.setFirstLaunch(context, false)
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        val uriString = sharedPreferencesUtil.getDirectoryUri(context)
        if (uriString == null || sharedPreferencesUtil.isFirstLaunch(context)) {
            isLoading = true
            getContent.launch(null)
        } else {
            val uri = Uri.parse(uriString)
            loadBooks(uri)
        }
    }

    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab)
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTab = pagerState.currentPage
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(title = {
                Text(when (selectedTab) {
                    0 -> "uRead"
                    else -> "Shelf $selectedTab"
                })
            })
        },
        floatingActionButton = {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.End,
            ) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut(),
                ) {
                    Column(
                        modifier = Modifier.padding(4.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Row {
                            FilledTonalButton(
                                contentPadding = PaddingValues(8.dp),
                                shape = RoundedCornerShape(12.dp),
                                onClick = { navController.navigate("search_book_ol_screen") }
                            ) {
                                Text("Search in Open Library")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            SmallFloatingActionButton(
                                onClick = { navController.navigate(Navigation.SearchBookOLScreen.route) },
                            ) {
                                Icon(Icons.Filled.Search, contentDescription = "Search")
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Row {
                            FilledTonalButton(
                                contentPadding = PaddingValues(8.dp),
                                shape = RoundedCornerShape(12.dp),
                                onClick = { /*TODO*/ }
                            ) {
                                Text("Add Manually")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            SmallFloatingActionButton(
                                onClick = { /* Handle click */ },
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Add")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                FloatingActionButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Icon(
                        if (isExpanded) Icons.Filled.Close else Icons.Filled.Add,
                        contentDescription = if (isExpanded) "Close" else "Add"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Shelves(shelves, selectedTab) { index ->
                selectedTab = index
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.background)
            ) { index ->
                when (index) {
                    0 -> {
                        if (isLoading) {
                            // Display skeletons while loading
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 120.dp),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(20) {
                                    BookItem(book = null, isLoading = true)
                                }
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 120.dp),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(books) { book ->
                                    BookItem(book)
                                }
                            }
                        }
                    }
                    else -> ShelfPageScreen(index)
                }
            }
        }
    }
}

@Composable
fun BookItem(book: Book?, isLoading: Boolean = false) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (isLoading) {
            // Show a loading placeholder
            Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.LightGray)
                ) {
                    book?.coverPath?.let { path ->
                        val bitmap = BitmapFactory.decodeFile(path)
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Book cover",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Text(
                    text = book?.title ?: "Loading...",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}