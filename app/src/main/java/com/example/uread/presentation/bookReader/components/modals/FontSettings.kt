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
fun FontSettings(
    readerPreferences: ReaderPreferences,
    onPreferencesChanged: (ReaderPreferences) -> Unit,
    onDismiss: () -> Unit,
) {
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //can be changed on publisher styles
            SettingsRange(
                title = "Font Size",
                value = updatedPreferences.fontSize,
                onValueChange = { updatePreference { copy(fontSize = it) } },
                valueRange = minFontSize..maxFontSize,
                valueDisplay = { "${String.format(Locale.getDefault(), "%.0f", it * 100)}%" }
            )

            //cannot be changed on publisher styles
            SettingsRange(
                title = "Line Height",
                value = updatedPreferences.lineHeight,
                onValueChange = { updatePreference { copy(lineHeight = it) } },
                valueRange = 1.0..3.0,
                valueDisplay = { String.format(Locale.getDefault(), "%.1f", it) },
                enabled = !readerPreferences.publisherStyles
            )








            //cannot be changed on publisher styles
            SettingsRange(
                title = "Letter Spacing",
                value = updatedPreferences.letterSpacing,
                onValueChange = { updatePreference { copy(letterSpacing = it) } },
                valueRange = 0.0..1.0,
                valueDisplay = { String.format(Locale.getDefault(), "%.1f", it) },
                enabled = !readerPreferences.publisherStyles
            )

            //cannot be changed on publisher styles
            SettingsRange(
                title = "Word Spacing",
                value = updatedPreferences.wordSpacing,
                onValueChange = { updatePreference { copy(wordSpacing = it) } },
                valueRange = 0.0..3.0,
                valueDisplay = { String.format(Locale.getDefault(),"%.1f", it) },
                enabled = !readerPreferences.publisherStyles
            )


        }
    }
}


@OptIn(ExperimentalReadiumApi::class)
@Composable
fun SettingsRange(
    title: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    valueRange: ClosedFloatingPointRange<Double>,
    valueDisplay: (Double) -> String,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (!enabled) {
                    Toast.makeText(context, "Cannot change settings on publisher styles", Toast.LENGTH_SHORT).show()
                }else{
                    val newSize = (value - 0.1).coerceIn(valueRange.start, valueRange.endInclusive)
                    onValueChange(newSize)
                }

            }
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease $title")
        }
        Text(valueDisplay(value), style = MaterialTheme.typography.bodyLarge)
        IconButton(
            onClick = {
                if (!enabled) {
                    Toast.makeText(context, "Cannot change settings on publisher styles", Toast.LENGTH_SHORT).show()
                }else{
                    val newSize = (value + 0.1).coerceIn(valueRange.start, valueRange.endInclusive)
                    onValueChange(newSize)
                }
            }
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase $title")
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}


@Composable
fun SettingsSlider(
    title: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    valueRange: ClosedFloatingPointRange<Double>,
    valueDisplay: (Double) -> String
) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Slider(
        value = value.toFloat(),
        onValueChange = { onValueChange(it.toDouble()) },
        valueRange = valueRange.start.toFloat()..valueRange.endInclusive.toFloat()
    )
    Text(valueDisplay(value), style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(16.dp))
}

