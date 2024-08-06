package com.example.uread.presentation.bookReader

import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.uread.data.model.ReaderPreferences
import com.example.uread.presentation.bookReader.components.toolbars.BottomToolbar
import com.example.uread.presentation.bookReader.components.toolbars.TopToolbar
import com.example.uread.presentation.bookReader.components.drawers.ChaptersDrawer
import com.example.uread.presentation.bookReader.components.modals.FontSettings
import com.example.uread.presentation.bookReader.components.drawers.HighlightsDrawer
import com.example.uread.presentation.bookReader.components.drawers.NotesDrawer
import com.example.uread.presentation.bookReader.components.modals.ReaderSettings
import com.example.uread.presentation.bookReader.components.modals.UiSettings
import com.example.uread.util.SetFullScreen
import kotlinx.coroutines.launch
import org.readium.r2.navigator.OverflowableNavigator
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.util.DirectionalNavigationAdapter
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.locateProgression

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
            is BookReaderUiState.Loading -> CircularProgressIndicator()
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
                    viewModel = viewModel
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
    var showUISettings by remember { mutableStateOf(false) }
    var isChaptersDrawerOpen by remember { mutableStateOf(false) }
    var isNotesDrawerOpen by remember { mutableStateOf(false) }
    var isHighlightsDrawerOpen by remember { mutableStateOf(false) }


    // Custom state variables
    var progression by remember { mutableDoubleStateOf(1.0) }
    var currentChapter by remember { mutableStateOf("") }



    LaunchedEffect(navigatorFragment) {
        navigatorFragment?.let { navigator ->
            navigator.currentLocator.collect { locator ->
                onLocatorChange(locator)
                progression = locator.locations.totalProgression ?: 0.0
                currentChapter = locator.title ?: ""

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

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        AndroidView(
            factory = { context ->
                FrameLayout(context).apply {
                    id = View.generateViewId()
                }
            },
            modifier = Modifier
                .fillMaxSize(),
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
                        configuration = EpubNavigatorFragment.Configuration()
                    ).instantiate(
                        fragmentActivity.classLoader,
                        EpubNavigatorFragment::class.java.name
                    )

                    navigatorFragment = fragment as EpubNavigatorFragment

                    fragmentActivity.supportFragmentManager.beginTransaction()
                        .replace(view.id, fragment)
                        .commitAllowingStateLoss()


                    // Set up the directional navigation adapter
                    (navigatorFragment as? OverflowableNavigator)?.apply {
                        addInputListener(DirectionalNavigationAdapter(this))
                    }
                }
                navigatorFragment?.let { navigator ->
                    if (navigator.isAdded) {// Check if the fragment is added
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
                onToggleReaderSettings = { showReaderSettings = true },
                onToggleUISettings = { showUISettings = true }
            )
        }

        // Top toolbar
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

        NotesDrawer(isOpen = isNotesDrawerOpen,
            onClose = { isNotesDrawerOpen = false }
        )

        HighlightsDrawer(isOpen = isHighlightsDrawerOpen,
            onClose = { isHighlightsDrawerOpen = false }
        )


        if (showFontSettings) {
            FontSettings(
                readerPreferences = readerPreferences,
                onPreferencesChanged = { newPreferences ->
                    viewModel.updateReaderPreferences(newPreferences)
                },
                onDismiss = { showFontSettings = false }
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
