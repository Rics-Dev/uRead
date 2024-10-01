package com.ricdev.uread.presentation.bookReader.components.modals


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.data.model.ReaderPreferences
import com.ricdev.uread.presentation.bookReader.BookReaderViewModel
import org.readium.r2.shared.ExperimentalReadiumApi
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalReadiumApi::class)
@Composable
fun FontSettings(
    viewModel: BookReaderViewModel,
    readerPreferences: ReaderPreferences,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    val minFontSize = 0.5
    val maxFontSize = 2.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.font_settings),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                TextButton(
                    onClick = {
                        viewModel.resetFontPreferences()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)

                ) {
                    Text(
                        text = stringResource(R.string.reset),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            //can be changed on publisher styles
            SettingsRange(
                title = stringResource(R.string.font_size),
                value = readerPreferences.fontSize,
                onValueChange = { viewModel.updateReaderPreferences( readerPreferences.copy(fontSize = it) ) },
                valueRange = minFontSize..maxFontSize,
                valueDisplay = { "${String.format(Locale.getDefault(), "%.0f", it * 100)}%" }
            )

            //cannot be changed on publisher styles
            SettingsRange(
                title = stringResource(R.string.line_height),
                value = readerPreferences.lineHeight,
                onValueChange = { viewModel.updateReaderPreferences( readerPreferences.copy(lineHeight = it) ) },
                valueRange = 1.0..3.0,
                valueDisplay = { String.format(Locale.getDefault(), "%.1f", it) },
                enabled = !readerPreferences.publisherStyles
            )


            //cannot be changed on publisher styles
            SettingsRange(
                title = stringResource(R.string.letter_spacing),
                value = readerPreferences.letterSpacing,
                onValueChange = { viewModel.updateReaderPreferences( readerPreferences.copy(letterSpacing = it) ) },
                valueRange = 0.0..1.0,
                valueDisplay = { String.format(Locale.getDefault(), "%.1f", it) },
                enabled = !readerPreferences.publisherStyles
            )

            //cannot be changed on publisher styles
            SettingsRange(
                title = stringResource(R.string.word_spacing),
                value = readerPreferences.wordSpacing,
                onValueChange = { viewModel.updateReaderPreferences( readerPreferences.copy(wordSpacing = it) ) },
                valueRange = 0.0..3.0,
                valueDisplay = { String.format(Locale.getDefault(), "%.1f", it) },
                enabled = !readerPreferences.publisherStyles
            )
        }
    }
}


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
                    Toast.makeText(
                        context,
                        "Cannot change settings on publisher styles",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
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
                    Toast.makeText(
                        context,
                        "Cannot change settings on publisher styles",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
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

