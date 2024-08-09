package com.example.uread.presentation.bookReader

import android.view.View
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.uread.data.model.ReaderPreferences
import com.example.uread.presentation.bookReader.components.drawers.AnnotationsDrawer
import com.example.uread.presentation.bookReader.components.toolbars.BottomToolbar
import com.example.uread.presentation.bookReader.components.toolbars.TopToolbar
import com.example.uread.presentation.bookReader.components.drawers.ChaptersDrawer
import com.example.uread.presentation.bookReader.components.modals.FontSettings
import com.example.uread.presentation.bookReader.components.drawers.NotesDrawer
import com.example.uread.presentation.bookReader.components.modals.PageSettings
import com.example.uread.presentation.bookReader.components.modals.ReaderSettings
import com.example.uread.presentation.bookReader.components.modals.UiSettings
import com.example.uread.presentation.bookReader.util.SelectionActionModeCallback
import com.example.uread.util.SetFullScreen
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch
import org.readium.r2.navigator.Decoration
import org.readium.r2.navigator.OverflowableNavigator
import org.readium.r2.navigator.VisualNavigator
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.input.DragEvent
import org.readium.r2.navigator.input.InputListener
import org.readium.r2.navigator.input.TapEvent
import org.readium.r2.navigator.util.DirectionalNavigationAdapter
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.locateProgression
import java.util.UUID

@OptIn(ExperimentalReadiumApi::class)
@Composable
fun BookReaderScreen(viewModel: BookReaderViewModel = hiltViewModel()) {
    val context = LocalContext.current
    SetFullScreen(context)

    val uiState by viewModel.uiState.collectAsState()
    val initialLocator by viewModel.initialLocator.collectAsState()

    val readerPreferences by viewModel.readerPreferences.collectAsState()
    val epubPreferences by viewModel.epubPreferences.collectAsState()


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is BookReaderUiState.Loading -> CircularProgressIndicator(color = Color.Black)
            is BookReaderUiState.Error -> Text((uiState as BookReaderUiState.Error).message)
            is BookReaderUiState.Success -> {
                val successState = uiState as BookReaderUiState.Success
                EpubReaderView(
                    publication = successState.publication,
                    onLocatorChange = { locator ->
                        viewModel.saveReadingProgress(locator)
                    },
                    initialLocator = initialLocator,
                    readerPreferences = readerPreferences,
                    epubPreferences = epubPreferences,
                    viewModel = viewModel,
                )
            }
        }
    }
}


@OptIn(ExperimentalReadiumApi::class)
@Composable
fun EpubReaderView(
    publication: Publication,
    initialLocator: Locator?,
    onLocatorChange: (Locator) -> Unit,
    readerPreferences: ReaderPreferences,
    epubPreferences: EpubPreferences,
    viewModel: BookReaderViewModel,
) {
    val fragmentActivity = LocalContext.current as FragmentActivity
    var navigatorFragment by remember { mutableStateOf<EpubNavigatorFragment?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var showToolbar by remember { mutableStateOf(false) }
    var showReaderSettings by remember { mutableStateOf(false) }
    var showFontSettings by remember { mutableStateOf(false) }
    var showPageSettings by remember { mutableStateOf(false) }
    var showUISettings by remember { mutableStateOf(false) }
    var isChaptersDrawerOpen by remember { mutableStateOf(false) }
    var isNotesDrawerOpen by remember { mutableStateOf(false) }
    var isHighlightsDrawerOpen by remember { mutableStateOf(false) }

    var progression by remember { mutableDoubleStateOf(1.0) }
    var currentChapter by remember { mutableStateOf("") }
    var selectedText by remember { mutableStateOf<String?>(null) }

    var currentDirectionalNavigationAdapter by remember {
        mutableStateOf<DirectionalNavigationAdapter?>(null)
    }

    val annotations by viewModel.annotations.collectAsState()
    var currentLocator by remember { mutableStateOf<Locator?>(null) }

    var showActionMode by remember { mutableStateOf(false) }
    var isHighlightColorPicker by remember { mutableStateOf(true) }
    var onColorSelected by remember { mutableStateOf<(Color) -> Unit>({}) }

    fun showActionMode(isHighlight: Boolean, onSelected: (Color) -> Unit) {
        isHighlightColorPicker = isHighlight
        onColorSelected = onSelected
        showToolbar = false
        showActionMode = true
    }

    fun onPageChange(newPage: Double) {
        navigatorFragment?.let { navigator ->
            coroutineScope.launch {
                val locator = publication.locateProgression(newPage)
                locator?.let {
                    navigator.go(locator)
                }
            }
        }
    }


    fun handleHighlight(color: Color) {
        coroutineScope.launch {
            val selection = navigatorFragment?.currentSelection()
            if (selection != null) {
                val locator = selection.locator
                selectedText = locator.text.highlight.toString()

                val existingAnnotation = viewModel.annotations.value.find { it.locator == locator }
                if (existingAnnotation is Highlight) {
                    viewModel.removeAnnotation(existingAnnotation.id)
                } else {
                    val newHighlight = Highlight(
                        id = UUID.randomUUID().toString(),
                        locator = locator,
                        color = color,
                        note = selectedText ?: ""
                    )
                    viewModel.addAnnotation(newHighlight)
                }
                selectedText = null
            }
        }
    }

    fun handleUnderline(color: Color) {
        coroutineScope.launch {
            val selection = navigatorFragment?.currentSelection()
            if (selection != null) {
                val locator = selection.locator
                selectedText = locator.text.highlight.toString()

                val existingAnnotation = viewModel.annotations.value.find { it.locator == locator }
                if (existingAnnotation is Underline) {
                    viewModel.removeAnnotation(existingAnnotation.id)
                } else {
                    val newUnderline = Underline(
                        id = UUID.randomUUID().toString(),
                        locator = locator,
                        color = color,
                        note = selectedText ?: ""
                    )
                    viewModel.addAnnotation(newUnderline)
                }
                selectedText = null
            }
        }
    }



    // Set up the selection listener for the EpubNavigatorFragment
    LaunchedEffect(navigatorFragment) {
        navigatorFragment?.let { navigator ->
            (navigator as? VisualNavigator)?.apply {
                addInputListener(object : InputListener {
                    override fun onTap(event: TapEvent): Boolean {
                        // Toggle the toolbar visibility
                        showToolbar = !showToolbar
                        return true
                    }

                    override fun onDrag(event: DragEvent): Boolean {
                        // Handle drag events if needed
                        return false
                    }
                })
            }
        }
    }



    LaunchedEffect(navigatorFragment) {
        navigatorFragment?.let { navigator ->
            navigator.currentLocator.collect { locator ->
                onLocatorChange(locator)
                progression = locator.locations.totalProgression ?: 0.0
                currentChapter = locator.title ?: ""
                currentLocator = locator
            }
        }
    }

    LaunchedEffect(annotations) {
        navigatorFragment?.let { navigator ->
            if (navigator.isAdded) {
                // Remove all existing decorations first
                navigator.applyDecorations(emptyList(), "user-annotations")

                // Then apply the current list of annotations
                navigator.applyDecorations(
                    annotations.map { annotation ->
                        when (annotation) {
                            is Highlight -> Decoration(
                                id = annotation.id,
                                locator = annotation.locator,
                                style = Decoration.Style.Highlight(tint = annotation.color.toArgb())
                            )
                            is Underline -> Decoration(
                                id = annotation.id,
                                locator = annotation.locator,
                                style = Decoration.Style.Underline(tint = annotation.color.toArgb())
                            )
                        }
                    },
                    "user-annotations"
                )
            }
        }
    }

    LaunchedEffect(readerPreferences.tapNavigation) {
        navigatorFragment?.let { navigator ->
            if (navigator.isAdded) {
                (navigator as? OverflowableNavigator)?.let { overflowableNavigator ->
                    if (readerPreferences.tapNavigation) {
                        currentDirectionalNavigationAdapter?.let { adapter ->
                            overflowableNavigator.removeInputListener(adapter)
                        }
                        val newAdapter = DirectionalNavigationAdapter(overflowableNavigator)
                        overflowableNavigator.addInputListener(newAdapter)
                        currentDirectionalNavigationAdapter = newAdapter
                    } else {
                        currentDirectionalNavigationAdapter?.let { adapter ->
                            overflowableNavigator.removeInputListener(adapter)
                        }
                        currentDirectionalNavigationAdapter = null
                    }
                }
            }
        }
    }






    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                FrameLayout(context).apply {
                    id = View.generateViewId()
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                if (navigatorFragment == null) {
                    val factory = EpubNavigatorFactory(
                        publication = publication,
                        configuration = EpubNavigatorFactory.Configuration(
                            defaults = EpubDefaults(
                                pageMargins = epubPreferences.pageMargins,
                                fontSize = epubPreferences.fontSize,
                                fontWeight = epubPreferences.fontWeight,
                                scroll = epubPreferences.scroll
                            )
                        )
                    )

                    val fragment = factory.createFragmentFactory(
                        initialLocator = initialLocator,
                        listener = null,
                        configuration = EpubNavigatorFragment.Configuration(
                            selectionActionModeCallback = SelectionActionModeCallback(
                                onHighlight = { handleHighlight(it) },
                                onUnderline = { handleUnderline(it) },
                                showActionMode = ::showActionMode
                            )
                        )
                    ).instantiate(
                        fragmentActivity.classLoader,
                        EpubNavigatorFragment::class.java.name
                    )

                    navigatorFragment = fragment as EpubNavigatorFragment

                    fragmentActivity.supportFragmentManager.beginTransaction()
                        .replace(view.id, fragment)
                        .commitAllowingStateLoss()
                }
                navigatorFragment?.let { navigator ->
                    if (navigator.isAdded) {
                        navigator.submitPreferences(epubPreferences)
                    }
                }
            }
        )



        // ActionModeLayout
        if (showActionMode || isHighlightsDrawerOpen || isChaptersDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showActionMode = false
                        isNotesDrawerOpen = false
                        isHighlightsDrawerOpen = false
                        isChaptersDrawerOpen = false
                    }
            )
        }

        AnimatedVisibility(
            visible = showActionMode,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            ActionModeLayout(
                onColorSelected = onColorSelected
            )
        }


        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomToolbar(
                navigatorFragment = navigatorFragment,
                showToolbar = showToolbar,
                progression = progression,
                onPageChange = ::onPageChange,
                onToggleFontSettings = { showFontSettings = true },
                onTogglePageSettings = { showPageSettings = true },
                onToggleReaderSettings = { showReaderSettings = true },
                onToggleUISettings = { showUISettings = true }
            )
        }

        TopToolbar(
            showToolbar,
            publication,
            fragmentActivity,
            currentChapter = currentChapter,
            onChaptersClick = { isChaptersDrawerOpen = true },
            onNotesDrawerToggle = { isNotesDrawerOpen = true },
            onHighlightsDrawerToggle = { isHighlightsDrawerOpen = true }
        )

        ChaptersDrawer(
            isOpen = isChaptersDrawerOpen,
            currentChapter = currentChapter,
            tableOfContents = publication.tableOfContents,
            onChapterSelect = { selectedChapter ->
                coroutineScope.launch {
                    navigatorFragment?.let { navigator ->
                        val locator = publication.locatorFromLink(selectedChapter)
                        locator?.let {
                            navigator.go(it)
                            isChaptersDrawerOpen = false
                        }
                    }
                }
            },
            onClose = { isChaptersDrawerOpen = false }
        )

        NotesDrawer(
            isOpen = isNotesDrawerOpen,
            onClose = { isNotesDrawerOpen = false }
        )

        AnnotationsDrawer(
            navigator = navigatorFragment,
            annotations = viewModel.annotations.collectAsState().value,
            onRemoveAnnotation = { annotation ->
                viewModel.removeAnnotation(annotation.id)
            },
            isOpen = isHighlightsDrawerOpen,
            onClose = { isHighlightsDrawerOpen = false },
            onUpdateAnnotation = viewModel::onUpdateAnnotation
        )




        if (showFontSettings) {
            FontSettings(
                readerPreferences = readerPreferences,
                onPreferencesChanged = { newPreferences ->
                    viewModel.updateReaderPreferences(newPreferences)
                },
                onDismiss = { showFontSettings = false },
            )
        }

        if (showPageSettings) {
            PageSettings(
                readerPreferences = readerPreferences,
                onPreferencesChanged = { newPreferences ->
                    viewModel.updateReaderPreferences(newPreferences)
                },
                onDismiss = { showPageSettings = false },
            )
        }

        if (showUISettings) {
            UiSettings(
                readerPreferences = readerPreferences,
                onPreferencesChanged = { newPreferences ->
                    viewModel.updateReaderPreferences(newPreferences)
                },
                onDismiss = { showUISettings = false }
            )
        }

        if (showReaderSettings) {
            ReaderSettings(
                readerPreferences = readerPreferences,
                onPreferencesChanged = { newPreferences ->
                    viewModel.updateReaderPreferences(newPreferences)
                },
                onDismiss = { showReaderSettings = false }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            navigatorFragment?.let { fragment ->
                fragmentActivity.supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }
    }
}








@Composable
fun ActionModeLayout(
    onColorSelected: (Color) -> Unit
) {
    var selectedColor by remember { mutableStateOf<Color?>(null) }  // Track the selected color
    var isPaletteVisible by remember { mutableStateOf(false) }  // Track the visibility of the color picker

    // Create a controller for the color picker
    val controller = rememberColorPickerController()

    // Main container for the layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(9999f)  // Ensure the layout is on top of other UI elements
            .padding(bottom = 6.dp),
        contentAlignment = Alignment.BottomCenter  // Position at the bottom center of the screen
    ) {
        // Darken the background when the palette is visible
        if (isPaletteVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))  // Semi-transparent black background
            )
        }

        // Main column for action buttons and color picker
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 16.dp)
        ) {
            // Row containing color options and custom color button
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.DarkGray  // Background color of the action mode
                    )
                    .padding(0.dp),
                verticalAlignment = Alignment.CenterVertically  // Align content vertically centered
            ) {
                // Group of radio buttons for selecting predefined colors
                ColorRadioGroup(
                    onColorSelected = { color ->
                        selectedColor = color  // Update the selected color
                        onColorSelected(color)  // Notify parent composable of the color change
                    }
                )

                // Divider between color options and the custom color button
                VerticalDivider()

                // Button to open the custom color picker
                CustomColor(onClick = {
                    isPaletteVisible = true  // Show the color picker when clicked
                })

                // Optional: Add other actions like a delete button here
                // VerticalDivider()
                // DeleteButton()
            }
        }

        // Conditional rendering of the color picker based on `isPaletteVisible`
        if (isPaletteVisible) {
            Box(
                modifier = Modifier.fillMaxSize(),  // Fill the screen size
                contentAlignment = Alignment.Center  // Center the color picker in the screen
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.wrapContentSize()
                ) {
                    // HSV color picker
                    HsvColorPicker(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)  // Optional: Adjust the width to 80% of the screen width
                            .height(350.dp)
                            .padding(10.dp),  // Layout settings for the color picker
                        controller = controller,
                        initialColor = selectedColor ?: Color.White,  // Set the initial color for the picker
                        onColorChanged = { colorEnvelope ->
                            val color = colorEnvelope.color
                            selectedColor = color  // Update the selected color
                        }
                    )


                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(selectedColor ?: Color.White)
                    )
                    Spacer(modifier = Modifier.height(16.dp))  // Spacer to add space between the color picker and the button
                    // Select button
                    Button(
                        onClick = {
                            onColorSelected(selectedColor ?: Color.White)  // Notify parent composable of the color change
                            isPaletteVisible = false  // Hide the color picker
                        }
                    ) {
                        Text("Select")  // Button label
                    }
                }
            }
        }
    }
}



@Composable
fun ColorRadioGroup(onColorSelected: (Color) -> Unit) {
    val colors = listOf(
        Color(0xFF6DBA70),
        Color(0xFFFFF176),
        Color(0xFF618CFF),
        Color(0xFFFF6B6B),
    )
    Row(
        modifier = Modifier
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        colors.forEach { color ->
            RadioButton(
                selected = true,
                onClick = { onColorSelected(color) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = color,
                    unselectedColor = color
                )
            )
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

@Composable
fun CustomColor(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Colorize,
            contentDescription = "Custom Color",
            tint = Color.LightGray
        )
    }
}

@Composable
fun DeleteButton() {
    IconButton(
        onClick = { /* Your click handler */ },
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}























@Composable
fun AnnotationColorPicker(
    isHighlight: Boolean,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = listOf(
        Color.Green, Color.Yellow,  Color.Blue, Color.Red,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Choose ${if (isHighlight) "Highlight" else "Underline"} Color") },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()), // Allow horizontal scrolling
                horizontalArrangement = Arrangement.SpaceEvenly // Space colors evenly
            ) {
                colors.forEach { color ->
                    ColorBoxAnnotation(
                        color = color,
                        onColorSelected = { selectedColor -> onColorSelected(selectedColor) },
                        onDismiss = onDismiss
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ColorBoxAnnotation(
    color: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .shadow(elevation = 200.dp)
            .clip(shape = RoundedCornerShape(50.dp))
            .background(color)
            .clickable {
                onColorSelected(color)
                onDismiss()
            }
    )
}




sealed class BookAnnotation(
    open val id: String, // Add this line
    open val locator: Locator,
    open val color: Color,
    open val note: String? = null
)

data class Highlight(
    override val id: String, // Add this line
    override val locator: Locator,
    override val color: Color,
    override val note: String? = null
) : BookAnnotation(id, locator, color, note)

data class Underline(
    override val id: String, // Add this line
    override val locator: Locator,
    override val color: Color,
    override val note: String? = null
) : BookAnnotation(id, locator, color, note)



