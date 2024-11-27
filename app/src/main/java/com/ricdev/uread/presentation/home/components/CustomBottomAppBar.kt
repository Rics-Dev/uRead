package com.ricdev.uread.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FolderCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.navigation.NavHostController
import com.ricdev.uread.R
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.presentation.home.HomeViewModel
import com.ricdev.uread.navigation.Screens
import com.ricdev.uread.navigation.navigateToScreen
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun CustomBottomAppBar(
//    selectionMode: Boolean,
    shelves: List<Shelf>,
    selectedBooks: List<Book>,
    viewModel: HomeViewModel,
    clearSelection: () -> Unit,
    navController: NavHostController // Add this parameter
) {
    var showAddBookToShelfDialog by remember { mutableStateOf(false) }
    var removeBooksDialog by remember { mutableStateOf(false) }
    var hardRemove by remember { mutableStateOf(false) }
    var selectedShelves by remember { mutableStateOf(setOf<Shelf>()) }
    var unselectedShelves by remember { mutableStateOf(setOf<Shelf>()) }
    var initialShelvesState by remember { mutableStateOf<Map<Long, Boolean>>(emptyMap()) }


    // Fetch books for each shelf
    LaunchedEffect(shelves, selectedBooks) {
        val stateMap = mutableMapOf<Long, Boolean>()
        shelves.forEach { shelf ->
            val books = viewModel.getBooksForShelfSelection(shelf.id).firstOrNull() ?: emptyList()
            stateMap[shelf.id] = selectedBooks.any { book -> books.contains(book) }
        }
        initialShelvesState = stateMap
    }



        BottomAppBar(
            content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = { showAddBookToShelfDialog = true }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Outlined.FolderCopy,
                            contentDescription = "Add book to shelf"
                        )
                    }
                    IconButton(onClick = { removeBooksDialog = true }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Book"
                        )
                    }
                }
            }
        )


    if (showAddBookToShelfDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddBookToShelfDialog = false
            },
            title = { Text(stringResource(R.string.manage_bookshelf)) },
            text = {
                if (shelves.isEmpty()) {
                    Text(stringResource(R.string.you_don_t_have_any_shelves_yet_create_a_shelf_to_add_books))
                } else {
                    Column {
                        shelves.forEach { shelf ->
                            val isChecked = initialShelvesState[shelf.id] ?: false
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            selectedShelves += shelf
                                            unselectedShelves -= shelf
                                        } else {
                                            selectedShelves -= shelf
                                            unselectedShelves += shelf
                                        }
                                        initialShelvesState =
                                            initialShelvesState.toMutableMap().apply {
                                                this[shelf.id] = isChecked
                                            }
                                    }
                                )
                                Text(shelf.name)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(

                        onClick = {
                            showAddBookToShelfDialog = false
                            navController.navigateToScreen(Screens.ShelvesScreen.route)
                        }
                    ) {
                        Text(stringResource(R.string.shelves))
                    }
                    Row {
                        TextButton(
                            onClick = {
                                showAddBookToShelfDialog = false
                                selectedShelves = emptySet()
                                unselectedShelves = emptySet()
                            }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                        TextButton(
                            onClick = {
                                // Add books to selected shelves
                                if (selectedShelves.isNotEmpty()) {
                                    viewModel.addBooksToShelves(
                                        selectedBooks.map { it.id },
                                        selectedShelves.map { it.id }
                                    )
                                }

                                // Remove books from unselected shelves
                                if (unselectedShelves.isNotEmpty()) {
                                    viewModel.removeBooksFromShelves(
                                        selectedBooks.map { it.id },
                                        unselectedShelves.map { it.id }
                                    )
                                }

                                showAddBookToShelfDialog = false
                                selectedShelves = emptySet()
                                unselectedShelves = emptySet()
                                clearSelection()
                            }
                        ) {
                            Text(stringResource(R.string.confirm))
                        }
                    }
                }
            }
        )
    }



    if (removeBooksDialog) {
        AlertDialog(
            onDismissRequest = {
                removeBooksDialog = false
                clearSelection()
            },
            title = { Text(stringResource(R.string.remove_books)) },
            text = {
                Column {
                    Text(text = stringResource(R.string.are_you_sure_you_want_to_remove_the_selected_books))
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = hardRemove,
                            onCheckedChange = { hardRemove = it }
                        )
                        Text(stringResource(R.string.remove_books_from_device))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        viewModel.removeBooks(selectedBooks.map { it }, hardRemove)
                        removeBooksDialog = false
                        clearSelection()
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        removeBooksDialog = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
