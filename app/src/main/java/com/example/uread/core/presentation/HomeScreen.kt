package com.example.uread.core.presentation

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import coil.compose.AsyncImage
import com.example.uread.R
import com.example.uread.books.presentation.book_shelf.ShelfPageScreen
import com.example.uread.core.presentation.components.Shelves
import com.example.uread.util.Book
import com.example.uread.util.Navigation
import com.example.uread.util.SharedPreferencesUtil
import kotlinx.coroutines.Dispatchers
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


data class BookMetadata(
    val title: String,
    val authors: List<String>,
    val description: String?,
    val coverBitmap: Bitmap?
)


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferencesUtil = SharedPreferencesUtil()
    var books by remember { mutableStateOf<List<DocumentFile>>(emptyList()) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val shelves = remember { mutableStateListOf("All Books") }
    val pagerState = rememberPagerState { shelves.size }
    var isExpanded by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        if (isExpanded) Color.Black.copy(alpha = 0.6f) else Color.Transparent, label = ""
    )

    val httpClient = DefaultHttpClient()
    val assetRetriever = AssetRetriever(context.contentResolver, httpClient)

    val publicationParser = DefaultPublicationParser(context, httpClient, assetRetriever, null)
    val publicationOpener = PublicationOpener(publicationParser)

    var booksWithMetadata by remember { mutableStateOf<List<Pair<DocumentFile, BookMetadata?>>>(emptyList()) }


    suspend fun extractMetadata(publication: Publication?): BookMetadata? {
        return publication?.let {
            BookMetadata(
                title = it.metadata.title ?: "Unknown Title",
                authors = it.metadata.authors.map { author -> author.name },
                description = it.metadata.description,
                coverBitmap = it.cover()?.let { cover -> cover as? Bitmap }
            )
        }
    }

    suspend fun getBookMetadata(documentFile: DocumentFile): BookMetadata? = withContext(Dispatchers.IO) {
        try {
            val url = documentFile.uri.toAbsoluteUrl()
            val asset = url?.let { assetRetriever.retrieve(it)
                .getOrElse { throw ErrorException(it) } }
            val publication = asset?.let { publicationOpener.open(it, allowUserInteraction = false)
                .getOrElse { throw ErrorException(it) } }
            extractMetadata(publication)
        } catch (e: Exception) {
            null
        }
    }



    LaunchedEffect(books) {
        booksWithMetadata = books.map { documentFile ->
            documentFile to getBookMetadata(documentFile)
        }
    }

    fun getBooksFromDirectory(context: Context, uri: Uri): List<DocumentFile> {
        val booksList = mutableListOf<DocumentFile>()
        val treeUri = DocumentFile.fromTreeUri(context, uri)
        val children = treeUri?.listFiles()

        children?.forEach { documentFile ->
            if (documentFile.isFile && documentFile.name?.endsWith(".epub", ignoreCase = true) == true) {
                booksList.add(documentFile)
            }
        }

        return booksList
    }

    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            val booksList = getBooksFromDirectory(context, it)
            books = booksList
            sharedPreferencesUtil.saveDirectoryUri(context, it.toString())
            sharedPreferencesUtil.setFirstLaunch(context, false)
        }
    }

    LaunchedEffect(Unit) {
        if (sharedPreferencesUtil.isFirstLaunch(context)) {
            getContent.launch(null)
        } else {
            val uriString = sharedPreferencesUtil.getDirectoryUri(context)
            uriString?.let {
                val uri = Uri.parse(it)
                val booksList = getBooksFromDirectory(context, uri)
                books = booksList
            }
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
                when (selectedTab) {
                    0 -> Text("uRead")
                    else -> Text("Shelf $selectedTab")
                }
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
                                onClick = { navController.navigate("search_book_ol_screen") }) {
                                Text("Search in Open Library")
                            }

                            Spacer(modifier = Modifier.width(16.dp))
                            SmallFloatingActionButton(
                                content = {
                                    Icon(
                                        Icons.Filled.Search,
                                        contentDescription = "Search"
                                    )
                                },
                                onClick = { navController.navigate(Navigation.SearchBookOLScreen.route) },
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Row {
                            FilledTonalButton(
                                contentPadding = PaddingValues(8.dp),
                                shape = RoundedCornerShape(12.dp),
                                onClick = { /*TODO*/ }) {
                                Text("Add Manually")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            SmallFloatingActionButton(
                                content = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                                onClick = { /* Handle click */ },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                FloatingActionButton(
                    content = {
                        Icon(
                            if (isExpanded) Icons.Filled.Close else Icons.Filled.Add,
                            contentDescription = if (isExpanded) "Close" else "Add"
                        )
                    },
                    onClick = { isExpanded = !isExpanded }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Shelves(shelves, selectedTab) { index: Int ->
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
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 120.dp),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(booksWithMetadata) { (documentFile, metadata) ->
                                BookItem(documentFile, metadata)
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
fun BookItem(documentFile: DocumentFile, metadata: BookMetadata?) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth()
//            ) {
//                AsyncImage(
//                    model = metadata?.coverUrl,
//                    contentDescription = "Book cover",
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop,
//                    error = painterResource(id = R.drawable.placeholder_cover) // Replace with your placeholder image resource
//                )
//            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.LightGray) // This serves as the placeholder
            ) {
                metadata?.coverBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Book cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Text(
                text = metadata?.title ?: (documentFile.name ?: "Unknown"),
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}