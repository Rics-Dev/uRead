package com.example.uread.presentation.bookReader

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val context = LocalContext.current
    val publication by viewModel.publication.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(bookUri) {
        viewModel.openBook(bookUri)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            publication?.let { pub ->
                BookReaderFragment(publication = pub, context = context , modifier = Modifier.fillMaxSize())
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
