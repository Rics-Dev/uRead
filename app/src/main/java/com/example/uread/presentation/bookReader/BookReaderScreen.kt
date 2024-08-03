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
import android.graphics.PointF
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.graphics.Color
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
import org.readium.r2.shared.util.resource.Resource

@Composable
fun BookReaderScreen(viewModel: BookReaderViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var fontSize by remember { mutableStateOf(100) }
    var showOptions by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    var totalPages by remember { mutableStateOf(1) }

    Box(modifier = Modifier.fillMaxSize().clickable { showOptions = !showOptions }, contentAlignment = Alignment.Center) {
        when (uiState) {
            is BookReaderUiState.Loading -> CircularProgressIndicator()
            is BookReaderUiState.Error -> Text((uiState as BookReaderUiState.Error).message)
            is BookReaderUiState.Success -> {
                val successState = uiState as BookReaderUiState.Success
                BookReaderFragment(
                    publication = successState.publication,
                    context = context,
                    modifier = Modifier.fillMaxSize(),
                    fontSize = fontSize,
                    onPageChange = { page, total ->
                        currentPage = page
                        totalPages = total
                    },
                    onToggleOptions = { showOptions = !showOptions }
                )
            }
        }
        if (showOptions) {
            ReaderOptions(
                fontSize = fontSize,
                onFontSizeChange = { fontSize = it },
                currentPage = currentPage,
                totalPages = totalPages
            )
        }
    }
}

@OptIn(ExperimentalReadiumApi::class)
@Composable
fun BookReaderFragment(
    publication: Publication,
    context: Context,
    modifier: Modifier = Modifier,
    fontSize: Int,
    onPageChange: (Int, Int) -> Unit,
    onToggleOptions: () -> Unit
) {
    AndroidView(
        factory = { FrameLayout(context).apply { id = View.generateViewId() } },
        modifier = modifier,
        update = { frameLayout ->
            val activity = context as FragmentActivity
            val fragmentManager = activity.supportFragmentManager
            val fragmentTag = "EpubNavigatorFragment"

            val navigatorFactory = EpubNavigatorFactory(
                publication = publication,
                configuration = EpubNavigatorFactory.Configuration(
                    defaults = EpubDefaults(
                        fontSize = fontSize.toDouble() / 100,
                        pageMargins = 1.4
                    )
                )
            )


            fragmentManager.fragmentFactory = navigatorFactory.createFragmentFactory(
                initialLocator = null,
                listener = object : EpubNavigatorFragment.Listener {
                    override fun onExternalLinkActivated(url: AbsoluteUrl) {
                        // Handle external link activation
                        // For example, open the URL in a browser
                        // context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())))
                    }

                    override fun shouldFollowInternalLink(
                        link: Link,
                        context: HyperlinkNavigator.LinkContext?
                    ): Boolean {
                        // Determine if an internal link should be followed
                        return true
                    }

                    override fun onJumpToLocator(locator: Locator) {
                        // Handle jump to locator
                        val totalPages = publication.readingOrder.size
                        val currentPage = locator.locations.position ?: 1
                        onPageChange(currentPage, totalPages)
                    }

                    override fun onResourceLoadFailed(href: Url, error: ReadError) {
                        // Handle resource load failure
                        // For example, show an error message
                        // Toast.makeText(context, "Failed to load resource: $href", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            fragmentManager.beginTransaction()
                .replace(frameLayout.id, EpubNavigatorFragment::class.java, null, fragmentTag)
                .commit()
        }
    )
}

@Composable
fun ReaderOptions(
    fontSize: Int,
    onFontSizeChange: (Int) -> Unit,
    currentPage: Int,
    totalPages: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onFontSizeChange((fontSize - 10).coerceAtLeast(50)) }) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease font size")
            }
            Text("Font Size: $fontSize%", color = Color.White)
            IconButton(onClick = { onFontSizeChange((fontSize + 10).coerceAtMost(200)) }) {
                Icon(Icons.Default.Add, contentDescription = "Increase font size")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = currentPage.toFloat() / totalPages.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Page $currentPage of $totalPages", color = Color.White)
    }
}