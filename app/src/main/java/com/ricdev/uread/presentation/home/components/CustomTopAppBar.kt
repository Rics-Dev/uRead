package com.ricdev.uread.presentation.home.components

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.Layout
import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.presentation.home.HomeViewModel
import com.ricdev.uread.presentation.sharedComponents.dialogs.DeleteShelfDialog
import com.ricdev.uread.util.ImageUtils
import com.ricdev.uread.util.PermissionHandler
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    viewModel: HomeViewModel,
    selectedTab: Int,
    shelves: List<Shelf>,
    selectedBooks: List<Book>,
    selectionMode: Boolean,
    clearSelection: () -> Unit,
    selectAll: () -> Unit,
    appPreferences: AppPreferences,
    toggleLayoutModal: () -> Unit,
    toggleSortFilterModal: () -> Unit,
    totalBooks: Int,
    currentShelfBookCount: Int,
    toggleSearchMode: () -> Unit,
    openDrawer: () -> Unit,
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    val dropdownMenuOffset = remember { mutableStateOf(DpOffset.Zero) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var shelfToRemove by remember { mutableStateOf<Shelf?>(null) }
    val isAddingBook by viewModel.isAddingBooks.collectAsState()

    TopAppBar(
        title = {
            if (selectionMode) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("${selectedBooks.size}")
                    IconButton(onClick = {
                        clearSelection()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Selection Mode"
                        )
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = when (selectedTab) {
                            0 -> if (appPreferences.showEntries) "uRead " else "uRead"
                            else -> shelves.getOrNull(selectedTab - 1)?.name ?: "Unknown Shelf"
                        }
                    )
                    AnimatedVisibility(visible = appPreferences.showEntries) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.LightGray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)

                        ) {
                            Text(
                                text = when (selectedTab) {
                                    0 -> "$totalBooks"
                                    else -> "$currentShelfBookCount"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        actions = {
            if (selectionMode) {
                IconButton(onClick = {
                    selectAll()
                }) {
                    Icon(
                        imageVector = Icons.Filled.SelectAll,
                        contentDescription = "Select All"
                    )
                }
            } else {
                IconButton(
//                    enabled = !isAddingBook,
                    onClick = {
                    toggleSearchMode()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search books"
                    )
                }
                BadgedBox(
                    badge = {
                       if(appPreferences.readingStatus.isNotEmpty() || appPreferences.fileTypes.isNotEmpty()) Badge()
                    }
                ) {
                    IconButton(
                        onClick = {
                            toggleSortFilterModal()
                        }) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Sort & Filter books"
                        )
                    }
                }
                IconButton(
//                    enabled = !isAddingBook,
                    onClick = {
                    toggleLayoutModal()
                }) {
                    Icon(
                        if (appPreferences.homeLayout == Layout.Grid || appPreferences.homeLayout == Layout.CoverOnly) Icons.Outlined.GridView
                        else Icons.Outlined.ViewAgenda,
                        contentDescription = "Change Layout"
                    )
                }
                IconButton(
                    enabled = !isAddingBook,
                    onClick = {
                        dropdownMenuExpanded = !dropdownMenuExpanded
                    },
                    modifier = Modifier.onSizeChanged { size ->
                        dropdownMenuOffset.value = DpOffset((size.width / 5).dp, 0.dp)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More Actions"
                    )
                }
                DropdownMenu(
                    expanded = dropdownMenuExpanded,
                    onDismissRequest = {
                        dropdownMenuExpanded = false
                    },
                    offset = dropdownMenuOffset.value
                ) {
                    if (selectedTab != 0) {
                        DropdownMenuItem(
                            onClick = {
                                // Show confirmation dialog
                                shelfToRemove = shelves[selectedTab - 1]
                                showConfirmDialog = true
                                dropdownMenuExpanded = false
                            },
                            text = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        stringResource(
                                            R.string.remove_shelf,
                                            shelves[selectedTab - 1].name
                                        ),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete Shelf",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    }

                    ImagePicker { path ->
                        viewModel.updateAppPreferences(appPreferences.copy(homeBackgroundImage = path))
                    }

                    DropdownMenuItem(onClick = {
                        viewModel.refreshBooks()
                        dropdownMenuExpanded = false
                    },
                        text = {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(stringResource(R.string.refresh_library))
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Library",
                                )
                            }

                        }
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick =  openDrawer ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
    )

    // Show confirmation dialog
    if (showConfirmDialog && shelfToRemove != null) {
        DeleteShelfDialog(
            selectedShelf = shelfToRemove,
            onDismiss = {
                showConfirmDialog = false
                shelfToRemove = null
            },
            onConfirmDelete = {
                viewModel.removeShelf(it)
                showConfirmDialog = false
                shelfToRemove = null
            }
        )
    }
}

@Composable
fun ImageSourceDialog(
    context: Context,
    onDismiss: () -> Unit,
    onSelectBookCover: (String) -> Unit,
    onSelectImagePicker: () -> Unit
) {
    val savedCovers = remember { ImageUtils.listSavedBookCovers(context) }
    var showGrid by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.change_home_background)) },
        text = {
            Column {
                when {
                    showGrid && savedCovers.isNotEmpty() -> SavedCoversGrid(savedCovers, onSelectBookCover, onDismiss)
                    else -> {
                        Column {
                            if (savedCovers.isNotEmpty()) {
                                Button(
                                    onClick = { showGrid = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.saved_book_covers))
                                }
                                Spacer(Modifier.height(16.dp))
                            }

                            Button(
                                onClick = {
                                    onSelectImagePicker()
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.select_from_gallery))
                            }
                        }
                    }
                }
            }
               },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = {
                    if (showGrid) {
                        showGrid = false
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(if (showGrid) stringResource(R.string.back) else stringResource(R.string.cancel))
            }
 }
    )
}

@Composable
private fun SavedCoversGrid(
    savedCovers: List<File>,
    onSelectBookCover: (String) -> Unit,
    onDismiss: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        content = {
            items(savedCovers.sortedByDescending { it.lastModified() }) { file ->
                ImageCard(
                    file = file,
                    onClick = {
                        onSelectBookCover(file.absolutePath)
                        onDismiss()
                    }
                )
            }
        }
    )
}

@Composable
fun ImageCard(file: File, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = file,
            contentDescription = "Book Cover",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ImagePicker(onImageSelected: (String) -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // GetContent launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onImageSelected(ImageUtils.saveHomeBackgroundImage(context, it).orEmpty())
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.any { it.value }) {
            imagePicker.launch("image/*")
        }
    }

    DropdownMenuItem(
        onClick = { showDialog = true },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(stringResource(R.string.change_home_background))
                Icon(
                    imageVector = Icons.Default.ImageSearch,
                    contentDescription = "Select Image",
                )
            }
        }
    )

    if (showDialog) {
        ImageSourceDialog(
            context = context,
            onDismiss = { showDialog = false },
            onSelectBookCover = onImageSelected,
            onSelectImagePicker = {
                if (PermissionHandler.hasPermissions(context)) {
                    imagePicker.launch("image/*")
                } else {
                    PermissionHandler.requestPermissions(permissionLauncher)
                }
            }
        )
    }
}





@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(50.dp)
            ),
        placeholder = { Text(stringResource(R.string.search_books)) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close search")
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(50.dp),
        textStyle = MaterialTheme.typography.bodyLarge



    )
}