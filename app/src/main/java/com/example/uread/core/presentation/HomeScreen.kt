package com.example.uread.core.presentation

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.uread.books.presentation.book_shelf.ShelfPageScreen
import com.example.uread.core.presentation.components.Shelves
import com.example.uread.util.Navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun HomeScreen(navController: NavHostController) {


    val context = LocalContext.current
    var books by remember { mutableStateOf<List<File>>(emptyList()) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val shelves = remember { mutableStateListOf("All Books") }
    val pagerState = rememberPagerState { shelves.size }
    var isExpanded by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        if (isExpanded) Color.Black.copy(alpha = 0.6f) else Color.Transparent, label = ""
    )



    fun getBooksFromDirectory(contentResolver: ContentResolver, uri: Uri): List<File> {
        val booksList = mutableListOf<File>()
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri))
        val cursor: Cursor? = contentResolver.query(childrenUri, arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME), null, null, null)

        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val nameIndex = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)

            while (it.moveToNext()) {
                val documentId = it.getString(idIndex)
                val displayName = it.getString(nameIndex)
                val documentUri = DocumentsContract.buildDocumentUriUsingTree(uri, documentId)

                // Filter for EPUB files
                if (displayName.endsWith(".epub", ignoreCase = true)) {
                    booksList.add(File(documentUri.path))
                }
            }
        }

        return booksList
    }


    // Handle the directory picker result
    val getContent =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
            uri?.let {
                val booksList = getBooksFromDirectory(context.contentResolver, it)
                books = booksList
            }
        }



    // Launch the directory picker when the composable is first launched
    LaunchedEffect(Unit) {
            getContent.launch(null)
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
                modifier = Modifier
                    .wrapContentSize(),
                horizontalAlignment = Alignment.End,
            ) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut(),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(4.dp),
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

                            // Display the list of EPUB files
                            LazyColumn {
                                items(books) { file ->
                                    Text(text = file.name, modifier = Modifier.padding(16.dp))
                                }
                            }
                    }
                    else -> ShelfPageScreen(index)
                }
            }
        }
    }
}
