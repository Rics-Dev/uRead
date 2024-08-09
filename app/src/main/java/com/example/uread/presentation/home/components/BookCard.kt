package com.example.uread.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.example.uread.data.model.Book
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BookCard(
    book: Book?,
    isLoading: Boolean,
    openBook: (Book) -> Unit
) {
    var isClicked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isClicked) 1.05f else 1f,
        animationSpec = tween(durationMillis = 100), label = ""
    )

    Card(
        modifier = Modifier
            .width(120.dp)
            .height(210.dp)
            .scale(scale)
            .clickable(enabled = !isLoading) {
                scope.launch {
                    book?.let {
                        isClicked = true
                        // Delay the navigation to allow the animation to play
                        delay(50L)
                        openBook(it)
                    }
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
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
                if (book != null) {
                    val request = ImageRequest.Builder(LocalContext.current)
                        .data(book.coverPath)
                        .size(300)
                        .scale(Scale.FILL)
                        .build()
                    // Using remember for image loading
                    val imageModifier = remember { Modifier.fillMaxSize() }
                    AsyncImage(
                        model = request,
                        contentDescription = "Book cover",
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Text(
                text = book?.title ?: "",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
