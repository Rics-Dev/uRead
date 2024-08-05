package com.example.uread.presentation.bookReader

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.uread.presentation.bookReader.components.BottomToolbar
import com.example.uread.presentation.bookReader.components.TopToolbar
import com.example.uread.presentation.home.components.ChaptersDrawer
import com.example.uread.presentation.home.components.ReaderSettings
import com.example.uread.util.SetFullScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.readium.r2.navigator.HyperlinkNavigator
import org.readium.r2.navigator.OverflowableNavigator
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubSettings
import org.readium.r2.navigator.util.DirectionalNavigationAdapter
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.locate
import org.readium.r2.shared.publication.services.locateProgression
import org.readium.r2.shared.publication.toLocator


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
    var showChapters by remember { mutableStateOf(false) }

    var isChaptersDrawerOpen by remember { mutableStateOf(false) }


    // New state variables
    var progression by remember { mutableStateOf(1.0) }
    var currentChapter by remember { mutableStateOf("") }
    var currentFontSize by remember { mutableStateOf(100) }
    var currentPageMargins by remember { mutableStateOf(1.4) }
    var currentScrollMode by remember { mutableStateOf(false) }

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



        if (showReaderSettings) {
            ReaderSettings(
                navigatorFragment = navigatorFragment,
                initialFontSize = currentFontSize,
                initialPageMargins = currentPageMargins,
                initialScrollMode = currentScrollMode,
                onFontSizeChange = { newSize -> currentFontSize = newSize },
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