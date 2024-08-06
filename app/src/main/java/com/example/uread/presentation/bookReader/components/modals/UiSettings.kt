package com.example.uread.presentation.bookReader.components.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.elixer.palette.Presets
//import com.elixer.palette.composables.Palette
import com.elixer.palette.constraints.HorizontalAlignment
import com.elixer.palette.constraints.VerticalAlignment
import com.example.uread.data.model.ReaderPreferences
import com.example.uread.util.Palette
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.shared.ExperimentalReadiumApi
import java.util.Locale



enum class ColorType(val displayName: String) {
    BACKGROUND("Background"),
    TEXT("Text"),
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiSettings(
    readerPreferences: ReaderPreferences,
    onPreferencesChanged: (ReaderPreferences) -> Unit,
    onDismiss: () -> Unit,
) {
    var updatedPreferences by remember { mutableStateOf(readerPreferences) }
    var isPaletteVisible by remember { mutableStateOf(false) }
    var editingColorType by remember { mutableStateOf(ColorType.BACKGROUND) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    fun updatePreference(update: ReaderPreferences.() -> ReaderPreferences) {
        updatedPreferences = updatedPreferences.update()
        onPreferencesChanged(updatedPreferences)
    }

    val predefinedColors = remember {
        mapOf(
            "White" to Color.White,
            "Black" to Color.Black,
            "Gray" to Color(0xFFD8D3D6),
            "Blue Gray" to Color(0xFFDBE1F1),
        )
    }

    LaunchedEffect(isPaletteVisible) {
        if (isPaletteVisible) sheetState.expand() else sheetState.partialExpand()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ColorSection(
                title = "Background Color",
                currentColor = updatedPreferences.backgroundColor,
                predefinedColors = predefinedColors,
                onColorSelected = { color ->
                    updatePreference {
                        copy(
                            backgroundColor = color,
                            textColor = if (color == Color.Black) Color.White else Color.Black
                        )
                    }
                },
                onCustomColorClicked = {
                    editingColorType = ColorType.BACKGROUND
                    isPaletteVisible = true
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(20.dp))

            ColorSection(
                title = "Text Color",
                currentColor = updatedPreferences.textColor,
                predefinedColors = predefinedColors,
                onColorSelected = { color ->
                    updatePreference { copy(textColor = color) }
                },
                onCustomColorClicked = {
                    editingColorType = ColorType.TEXT
                    isPaletteVisible = true
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(20.dp))

            if (isPaletteVisible) {
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    "Select ${editingColorType.displayName} Color",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Palette(
                    isVisible = isPaletteVisible,
                    defaultColor = when (editingColorType) {
                        ColorType.BACKGROUND -> updatedPreferences.backgroundColor
                        ColorType.TEXT -> updatedPreferences.textColor
                    },
                    buttonSize = 70.dp,
                    swatches = Presets.material(),
                    innerRadius = 200f,
                    strokeWidth = 90f,
                    spacerRotation = 5f,
                    spacerOutward = 2f,
                    verticalAlignment = VerticalAlignment.Bottom,
                    horizontalAlignment = HorizontalAlignment.Center,
                    onColorSelected = { color ->
                        isPaletteVisible = false
                        updatePreference {
                            when (editingColorType) {
                                ColorType.BACKGROUND -> copy(
                                    backgroundColor = color,
                                    textColor = if (color == Color.Black) Color.White else Color.Black
                                )
                                ColorType.TEXT -> copy(textColor = color)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ColorSection(
    title: String,
    currentColor: Color,
    predefinedColors: Map<String, Color>,
    onColorSelected: (Color) -> Unit,
    onCustomColorClicked: () -> Unit
) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = predefinedColors.entries.find { it.value == currentColor }?.key ?: "Custom Color",
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        predefinedColors.forEach { (_, color) ->
            ColorBox(
                color = color,
                isSelected = color == currentColor,
                onClick = { onColorSelected(color) }
            )
        }
        // Custom Color Picker
        ColorBox(
            color = currentColor,
            isSelected = !predefinedColors.containsValue(currentColor),
            onClick = onCustomColorClicked
        )
    }
}

@Composable
private fun ColorBox(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(shape = RoundedCornerShape(50.dp))
            .border(
                width = 2.dp,
                color = if (isSelected) {
                    if (color == Color.Black) Color(0xFFFFF8DC) else Color.Black
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(50.dp)
            )
            .background(color)
            .clickable(onClick = onClick)
    )
}

