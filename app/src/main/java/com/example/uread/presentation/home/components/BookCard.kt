package com.example.uread.presentation.home.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.example.uread.data.model.Book

@Composable
fun BookCard(
    book: Book?,
    openBook: (Book) -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(210.dp)
            .clickable { book?.let { openBook(it) } },
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
