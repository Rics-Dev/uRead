package com.ricdev.uread.presentation.bookReader.components.drawers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Note
import com.ricdev.uread.presentation.bookReader.BookReaderViewModel
//import com.ricsdev.uread.presentation.sharedComponents.PremiumModal
import com.ricdev.uread.util.PurchaseHelper

@Composable
fun NotesDrawer(
    viewModel: BookReaderViewModel,
    purchaseHelper: PurchaseHelper,
    appPreferences: AppPreferences,
    isOpen: Boolean,
    onClose: () -> Unit,
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onUpdateNote: (Note) -> Unit,
    onRemoveNote: (Note) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(stringResource(R.string.notes))
//    var showPremiumModal by remember { mutableStateOf(false) }


    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ModalDrawerSheet(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close Notes")
                        }
                        Text(stringResource(R.string.notes), style = MaterialTheme.typography.titleLarge)
                    }

                    TabRow(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow ,
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title) }
                            )
                        }
                    }


                    when (selectedTabIndex) {
                        0 -> {
                            BookmarksList(
                                appPreferences = appPreferences,
                                notes = notes.reversed(),
                                onNoteClick = onNoteClick,
                                onUpdateNote = { updatedNote -> onUpdateNote(updatedNote)},
                                onRemoveNote = onRemoveNote,
                                showPremiumModal = {
                                    viewModel.purchasePremium(purchaseHelper)
//                                    showPremiumModal = true
                                }
                                )
                        }
                    }
                }
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
fun BookmarksList(
    appPreferences: AppPreferences,
    notes: List<Note>, onNoteClick: (Note) -> Unit, onUpdateNote: (Note) -> Unit ,onRemoveNote: (Note) -> Unit,
    showPremiumModal: () -> Unit,
    ) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    )  {
        items(notes) { note ->
            NoteItem(
                appPreferences = appPreferences,note = note, onClick = { onNoteClick(note) }, onUpdateNote = { updatedNote -> onUpdateNote(updatedNote) } ,onRemoveNote = { onRemoveNote(note) },
                showPremiumModal = { showPremiumModal() }
                )
            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}

@Composable
fun NoteItem(
    appPreferences: AppPreferences,
    note: Note,
    onClick: () -> Unit,
    onUpdateNote: (Note) -> Unit,
    onRemoveNote: (Note) -> Unit,
    showPremiumModal: () -> Unit,

) {



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
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
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
                            if(appPreferences.isPremium){
                                isPaletteVisible = !isPaletteVisible
                            }else{
                                showPremiumModal()
                            }
                        })
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = note.note,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f) // Allocate remaining space to text
                )
                IconButton(
                    onClick = { onRemoveNote(note) },
                    modifier = Modifier.size(24.dp) // Adjust size if needed
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove Note")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.selectedText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2,
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
}