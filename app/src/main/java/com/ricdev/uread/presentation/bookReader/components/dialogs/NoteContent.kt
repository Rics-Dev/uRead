package com.ricdev.uread.presentation.bookReader.components.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.ricdev.uread.data.model.Note
import androidx.compose.foundation.background
import androidx.compose.ui.res.stringResource
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences

@Composable
fun NoteContent(
    appPreferences: AppPreferences,
    note: Note,
    onDismiss: () -> Unit,
    onEdit: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    showPremiumModal: () -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedNoteText by remember { mutableStateOf(note.note) }
    var selectedColor by remember { mutableStateOf(Color(note.color.toLong())) }
    var isColorPickerVisible by remember { mutableStateOf(false) } // State to control color picker visibility
    val controller = rememberColorPickerController()

    // List of available default colors
    val defaultColors = listOf(
        Color(0xFFFFF176),
        Color(0xFF6DBA70),
        Color(0xFF618CFF),
        Color(0xFFFF6B6B),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) stringResource(R.string.edit_note) else stringResource(R.string.notes)) },
        text = {
            Column(
                horizontalAlignment = if (isEditing) Alignment.CenterHorizontally else Alignment.Start
            ) {
                if (isEditing) {
                    TextField(
                        value = editedNoteText,
                        onValueChange = { editedNoteText = it },
                        label = { Text(stringResource(R.string.edit_note)) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Default color selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        defaultColors.forEach { color ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .clickable(
                                        onClick = {

                                            selectedColor = color
                                            isColorPickerVisible =
                                                false // Hide color picker if any default color is selected
                                        }
                                    )
                            ) {
                                if (selectedColor == color) {
                                    val circleColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    Canvas(modifier = Modifier.size(40.dp)) {
                                        drawCircle(color = circleColor)
                                    }
                                }
                                Canvas(modifier = Modifier.size(35.dp)) {
                                    drawCircle(color = color)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to show color picker
                    TextButton(onClick = {
                        if(appPreferences.isPremium){
                            isColorPickerVisible = !isColorPickerVisible
                        }else{
                            showPremiumModal()
                        }
                    }) {
                        Text(stringResource(R.string.choose_custom_color))
                    }

                    // Conditional rendering of the color picker
                    if (isColorPickerVisible) {
                        HsvColorPicker(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .padding(10.dp),
                            controller = controller,
                            initialColor = selectedColor,
                            onColorChanged = { colorEnvelope ->
                                selectedColor = colorEnvelope.color
                            }
                        )

                        // Display selected custom color
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(selectedColor)
                        )
                    }
                } else {
                    Text("Note: ${note.note}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(note.selectedText)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isEditing) {
                        onEdit(note.copy(note = editedNoteText, color = selectedColor.toArgb().toString()))
                        isEditing = false
                    } else {
                        isEditing = true
                    }
                }
            ) {
                Text(if (isEditing) stringResource(R.string.save) else stringResource(R.string.edit))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (isEditing) {
                        isEditing = false
                        editedNoteText = note.note
                        selectedColor = Color(note.color.toLong()) // Reset to original values
                    } else {
                        onDelete(note)
                    }
                }
            ) {
                Text(if (isEditing) stringResource(R.string.cancel) else stringResource(R.string.delete))
            }
        }
    )
}


