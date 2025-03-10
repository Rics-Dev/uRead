package com.ricdev.uread.presentation.bookReader.components

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.ricdev.uread.data.model.AnnotationType
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.navigation.Screens
import com.ricdev.uread.presentation.bookReader.BookReaderViewModel

@Composable
fun TextToolbar(
    navController: NavHostController,
    viewModel: BookReaderViewModel,
    selectedText: String?,
    rect: Rect,
    onHighlight: (Color) -> Unit,
    onUnderline: (Color) -> Unit,
    onNote: () -> Unit,
    onDismiss: () -> Unit,
    appPreferences: AppPreferences,
    selectedAnnotation: BookAnnotation?,
    onRemoveAnnotation: (BookAnnotation) -> Unit,
    colorHistory: List<Color>,
    onColorHistoryUpdated: (List<Color>) -> Unit,
    showColorSelectionPanel: Boolean
) {
    val context = LocalContext.current
    var showHighlightAction by remember { mutableStateOf(false) }
    var showUnderlineAction by remember { mutableStateOf(false) }
    var isPaletteVisible by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf<Color?>(null) }
    var showTranslationDialog by remember { mutableStateOf(false) }
    var showDefinitionDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    val density = LocalDensity.current
    val screenWidthPx = with(density) { screenWidthDp.toPx() }
    val screenHeightPx = with(density) { screenHeightDp.toPx() }

    val toolbarWidth = 250.dp
    val toolbarWidthPx = with(density) { toolbarWidth.toPx() }

    val toolbarHeight =
        if (showHighlightAction || showUnderlineAction || showColorSelectionPanel) 260f else 160f

    val offsetX = calculateOffsetX(rect, screenWidthPx, toolbarWidthPx)
    val isNearTop = rect.top < toolbarHeight
    val targetOffsetY = if (isNearTop) {
        minOf(rect.bottom + 10f, screenHeightPx - toolbarHeight)
    } else {
        maxOf(rect.top - toolbarHeight - 10f, 0f)
    }

    val animatedOffsetY by animateFloatAsState(
        targetValue = targetOffsetY,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = ""
    )

    val controller = rememberColorPickerController()

    fun openGoogleSearch(word: String) {
        val searchUrl = "https://www.google.com/search?q=define:$word"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl))
        context.startActivity(Intent.createChooser(intent, "Search with"))
    }

    fun openGoogleTranslate(selectedText: String) {
        val translateUrl =
            "https://translate.google.com/?hl=fr&sl=auto&tl=fr&text=$selectedText&op=translate"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(translateUrl))
        context.startActivity(Intent.createChooser(intent, "search with"))
    }




    Box(
        modifier = Modifier.offset {
            IntOffset(
                offsetX.toInt(),
                animatedOffsetY.toInt()
            )
        }
    ) {
        Card(modifier = Modifier.width(toolbarWidth)) {
            Column {
                AnimatedVisibility(visible = !showHighlightAction && !showUnderlineAction && !showColorSelectionPanel) {
                    ActionButtons(
                        selectedText = selectedText,
                        onHighlight = { showHighlightAction = true },
                        onUnderline = { showUnderlineAction = true },
                        onNote = onNote,
                        onTranslate = {
//                            showTranslationDialog = true
                            if (selectedText != null) {
                                openGoogleTranslate(selectedText)
                            }
                        },
                        onDefinition = {
//                            showDefinitionDialog = true
                            val firstWord = selectedText?.split("\\s+".toRegex())?.firstOrNull()
                            if (firstWord != null) {
                                openGoogleSearch(firstWord)
                            }
                        }
                    )
                }

                AnimatedVisibility(visible = showHighlightAction || showUnderlineAction || showColorSelectionPanel) {
                    ColorSelectionPanel(
                        selectedAnnotation = selectedAnnotation,
                        onRemoveAnnotation = onRemoveAnnotation,
                        onCustomColorClick = {
                            if (appPreferences.isPremium) isPaletteVisible = true
                            else {
                                navController.navigate(Screens.PremiumScreen.route)
                            }
                        },
                        onColorSelected = { color ->
                            selectedColor = color
                            handleColorSelection(
                                color,
                                showHighlightAction = showHighlightAction || (showColorSelectionPanel && selectedAnnotation?.type == AnnotationType.HIGHLIGHT),
                                showUnderlineAction = showUnderlineAction || (showColorSelectionPanel && selectedAnnotation?.type == AnnotationType.UNDERLINE),
                                onHighlight = {
                                    if (showColorSelectionPanel) {
                                        viewModel.updateAnnotation(
                                            selectedAnnotation!!.copy(
                                                color = color.toArgb().toString()
                                            )
                                        )
                                    } else {
                                        onHighlight(color)
                                    }
                                },
                                onUnderline = {
                                    if (showColorSelectionPanel) {
                                        viewModel.updateAnnotation(
                                            selectedAnnotation!!.copy(
                                                color = color.toArgb().toString()
                                            )
                                        )
                                    } else {
                                        onUnderline(color)
                                    }
                                },
                                colorHistory,
                                onColorHistoryUpdated
                            )
                        },
                        onBackClick = {
                            showHighlightAction = false
                            showUnderlineAction = false
                            if (showColorSelectionPanel) {
                                onDismiss()
                            }
                        },
                        colorHistory = colorHistory
                    )
                }
            }
        }
    }

    if (isPaletteVisible && appPreferences.isPremium) {
        ColorPickerOverlay(
            selectedColor = selectedColor,
            controller = controller,
            onColorChanged = { selectedColor = it },
            onColorSelected = { color ->
                handleColorSelection(
                    color,
                    showHighlightAction = showHighlightAction || (showColorSelectionPanel && selectedAnnotation?.type == AnnotationType.HIGHLIGHT),
                    showUnderlineAction = showUnderlineAction || (showColorSelectionPanel && selectedAnnotation?.type == AnnotationType.UNDERLINE),
                    onHighlight = {
                        if (showColorSelectionPanel) {
                            viewModel.updateAnnotation(
                                selectedAnnotation!!.copy(
                                    color = color.toArgb().toString()
                                )
                            )
                        } else {
                            onHighlight(color)
                        }
                    },
                    onUnderline = {
                        if (showColorSelectionPanel) {
                            viewModel.updateAnnotation(
                                selectedAnnotation!!.copy(
                                    color = color.toArgb().toString()
                                )
                            )
                        } else {
                            onUnderline(color)
                        }
                    },
                    colorHistory,
                    onColorHistoryUpdated
                )
                isPaletteVisible = false
            }
        )
    }

    if (showTranslationDialog && selectedText != null) {
        TranslationDialog(
            text = selectedText,
            onDismiss = { showTranslationDialog = false }
        )
    }

    if (showDefinitionDialog && selectedText != null) {
        val firstWord = selectedText.split("\\s+".toRegex()).firstOrNull()
        if (firstWord != null) {
            DefinitionDialog(
                word = firstWord,
                onDismiss = { showDefinitionDialog = false }
            )
        }
    }

}

@Composable
fun ActionButtons(
    selectedText: String?,
    onHighlight: () -> Unit,
    onUnderline: () -> Unit,
    onNote: () -> Unit,
    onTranslate: () -> Unit,
    onDefinition: () -> Unit
) {
    val isSingleWord = selectedText?.split("\\s+".toRegex())?.size == 1


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onHighlight) {
            Icon(Icons.Outlined.BorderColor, contentDescription = "Highlight")
        }
        VerticalDivider()
        IconButton(onClick = onUnderline) {
            Icon(Icons.Outlined.FormatUnderlined, contentDescription = "Underline")
        }
        VerticalDivider()
        IconButton(onClick = onNote) {
            Icon(Icons.Outlined.EditNote, contentDescription = "Add Note")
        }
        VerticalDivider()
        IconButton(onClick = onTranslate) {
            Icon(Icons.Outlined.Translate, contentDescription = "Translate")
        }
        AnimatedVisibility(
            visible = isSingleWord
        ) {
            VerticalDivider()
            IconButton(onClick = onDefinition) {
                Icon(Icons.Default.Search, contentDescription = "Definition")
            }
        }
    }
}


@Composable
fun ColorSelectionPanel(
    selectedAnnotation: BookAnnotation?,
    onRemoveAnnotation: (BookAnnotation) -> Unit,
    onCustomColorClick: () -> Unit,
    onColorSelected: (Color) -> Unit,
    onBackClick: () -> Unit,
    colorHistory: List<Color>
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = {
                    selectedAnnotation?.let {
                        onRemoveAnnotation(it)
                    }
                },
                enabled = selectedAnnotation != null,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete Annotation",
                    modifier = Modifier.size(24.dp),
                    tint = if (selectedAnnotation != null) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.38f)
                )
            }
            IconButton(
                onClick = onCustomColorClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Colorize,
                    contentDescription = "Custom Color",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        DefaultColors(
            onColorSelected = onColorSelected,
            colorHistory = colorHistory
        )
    }
}

@Composable
fun DefaultColors(
    onColorSelected: (Color) -> Unit,
    colorHistory: List<Color>
) {
    val defaultColors = listOf(
        Color(0xFF4CAF50), // Material Green
        Color(0xFFFFEB3B), // Material Yellow
        Color(0xFF2196F3), // Material Blue
        Color(0xFFE91E63), // Material Pink
        Color(0xFF9C27B0), // Material Purple
    )

    val colorsToShow = if (colorHistory.isNotEmpty()) {
        (colorHistory + defaultColors).distinct().take(10)
    } else {
        defaultColors
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = Int.MAX_VALUE,
            key = { index -> index }
        ) { index ->
            val color = colorsToShow[index % colorsToShow.size]
            ColorButton(color = color, onClick = { onColorSelected(color) })
        }
    }
}

@Composable
fun ColorButton(color: Color, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        ),
        modifier = Modifier.size(32.dp),
        interactionSource = interactionSource
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Outer circle
                drawCircle(
                    color = color,
                    radius = size.minDimension / 2
                )

                // Inner circle (slightly darker shade for depth)
                drawCircle(
                    color = color.copy(alpha = 0.85f),
                    radius = size.minDimension / 2.5f
                )
            }
        }
    }
}

fun calculateOffsetX(rect: Rect, screenWidth: Float, toolbarWidth: Float): Float {
    val center = rect.left + (rect.right - rect.left) / 2
    var offsetX = center - toolbarWidth / 2

    // Adjust if the toolbar goes beyond the left edge
    if (offsetX < 0) {
        offsetX = 0f
    }

    // Adjust if the toolbar goes beyond the right edge
    if (offsetX + toolbarWidth > screenWidth) {
        offsetX = screenWidth - toolbarWidth
    }

    return offsetX
}

private fun handleColorSelection(
    color: Color,
    showHighlightAction: Boolean,
    showUnderlineAction: Boolean,
    onHighlight: (Color) -> Unit,
    onUnderline: (Color) -> Unit,
    colorHistory: List<Color>,
    onColorHistoryUpdated: (List<Color>) -> Unit
) {
    val updatedHistory = (listOf(color) + colorHistory).distinct().take(1)
    onColorHistoryUpdated(updatedHistory)
    if (showHighlightAction) onHighlight(color)
    if (showUnderlineAction) onUnderline(color)
}

@Composable
fun ColorPickerOverlay(
    selectedColor: Color?,
    controller: ColorPickerController,
    onColorChanged: (Color) -> Unit,
    onColorSelected: (Color) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        ) {
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(350.dp)
                    .padding(10.dp),
                controller = controller,
                initialColor = selectedColor ?: Color.White,
                onColorChanged = { colorEnvelope -> onColorChanged(colorEnvelope.color) }
            )

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(selectedColor ?: Color.White)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onColorSelected(selectedColor ?: Color.White) }) {
                Text("Select")
            }
        }
    }
}


@Composable
fun TranslationDialog(
    text: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Translation") },
        text = { Text("Translated text: [API call result for '$text']") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefinitionDialog(
    word: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val sheetMaxWidth = screenWidthDp * 0.95f

    fun openGoogleSearch() {
        val searchUrl = "https://www.google.com/search?q=define:$word"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl))
        context.startActivity(Intent.createChooser(intent, "Search with"))
    }

    ModalBottomSheet(
        dragHandle = null,
        modifier = Modifier.padding(bottom = 48.dp),
        shape = RoundedCornerShape(16.dp),
        sheetMaxWidth = sheetMaxWidth.dp,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "search")
                Text("Definition of '$word'", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { openGoogleSearch() }) {
                Text("Search in Google")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    }
}


@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(50.dp)
            .background(Color(0xFFB9B9B9))
    )
}