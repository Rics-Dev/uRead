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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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



    // New state variables
    var progression by remember { mutableStateOf(1.0) }
    var currentChapter by remember { mutableStateOf("") }
    var currentFontSize by remember { mutableStateOf(100) }

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

    Box(modifier = Modifier
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
                showToolbar = showToolbar,
                progression = progression,
                currentChapter = currentChapter,
                onFontSizeChange = { newPreferences ->
                    currentFontSize = (newPreferences.fontSize?.times(100) ?: 100).toInt()
                    navigatorFragment?.let { navigator ->
                        coroutineScope.launch {
                            navigator.submitPreferences(newPreferences)
                        }
                    }
                },
                currentFontSize = currentFontSize,
                onPageChange = ::onPageChange
            )
        }


        TopToolbar(showToolbar, publication, fragmentActivity)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSizeSettingsSheet(
    currentFontSize: Double,
    onFontSizeChange: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Font Size", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onFontSizeChange(currentFontSize - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease font size")
                }
                Text("${currentFontSize.toInt()}sp", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = { onFontSizeChange(currentFontSize + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase font size")
                }
            }
        }
    }
}

//            if (newPage in 1..totalPages) {
//                val newLocator =
//                coroutineScope.launch {
//                    navigator.go(newLocator)
//                }
//            }