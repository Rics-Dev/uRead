package com.ricdev.uread.presentation.notes


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.Note
import com.ricdev.uread.presentation.bookReader.components.dialogs.NoteContent
import com.ricdev.uread.presentation.sharedComponents.CustomNavigationDrawer
import com.ricdev.uread.util.PurchaseHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavHostController,
    purchaseHelper: PurchaseHelper,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()

    val booksWithNotes by viewModel.booksWithNotes.collectAsStateWithLifecycle(initialValue = emptyList())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val sortedBooksWithNotes = booksWithNotes.sortedBy { it.book.title }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }


    var showNoteDialog by remember { mutableStateOf(false) }
//    var showPremiumModal by remember { mutableStateOf(false) }




    var selectedNote by remember {
        mutableStateOf(
            Note(
                note = "",
                selectedText = "",
                color = Color.Yellow.toArgb().toString(),
                bookId = 0,
                locator = "",
            )
        )
    }

    CustomNavigationDrawer(
        purchaseHelper = purchaseHelper,
        drawerState = drawerState,
        navController = navController
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.notes)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { isSearchActive = !isSearchActive }
                        ) {
                            Icon( if(isSearchActive) Icons.Default.Close else Icons.Default.Search, contentDescription = "Search Note")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                AnimatedVisibility(visible = isSearchActive) {
                    // Search bar
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text(stringResource(R.string.search_notes)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
                    )
                }


                if (sortedBooksWithNotes.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            stringResource(R.string.no_notes_found),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            stringResource(R.string.start_adding_notes_to_your_books),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 16.dp
                    ) {
                        sortedBooksWithNotes.forEachIndexed { index, bookWithNotes ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = bookWithNotes.book.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }

                    BookNotesContent(
                        appPreferences = appPreferences,
                        bookWithNotes = sortedBooksWithNotes[selectedTabIndex],
                        searchQuery = searchQuery,
                        onNoteClick = { note ->
                            showNoteDialog = true
                            selectedNote = note
                        },
                        onUpdateNote = { updatedNote -> viewModel.updateNote(updatedNote) },
                        onRemoveNote = { note -> viewModel.deleteNote(note) },
                        showPremiumModal = {
//                            showPremiumModal = true
                            viewModel.purchasePremium(purchaseHelper)
                        }
                    )
                }
            }

            if (showNoteDialog) {
                NoteContent(
                    appPreferences = appPreferences,
                    note = selectedNote,
                    onDismiss = { showNoteDialog = false },
                    onEdit = { editedNote ->
                        viewModel.updateNote(editedNote)
                        showNoteDialog = false
                    },
                    onDelete = { note ->
                        viewModel.deleteNote(note)
                        showNoteDialog = false
                    },
                    showPremiumModal = {
                        viewModel.purchasePremium(purchaseHelper)
//                        showPremiumModal = true
                    }
                )
            }
        }
    }
//    if (showPremiumModal) {
//        PremiumModal(
//            purchaseHelper = purchaseHelper,
//            hidePremiumModal = { showPremiumModal = false }
//        )
//    }
}


@Composable
fun BookNotesContent(
    appPreferences: AppPreferences,
    bookWithNotes: BookWithNotes,
    searchQuery: String,
    onNoteClick: (Note) -> Unit,
    onUpdateNote: (Note) -> Unit,
    onRemoveNote: (Note) -> Unit,
    showPremiumModal: () -> Unit
) {
    val filteredNotes = bookWithNotes.notes.filter { note ->
        note.note.contains(searchQuery, ignoreCase = true) ||
                note.selectedText.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(filteredNotes) { note ->
            NoteItem(
                appPreferences = appPreferences,
                note = note,
                onClick = { onNoteClick(note) },
                onUpdateNote = { updatedNote -> onUpdateNote(updatedNote) },
                onRemoveNote = { onRemoveNote(note) },
                showPremiumModal = { showPremiumModal() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NoteItem(
    appPreferences: AppPreferences,
    note: Note,
    onClick: () -> Unit,
    onUpdateNote: (Note) -> Unit,
    onRemoveNote: () -> Unit,
    showPremiumModal: () -> Unit,
) {

    var showRemoveNoteDialog by remember { mutableStateOf(false) }

    var isPaletteVisible by remember { mutableStateOf(false) }

    var selectedColor by remember {
        mutableStateOf(
            Color(
                note.color.toIntOrNull() ?: Color.Yellow.toArgb()
            )
        )
    }
    val controller = rememberColorPickerController()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                clip = true
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)


    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            note.color
                                .toIntOrNull()
                                ?.let { Color(it) } ?: Color.Yellow)
                        .clickable(onClick = {
                            if (appPreferences.isPremium) {
                                isPaletteVisible = !isPaletteVisible
                            } else {
                                showPremiumModal()
                            }
                        })
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = note.note,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
//                    onClick = onRemoveNote,
                    onClick = { showRemoveNoteDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove Note")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.selectedText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )



            AnimatedVisibility(
                visible = isPaletteVisible,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        HsvColorPicker(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(350.dp)
                                .padding(10.dp),
                            controller = controller,
                            initialColor = selectedColor,
                            onColorChanged = { colorEnvelope ->
                                selectedColor = colorEnvelope.color
                            }
                        )

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(selectedColor)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val updatedNote =
                                    note.copy(color = selectedColor.toArgb().toString())
                                onUpdateNote(updatedNote)
                                isPaletteVisible = false
                            }
                        ) {
                            Text(stringResource(R.string.select))
                        }
                    }
                }
            }
        }
    }





    if (showRemoveNoteDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveNoteDialog = false },
            title = { Text("Remove Note") },
            text = { Text("Are you sure you want to remove this note?") },
            dismissButton = {
                Button(
                    onClick = { showRemoveNoteDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                    onClick = {
                        onRemoveNote()
                        showRemoveNoteDialog = false
                    }
                ) {
                    Text("Remove")
                }
            },
        )
    }


}


data class BookWithNotes(
    val book: Book,
    val notes: List<Note>
)
