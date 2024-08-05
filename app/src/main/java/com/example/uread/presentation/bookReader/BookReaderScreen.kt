package com.example.uread.presentation.bookReader

import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.uread.presentation.bookReader.components.BottomToolbar
import com.example.uread.presentation.bookReader.components.TopToolbar
import com.example.uread.presentation.bookReader.components.ChaptersDrawer
import com.example.uread.presentation.bookReader.components.NotesDrawer
import com.example.uread.presentation.bookReader.components.ReaderSettings
import com.example.uread.util.SetFullScreen
import kotlinx.coroutines.launch
import org.readium.r2.navigator.OverflowableNavigator
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.util.DirectionalNavigationAdapter
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.locateProgression


@Composable
fun BookReaderScreen(viewModel: BookReaderViewModel = hiltViewModel()) {
    val context = LocalContext.current
    SetFullScreen(context)

    val uiState by viewModel.uiState.collectAsState()
    val initialLocator by viewModel.initialLocator.collectAsState()

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
                    initialLocator = initialLocator
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
    onLocatorChange: (Locator) -> Unit
) {
    val fragmentActivity = LocalContext.current as FragmentActivity
    var navigatorFragment by remember { mutableStateOf<EpubNavigatorFragment?>(null) }
    var showToolbar by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var showReaderSettings by remember { mutableStateOf(false) }

    var isChaptersDrawerOpen by remember { mutableStateOf(false) }
    var isNotesDrawerOpen by remember { mutableStateOf(false) }


    // New state variables
    var progression by remember { mutableDoubleStateOf(1.0) }
    var currentChapter by remember { mutableStateOf("") }
    var currentFontSize by remember { mutableIntStateOf(100) }
    var currentPageMargins by remember { mutableDoubleStateOf(1.4) }
    var currentScrollMode by remember { mutableStateOf(false) }
    var currentFontWeight by remember { mutableDoubleStateOf(1.0) }

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
                                pageMargins = 1.4,
                                fontSize = currentFontSize / 100.0,
                                fontWeight = currentFontWeight,
                                scroll = false,
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
//                            if (showToolbar) {
//                                coroutineScope.launch {
//                                    delay(3000)
//                                    showToolbar = false
//                                }
//                            }
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
//                            if (showToolbar) {
//                                coroutineScope.launch {
//                                    delay(5000)
//                                    showToolbar = false
//                                }
//                            }
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
                onSettingsClick = { showReaderSettings = true },
            )
        }

        // Top toolbar
        TopToolbar(
            showToolbar,
            publication,
            fragmentActivity,
            currentChapter = currentChapter,
            onNotesDrawerToggle = { isNotesDrawerOpen = true },
            onChaptersClick = { isChaptersDrawerOpen = true })



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




        if (showReaderSettings) {
            ReaderSettings(
                navigatorFragment = navigatorFragment,
                initialFontSize = currentFontSize,
                initialFontWeight = currentFontWeight,
                initialPageMargins = currentPageMargins,
                initialScrollMode = currentScrollMode,
                onFontSizeChange = { newSize -> currentFontSize = newSize },
                onFontWeightChange = { newWeight -> currentFontWeight = newWeight },
                onPageMarginsChange = { newMargins -> currentPageMargins = newMargins },
                onScrollModeChange = { newMode -> currentScrollMode = newMode },
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