package com.ricdev.uread.presentation.bookReader.components.drawers

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ricdev.uread.data.model.AnnotationType
import com.ricdev.uread.data.model.BookAnnotation
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.presentation.bookReader.BookReaderViewModel
//import com.ricsdev.uread.presentation.sharedComponents.PremiumModal
import com.ricdev.uread.util.PurchaseHelper
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.shared.publication.Locator

@Composable
fun AnnotationsDrawer(
    viewModel: BookReaderViewModel,
    purchaseHelper: PurchaseHelper,
    appPreferences: AppPreferences,
    navigator: EpubNavigatorFragment?,
    annotations: List<BookAnnotation>,
    onRemoveAnnotation: (BookAnnotation) -> Unit,
    onUpdateAnnotation: (BookAnnotation) -> Unit,
    isOpen: Boolean,
    onClose: () -> Unit,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(stringResource(R.string.highlights), stringResource(R.string.underlines))
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
                    // Header
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
                        Text(
                            tabTitles[selectedTabIndex],
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    // Tab Row
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


                    // Annotation List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        val filteredAnnotations = annotations.filter {
                            when (selectedTabIndex) {
                                0 -> it.type == AnnotationType.HIGHLIGHT
                                1 -> it.type == AnnotationType.UNDERLINE
                                else -> false
                            }
                        }
                        items(filteredAnnotations.size) { index ->
                            AnnotationItem(
                                appPreferences = appPreferences,
                                annotation = filteredAnnotations.reversed()[index],
                                onRemoveAnnotation = onRemoveAnnotation,
                                onUpdateAnnotation = onUpdateAnnotation,
                                navigatorFragment = navigator,
                                onClose = onClose,
                                showPremiumModal = {
//                                    showPremiumModal = true
                                    viewModel.purchasePremium(purchaseHelper)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
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
fun AnnotationItem(
    appPreferences: AppPreferences,
    annotation: BookAnnotation,
    onRemoveAnnotation: (BookAnnotation) -> Unit,
    onUpdateAnnotation: (BookAnnotation) -> Unit,
    navigatorFragment: EpubNavigatorFragment?,
    onClose: () -> Unit,
    showPremiumModal: () -> Unit,

) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPaletteVisible by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(Color(annotation.color.toIntOrNull() ?: Color.Yellow.toArgb())) }
    val controller = rememberColorPickerController()

    fun goToAnnotation() {
        navigatorFragment?.let { navigator ->
            coroutineScope.launch {
                val locator = Locator.fromJSON(JSONObject(annotation.locator))
                locator?.let {
                    navigator.go(it)
                    Toast.makeText(context, "Navigating to annotation", Toast.LENGTH_SHORT).show()
                    onClose() // Close the annotation drawer
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                clip = true
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = ::goToAnnotation)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    annotation.color
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

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = annotation.note ?: stringResource(R.string.no_note_available),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = Locator.fromJSON(JSONObject(annotation.locator))?.title ?: "Unknown",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = { onRemoveAnnotation(annotation) }) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "Remove Annotation")
        }
    }

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
                        val updatedAnnotation = annotation.copy(color = selectedColor.toArgb().toString())
                        onUpdateAnnotation(updatedAnnotation)
                        isPaletteVisible = false
                    }
                ) {
                    Text(stringResource(R.string.select))
                }
            }
        }
    }
}
