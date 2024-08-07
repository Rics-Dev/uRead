package com.example.uread.presentation.bookReader

import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.uread.R
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.readium.r2.navigator.Decoration
import org.readium.r2.navigator.OverflowableNavigator
import org.readium.r2.navigator.SelectableNavigator
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
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

    val highlightColor by remember { mutableStateOf(Color.Yellow) }
    val underlineColor by remember { mutableStateOf(Color.Blue) }


    val annotations by viewModel.annotations.collectAsState()
    var currentLocator by remember { mutableStateOf<Locator?>(null) }

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

    fun handleHighlight() {
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
                        color = highlightColor,
                        note = selectedText ?: ""
                    )
                    viewModel.addAnnotation(newHighlight)
                }
                selectedText = null
            }
        }
    }

    fun handleUnderline() {
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
                        color = highlightColor,
                        note = selectedText ?: ""
                    )
                    viewModel.addAnnotation(newUnderline)
                }
                selectedText = null
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
                                ::handleHighlight,
                                ::handleUnderline
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.BottomCenter)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            showToolbar = !showToolbar
                        }
                    )
                }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.TopCenter)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            showToolbar = !showToolbar
                        }
                    )
                }
        )

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
            annotations = viewModel.annotations.collectAsState().value,
            onRemoveAnnotation = { annotation ->
                viewModel.removeAnnotation(annotation.id)
            },
            isOpen = isHighlightsDrawerOpen,
            onClose = { isHighlightsDrawerOpen = false }
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



