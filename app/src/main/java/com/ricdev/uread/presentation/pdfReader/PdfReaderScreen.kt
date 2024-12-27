package com.ricdev.uread.presentation.pdfReader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.ricdev.uread.presentation.pdfReader.components.PdfReaderBottomBar
import com.ricdev.uread.presentation.pdfReader.components.PdfReaderTopBar
import com.ricdev.uread.util.KeepScreenOn
import com.ricdev.uread.util.SetFullScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PdfReaderScreen(
    navController: NavHostController,
    viewModel: PdfReaderViewModel = hiltViewModel()
) {

    KeepScreenOn(true)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var areToolbarsVisible by remember { mutableStateOf(false) }
    SetFullScreen(context, showSystemBars = areToolbarsVisible)


    val book by viewModel.book.collectAsStateWithLifecycle()
    val pdfPages by viewModel.pdfPages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val backgroundColor by viewModel.backgroundColor.collectAsStateWithLifecycle()
    val pageCount by viewModel.pageCount.collectAsStateWithLifecycle()
    val initialPage by viewModel.initialPage.collectAsStateWithLifecycle()

    var currentPage by remember { mutableIntStateOf(initialPage) }


    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var showReader by remember { mutableStateOf(false) }
    var coverAlpha by remember { mutableFloatStateOf(1f) }
    var readerAlpha by remember { mutableFloatStateOf(0f) }

    var pagerState = rememberPagerState(initialPage = initialPage) { pageCount }


    LaunchedEffect(Unit) {
        viewModel.loadInitialPages()
        delay(1000) // Delay to show the cover
        showReader = true
        // Animate the transition
        animate(1f, 0f, animationSpec = tween(durationMillis = 500)) { value, _ ->
            coverAlpha = value
        }
        animate(0f, 1f, animationSpec = tween(durationMillis = 500)) { value, _ ->
            readerAlpha = value
        }
    }


    DisposableEffect(currentPage) {
        onDispose {
            viewModel.saveReadingProgress(currentPage)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val screenWidth = size.width
                    val screenHeight = size.height
                    val middleThirdWidth = screenWidth / 3f
                    val middleThirdHeight = screenHeight / 3f

                    val middleThirdRect = Rect(
                        left = middleThirdWidth,
                        top = middleThirdHeight,
                        right = (2 * middleThirdWidth),
                        bottom = (2 * middleThirdHeight)
                    )

                    if (middleThirdRect.contains(offset)) {
                        areToolbarsVisible = !areToolbarsVisible
                    }
                }
            }
    ) {
        // Book cover
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .alpha(coverAlpha),
            contentAlignment = Alignment.Center
        ) {
            val request = ImageRequest.Builder(LocalContext.current)
                .data(book?.coverPath)
                .size(300)
                .scale(Scale.FIT)
                .build()
            AsyncImage(
                model = request,
                contentDescription = "Book cover",
                modifier = Modifier
                    .fillMaxSize(0.7f)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
        }

        // PDF reader content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(readerAlpha)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    pagerState = rememberPagerState(initialPage = initialPage) { pageCount }

                    LaunchedEffect(pagerState) {
                        snapshotFlow { pagerState.currentPage }.collect { page ->
                            currentPage = page
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    val newScale = (scale * zoom).coerceIn(1f, 3f)
                                    if (newScale == 1f) {
                                        offsetX = 0f
                                        offsetY = 0f
                                    } else {
                                        offsetX += pan.x
                                        offsetY += pan.y
                                    }
                                    scale = newScale
                                }
                            }
                    ) { page ->
                        LaunchedEffect(page) {
                            viewModel.loadPage(page)
                            if (page < pdfPages.size - 1) {
                                viewModel.loadPage(page + 1)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(backgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            pdfPages[page]?.let { bitmap ->
                                AsyncImage(
                                    model = bitmap,
                                    contentDescription = "PDF page ${page + 1}",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer(
                                            scaleX = scale,
                                            scaleY = scale,
                                            translationX = offsetX,
                                            translationY = offsetY
                                        ),
                                    contentScale = ContentScale.Fit
                                )
                            } ?: CircularProgressIndicator()
                        }
                    }
                }
            }
        }






        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter),
            visible = areToolbarsVisible,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            PdfReaderTopBar(
                book = book,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }




        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            visible = areToolbarsVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            PdfReaderBottomBar(
                pageCount = pageCount,
                currentPage = currentPage,
                onPageChange = { newPage ->
                    currentPage = newPage
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(newPage - 1)
                    }
                },
            )
        }


    }
}





