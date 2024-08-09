package com.example.uread.presentation.bookReader.components.drawers

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.uread.presentation.bookReader.BookAnnotation
import com.example.uread.presentation.bookReader.Highlight
import com.example.uread.presentation.bookReader.Underline
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.shared.publication.Publication

@Composable
fun AnnotationsDrawer(
    navigator: EpubNavigatorFragment?,
    annotations: List<BookAnnotation>,
    onRemoveAnnotation: (BookAnnotation) -> Unit,
    onUpdateAnnotation: (BookAnnotation) -> Unit,
    isOpen: Boolean,
    onClose: () -> Unit,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Highlights", "Underlines")


    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ModalDrawerSheet(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
            ) {
                Column {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close Notes")
                        }
                        Text(
                            tabTitles[selectedTabIndex],
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    // Tab Row
                    TabRow(
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Annotation List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        val filteredAnnotations = annotations.filter {
                            when (selectedTabIndex) {
                                0 -> it is Highlight
                                1 -> it is Underline
                                else -> false
                            }
                        }
                        items(filteredAnnotations.size) { index ->
                            AnnotationItem(

                                annotation = filteredAnnotations[index],
                                onRemoveAnnotation = onRemoveAnnotation,
                                onUpdateAnnotation = onUpdateAnnotation,
                                navigatorFragment = navigator,
                                onClose = onClose
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnnotationItem(
    annotation: BookAnnotation,
    onRemoveAnnotation: (BookAnnotation) -> Unit,
    onUpdateAnnotation: (BookAnnotation) -> Unit,
    navigatorFragment: EpubNavigatorFragment?,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPaletteVisible by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf<Color?>(null) }
    val controller = rememberColorPickerController()

    fun goToAnnotation() {
        navigatorFragment?.let { navigator ->
            coroutineScope.launch {
                annotation.locator.let { locator ->
                    navigator.go(locator)
                    Toast.makeText(context, "Navigating to annotation", Toast.LENGTH_SHORT).show()
                    onClose() // Close the annotation drawer
                }
            }
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(elevation = 10.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = {
                    goToAnnotation()
                }
            )
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(annotation.color)
                .clickable(
                    onClick = {
                        isPaletteVisible = true
                        selectedColor = annotation.color
                    }
                )
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = annotation.note ?: "No note available",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Chapter: ${annotation.locator.title ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = { onRemoveAnnotation(annotation) }) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "Remove Annotation")
        }
    }
    if (isPaletteVisible) {
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
                    initialColor = selectedColor ?: Color.White,
                    onColorChanged = { colorEnvelope ->
                        selectedColor = colorEnvelope.color
                    }
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(selectedColor ?: Color.White)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        selectedColor?.let { newColor ->
                            val updatedAnnotation = when (annotation) {
                                is Highlight -> annotation.copy(color = newColor)
                                is Underline -> annotation.copy(color = newColor)
                            }
                            onUpdateAnnotation(updatedAnnotation)
                        }
                        isPaletteVisible = false
                    }
                ) {
                    Text("Select")
                }
            }
        }
    }
}
