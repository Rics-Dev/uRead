package com.ricdev.uread.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.FileType
import com.ricdev.uread.data.model.Layout
import com.ricdev.uread.presentation.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookCard(
    book: Book,
    openBook: (Book) -> Unit,
    updateLastOpened: (Book) -> Unit,
    selected: Boolean,
    selectionMode: Boolean,
    toggleSelection: (Book) -> Unit,
    isLoading: Boolean,
    appPreferences: AppPreferences,
    viewModel: HomeViewModel
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.onBackground else Color.Transparent
    var isClicked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isClicked) 1.05f else 1f,
        animationSpec = tween(durationMillis = 100), label = ""
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp


    // Calculate card dimensions based on screen size and grid count
    val cardWidth = (screenWidth / appPreferences.gridCount) - (20.dp / appPreferences.gridCount)
    val cardHeight = (cardWidth * 1.65f).coerceAtMost(screenHeight * 0.4f)


    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top

    ) {

        AnimatedVisibility(visible = appPreferences.showRating) {
            VerticalStarRating(
                book = book,
                onRatingChanged = {
                    viewModel.updateBook(book.copy(rating = it))
                },
                modifier = Modifier
                    .height(cardHeight * 0.5f)
            )
        }



        Box(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
        ) {


            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .border(2.dp, borderColor, shape = RoundedCornerShape(12.dp))
                    .combinedClickable(
                        onClick = {
                            scope.launch {
                                if (selectionMode) {
                                    scope.launch {
                                        isClicked = true
                                        delay(100L)
                                        toggleSelection(book)
                                        isClicked = false
                                    }
                                } else if (!isLoading) {
                                    book.let {
                                        isClicked = true
                                        delay(100L)
                                        updateLastOpened(it)
                                        openBook(it)
                                    }
                                }
                            }
                        },
                        onLongClick = {
                            scope.launch {
                                isClicked = true
                                delay(100L)
                                toggleSelection(book)
                                isClicked = false
                            }
                        }
                    ),
                elevation = CardDefaults.cardElevation(if (selected) 20.dp else 8.dp)
            ) {

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(Color.LightGray)
                        ) {
                            val request = ImageRequest.Builder(LocalContext.current)
                                .data(book.coverPath)
                                .size(300)
                                .scale(Scale.FILL)
                                .build()
                            val imageModifier = remember { Modifier.fillMaxSize() }
                            AsyncImage(
                                model = request,
                                contentDescription = "Book cover",
                                modifier = imageModifier,
                                contentScale = ContentScale.Crop
                            )
                        }
                        AnimatedVisibility(visible = appPreferences.homeLayout != Layout.CoverOnly) {
                            Text(
                                text = book.title,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }



                    if (selected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x80000000)) // 50% transparent gray
                        )
                    }
                }
            }


            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .zIndex(1f)
            ) {
                AnimatedVisibility(
                    visible = book.fileType == FileType.PDF && appPreferences.showPdfLabel,
                ) {
                    PdfLabel()
                }
            }


            // Favorite indicator
            if (book.isFavorite) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp)
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun PdfLabel() {
    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
            )
            .padding(4.dp)

    ) {
        Text(
            text = "PDF",
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}