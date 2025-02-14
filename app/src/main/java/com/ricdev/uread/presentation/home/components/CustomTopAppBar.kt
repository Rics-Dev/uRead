package com.ricdev.uread.presentation.home.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.Layout
import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.presentation.home.HomeViewModel
import com.ricdev.uread.presentation.sharedComponents.dialogs.DeleteShelfDialog
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

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
    val savedCovers = listSavedBookCovers(context)
    var showGrid by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Image Source") },
        text = {
            Column {
                Text("Choose an image source")
                Spacer(modifier = Modifier.height(16.dp))

                if (showGrid && savedCovers.isNotEmpty()) {
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
                } else {
                    if (savedCovers.isNotEmpty()) {
                        Button(
                            onClick = { showGrid = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.saved_book_covers))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
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
        },
        confirmButton = {},
        dismissButton = {
            if (showGrid) {
                Button(onClick = { showGrid = false }) {
                    Text("Back")
                }
            } else {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun ImageCard(file: File, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = file,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ImagePicker(onImageSelected: (String) -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Content picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val imagePath = saveHomeBackgroundImage(context, it)
            imagePath?.let { path -> onImageSelected(path) }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false) ||
                    permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false) -> {
                imagePicker.launch("image/*")
            }
        }
    }

    fun checkAndRequestPermissions() {
        when {
            // Android 14+ (API 34)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ))
            }
            // Android 13 (API 33)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                ))
            }
            // Android 12L and below
            else -> {
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ))
            }
        }
    }

    fun hasPermissions(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PERMISSION_GRANTED
            }
            else -> {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PERMISSION_GRANTED
            }
        }
    }

    DropdownMenuItem(
        onClick = {
            showDialog = true
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(stringResource(R.string.change_home_background))
                Spacer(modifier = Modifier.width(8.dp))
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
            onSelectBookCover = { path ->
                onImageSelected(path)
            },
            onSelectImagePicker = {
                if (hasPermissions()) {
                    imagePicker.launch("image/*")
                } else {
                    checkAndRequestPermissions()
                }
            }
        )
    }
}


private fun saveHomeBackgroundImage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val imageBytes = inputStream?.readBytes()
        inputStream?.close()

        if (imageBytes == null) return null

        val md = MessageDigest.getInstance("MD5")
        val imageHash = md.digest(imageBytes).joinToString("") { "%02x".format(it) }
        val fileName = "home_bg_${imageHash}.jpg"

        // Changed to match exact filename instead of startsWith
        val existingFile = context.filesDir.listFiles { file ->
            file.name == fileName
        }?.firstOrNull()

        if (existingFile != null) {
            return existingFile.absolutePath
        }

        val file = File(context.filesDir, fileName)

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()

        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun listSavedBookCovers(context: Context): List<File> {
    val filesDir = context.filesDir
    return filesDir.listFiles { file ->
        val isJpg = file.extension.lowercase() == "jpg"
        val name = file.nameWithoutExtension
        isJpg && !name.matches(Regex("home_bg_[a-f0-9]+"))
    }?.toList() ?: emptyList()
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