package com.ricdev.uread.presentation.home.components

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ricdev.uread.BuildConfig
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.FileType
import com.ricdev.uread.presentation.home.HomeViewModel
import com.ricdev.uread.navigation.Screens
import kotlin.random.Random

@Composable
fun GridLayout(
    clearSearch: () -> Unit,
    books: LazyPagingItems<Book>,
    navController: NavHostController,
    selectedBooks: List<Book>,
    selectionMode: Boolean,
    toggleSelection: (Book) -> Unit,
    viewModel: HomeViewModel,
    isLoading: Boolean,
    appPreferences: AppPreferences,
) {


    val gridAdUnit = BuildConfig.OPEN_BOOK_GRID_AD_UNIT

    val context = LocalContext.current
    var isBookOpen by remember { mutableStateOf(false) }
    var mInterstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }
    fun loadInterstitialAd() {
        if (!appPreferences.isPremium) {
            InterstitialAd.load(
                context,
                gridAdUnit,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        mInterstitialAd = null
                    }
                }
            )
        }
    }

    fun showInterstitialAd(onAdDismissed: () -> Unit) {
        if (!appPreferences.isPremium && mInterstitialAd != null) {
            mInterstitialAd?.let { ad ->
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null
                        loadInterstitialAd()
                        onAdDismissed()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        mInterstitialAd = null
                        onAdDismissed()
                    }
                }
                ad.show(context as Activity)
            }
        } else {
            onAdDismissed()
        }
    }

    // Load the ad when the composable is first created
    LaunchedEffect(Unit) {
        if (!appPreferences.isPremium) {
            loadInterstitialAd()
        }
    }


    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp


    // Calculate spacing based on screen size
    val horizontalSpacing = (screenWidth * 0.02f).coerceAtLeast(4.dp).coerceAtMost(16.dp)
    val verticalSpacing = (screenHeight * 0.02f).coerceAtLeast(6.dp).coerceAtMost(24.dp)


    val isAddingBook by viewModel.isAddingBooks.collectAsState()


    LazyVerticalGrid(
        userScrollEnabled = !isAddingBook,
        columns = GridCells.Fixed(appPreferences.gridCount),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            count = books.itemCount,
            key = books.itemKey { book -> "${book.id}_${book.uri}" }
        ) { index ->
            val book = books[index] ?: return@items
            val isSelected = selectedBooks.contains(book)
            Box(
                modifier = Modifier.animateItem()
            ) {
                BookCard(
                    book = book,
                    openBook = { openedBook ->
                        if (selectionMode) {
                            toggleSelection(book)
                        } else if (!isBookOpen) {
                            clearSearch()
                            val shouldShowAd =
                                !appPreferences.isPremium && Random.nextFloat() < 0.25f // 25% chance to show ad
                            val navigateToBook = {
                                val encodedUri = Uri.encode(openedBook.uri)
                                isBookOpen = true
                                navController.navigate(
                                    route = when (book.fileType) {
                                        FileType.EPUB -> Screens.BookReaderScreen.route + "/${openedBook.id}/${encodedUri}"
                                        FileType.PDF -> Screens.PdfReaderScreen.route + "/${openedBook.id}/${encodedUri}"
                                        FileType.AUDIOBOOK -> Screens.AudiobookReaderScreen.route + "/${openedBook.id}/${encodedUri}"
                                    }
                                )
                            }
                            if (shouldShowAd) {
                                navigateToBook()
                                showInterstitialAd(navigateToBook)
                            } else {
                                navigateToBook()
                            }
                        }
                    },
                    updateLastOpened = {
                        viewModel.updateBook(book.copy(lastOpened = System.currentTimeMillis()))
                    },
                    selected = isSelected,
                    selectionMode = selectionMode,
                    toggleSelection = {
                        toggleSelection(it)
                    },
                    isLoading = isLoading,
                    appPreferences = appPreferences,
                    viewModel = viewModel,
                )
            }
        }
    }
}
