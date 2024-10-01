package com.ricdev.uread.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ricdev.uread.R
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.presentation.settings.viewmodels.DeletedBooksViewModel
import com.ricdev.uread.presentation.settings.states.DeletedBooksState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletedBooksScreen(
    navController: NavController,
    viewModel: DeletedBooksViewModel = hiltViewModel()
) {
    val deletedBooksState by viewModel.deletedBooksState.collectAsStateWithLifecycle()


    var selectedBooks by remember { mutableStateOf(setOf<Book>()) }
    var bookToDelete by remember { mutableStateOf<Book?>(null) }
    val isSelectionMode = selectedBooks.isNotEmpty()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRestoreSuccessMessage by remember { mutableStateOf(false) }
    var showDeleteMessage by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(showRestoreSuccessMessage || showDeleteMessage) {
        if (showRestoreSuccessMessage || showDeleteMessage) {
            snackbarHostState.showSnackbar("")
            delay(3000)
            showRestoreSuccessMessage = false
            showDeleteMessage = false
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSelectionMode) {
                            selectedBooks = emptySet()
                        } else {
                            navController.navigateUp()
                        }
                    }) {
                        Icon(
                            if (isSelectionMode) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isSelectionMode) "Clear Selection" else "Back"
                        )
                    }
                },
                title = { Text(text = if (isSelectionMode) stringResource(
                    R.string.selected,
                    selectedBooks.size
                ) else stringResource(R.string.deleted_books)) },
                actions = {
                    if (isSelectionMode) {
                        IconButton(onClick = {
                            viewModel.restoreBooks(selectedBooks)
                            selectedBooks = emptySet()
                            showRestoreSuccessMessage = true
                        }) {
                            Icon(Icons.Default.RestartAlt, contentDescription = "Restore Selected")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = "Delete Selected",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            ) { _ ->
                Snackbar(
                    action = {
                        TextButton(
                            onClick = {
                                showRestoreSuccessMessage = false
                                showDeleteMessage = false
                                snackbarHostState.currentSnackbarData?.dismiss()
                            }
                        ) {
                            Text(stringResource(R.string.dismiss))
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        if(showDeleteMessage){
                            Text(stringResource(R.string.books_deleted_successfully))
                        }else{
                            Text(stringResource(R.string.books_restored_successfully))
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        // Handle different states of deletedBooksState
        when (deletedBooksState) {
            is DeletedBooksState.Loading -> {
                // Show a loading indicator
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DeletedBooksState.Error -> {
                // Show an error message
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (deletedBooksState as DeletedBooksState.Error).message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is DeletedBooksState.Success -> {
                val deletedBooks = (deletedBooksState as DeletedBooksState.Success).books
                if (deletedBooks.isEmpty()) {
                    // Display a message or image when there are no deleted books
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_deleted_books),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        items(deletedBooks) { book ->
                            val isSelected = book in selectedBooks
                            ListItem(
                                headlineContent = { Text(text = book.title, maxLines = 1) },
                                supportingContent = { Text(text = book.authors, maxLines = 1) },
                                trailingContent = {
                                    if (!isSelectionMode) {
                                        Row {
                                            IconButton(onClick = {
                                                viewModel.restoreBooks(setOf(book))
                                                showRestoreSuccessMessage = true
                                            }) {
                                                Icon(Icons.Default.RestartAlt, contentDescription = "Restore")
                                            }
                                            IconButton(onClick = { bookToDelete = book }) {
                                                Icon(
                                                    Icons.Outlined.Delete,
                                                    contentDescription = "Delete Permanently",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                },
                                colors = ListItemDefaults.colors(
                                    headlineColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.1f
                                    ) else MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier
                                    .selectable(
                                        selected = book in selectedBooks,
                                        onClick = {
                                            selectedBooks = if (book in selectedBooks) {
                                                selectedBooks - book
                                            } else {
                                                selectedBooks + book
                                            }
                                        }
                                    )
                            )
                        }
                    }
                }
            }

        }


        if (showDeleteDialog && bookToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    bookToDelete = null
                    selectedBooks = emptySet()
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (bookToDelete != null) {
                            viewModel.permanentlyDeleteBooks(setOf(bookToDelete!!))
                        } else {
                            viewModel.permanentlyDeleteBooks(selectedBooks)
                        }
                        showDeleteDialog = false
                        bookToDelete = null
                        selectedBooks = emptySet()
                        showDeleteMessage = true
                    }) {
                        Text(text = stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        bookToDelete = null
                        selectedBooks = emptySet()
                    }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                },
                title = {
                    Text(text = if (bookToDelete != null) stringResource(R.string.delete_book) else stringResource(
                        R.string.delete_selected_books
                    )
                    )
                },
                text = {
                    Text(
                        text = if (bookToDelete != null)
                            stringResource(
                                R.string.are_you_sure_you_want_to_delete_from_your_device,
                                bookToDelete!!.title
                            )
                        else
                            stringResource(R.string.are_you_sure_you_want_to_delete_the_selected_books_from_your_device)
                    )
                }
            )
        }
    }
}