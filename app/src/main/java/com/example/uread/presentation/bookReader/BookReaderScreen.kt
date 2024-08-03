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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Publication

@Composable
fun BookReaderScreen(
    viewModel: BookReaderViewModel = hiltViewModel(),
    bookUri: String
) {
    val publication by viewModel.publication.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(bookUri) {
        viewModel.openBook(bookUri)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            error != null -> {
                Text(error!!)
            }
            publication != null -> {
                BookReaderFragment(publication = publication!!, context = context , modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalReadiumApi::class)
@Composable
fun BookReaderFragment(publication: Publication, context: Context, modifier: Modifier = Modifier) {
    // Set up the Fragment inside a Box
    AndroidView(
        factory = {
            FrameLayout(context).apply {
                id = View.generateViewId()
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { frameLayout ->
        // Set up the FragmentManager and Fragment
        val activity = context as FragmentActivity
        val fragmentManager = activity.supportFragmentManager
        val fragmentTag = "EpubNavigatorFragment"

        val navigatorFactory = EpubNavigatorFactory(
            publication = publication,
            configuration = EpubNavigatorFactory.Configuration(
                defaults = EpubDefaults(pageMargins = 1.4)
            )
        )

        val fragmentFactory = navigatorFactory.createFragmentFactory(
            initialLocator = null, // Pass any initial Locator if needed
            listener = null // Implement a listener if needed
        )

        fragmentManager.fragmentFactory = fragmentFactory

        // Use FragmentTransaction to add the fragment
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            frameLayout.id,
            EpubNavigatorFragment::class.java,
            null,
            fragmentTag
        )
        fragmentTransaction.commit()
    }
}