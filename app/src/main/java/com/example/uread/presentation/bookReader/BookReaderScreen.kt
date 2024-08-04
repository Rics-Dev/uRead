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
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import org.readium.r2.navigator.HyperlinkNavigator
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.data.ReadError

@Composable
fun BookReaderScreen(viewModel: BookReaderViewModel = hiltViewModel()) {
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
    var showFontSizeSettings by remember { mutableStateOf(false) }
    var fontSize by remember { mutableStateOf(1.0) }


    LaunchedEffect(navigatorFragment) {
        navigatorFragment?.let { navigator ->
            navigator.currentLocator.collect { locator ->
                onLocatorChange(locator)
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
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp),
            update = { view ->
                if (navigatorFragment == null) {
                    val factory = EpubNavigatorFactory(
                        publication = publication,
                        configuration = EpubNavigatorFactory.Configuration(
                            defaults = EpubDefaults(
                                pageMargins = 1.4,
                                scroll = true,
                                fontSize = fontSize
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
                }
            }
        )

        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(56.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navigatorFragment?.goBackward() }) {
                Icon(Icons.Default.Remove, contentDescription = "Previous page")
            }
            IconButton(onClick = { showFontSizeSettings = true }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
            IconButton(onClick = { navigatorFragment?.goForward() }) {
                Icon(Icons.Default.Add, contentDescription = "Next page")
            }
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