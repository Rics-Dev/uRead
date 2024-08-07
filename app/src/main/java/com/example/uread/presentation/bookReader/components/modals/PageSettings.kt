package com.example.uread.presentation.bookReader.components.modals


import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.uread.data.model.ReaderPreferences
import org.readium.r2.navigator.preferences.TextAlign
import org.readium.r2.shared.ExperimentalReadiumApi
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalReadiumApi::class)
@Composable
fun PageSettings(
    readerPreferences: ReaderPreferences,
    onPreferencesChanged: (ReaderPreferences) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var updatedPreferences by remember { mutableStateOf(readerPreferences) }
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun updatePreference(update: ReaderPreferences.() -> ReaderPreferences) {
        updatedPreferences = updatedPreferences.update()
        onPreferencesChanged(updatedPreferences)
    }

    val minFontSize = 0.5
    val maxFontSize = 2.0
    val minPageMargins = 0.0
    val maxPageMargins = 5.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            //can be changed on publisher styles
            SettingsRange(
                title = "Page Margins",
                value = updatedPreferences.pageMargins,
                onValueChange = { updatePreference { copy(pageMargins = it) } },
                valueRange = minPageMargins..maxPageMargins,
                valueDisplay = { String.format(Locale.getDefault(), "%.1f", it) }
            )


            //cannot be changed on publisher styles
            SettingsRange(
                title = "Paragraph Indent",
                value = updatedPreferences.paragraphIndent,
                onValueChange = { updatePreference { copy(paragraphIndent = it) } },
                valueRange = 0.0..3.0,
                valueDisplay = { String.format(Locale.getDefault(), "%.1f", it) },
                enabled = !readerPreferences.publisherStyles
            )

            //cannot be changed on publisher styles
            SettingsRange(
                title = "Paragraph Spacing",
                value = updatedPreferences.paragraphSpacing,
                onValueChange = { updatePreference { copy(paragraphSpacing = it) } },
                valueRange = 0.0..3.0,
                valueDisplay = { String.format(Locale.getDefault(), "%.1f", it) },
                enabled = !readerPreferences.publisherStyles
            )


            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Text Align", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    listOf(
                        TextAlign.LEFT to "Left",
                        TextAlign.JUSTIFY to "Justify",
                        TextAlign.RIGHT to "Right"
                    ).forEach { (alignment, label) ->
                        FilledTonalButton(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (updatedPreferences.textAlign == alignment) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                contentColor = if (updatedPreferences.textAlign == alignment) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            ),
                            onClick = {
                                if (readerPreferences.publisherStyles) {
                                    Toast.makeText(
                                        context,
                                        "Cannot change settings on publisher styles",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    updatePreference { copy(textAlign = alignment) }
                                }

                            }
                        ) {
                            Text(text = label)
                        }
                    }
                }
            }


        }
    }
}


//@Composable
//fun SettingsSlider(
//    title: String,
//    value: Double,
//    onValueChange: (Double) -> Unit,
//    valueRange: ClosedFloatingPointRange<Double>,
//    valueDisplay: (Double) -> String
//) {
//    Text(title, style = MaterialTheme.typography.titleMedium)
//    Slider(
//        value = value.toFloat(),
//        onValueChange = { onValueChange(it.toDouble()) },
//        valueRange = valueRange.start.toFloat()..valueRange.endInclusive.toFloat()
//    )
//    Text(valueDisplay(value), style = MaterialTheme.typography.bodyMedium)
//    Spacer(modifier = Modifier.height(16.dp))
//}

//cannot be changed on publisher styles
//            SettingsDropdown(
//                title = "Text Align",
//                options = listOf(TextAlign.LEFT, TextAlign.RIGHT, TextAlign.JUSTIFY),
//                selectedOption = updatedPreferences.textAlign,
//                onOptionSelected = { updatePreference { copy(textAlign = it) } },
//                enabled = !readerPreferences.publisherStyles
//            )

@Composable
fun <T> SettingsDropdown(
    title: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    Text(title, style = MaterialTheme.typography.titleMedium)
    Box {
        Text(
            selectedOption.toString(),
            modifier = Modifier
                .clickable { expanded = true }
                .padding(8.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        if (!enabled) {
                            Toast.makeText(
                                context,
                                "Cannot change settings on publisher styles",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            onOptionSelected(option)
                        }
                        expanded = false
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}
