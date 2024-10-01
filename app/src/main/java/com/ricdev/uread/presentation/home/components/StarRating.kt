package com.ricdev.uread.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.presentation.sharedComponents.dialogs.RatingDialog

@Composable
fun VerticalStarRating(
    book: Book,
    modifier: Modifier = Modifier,
    onRatingChanged: (Float) -> Unit
) {
    var showRatingDialog by remember { mutableStateOf(false) }
    var bookRating by remember { mutableFloatStateOf(book.rating) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = { showRatingDialog = true }),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (i in 1..5) {
            val icon = when {
                i <= book.rating -> Icons.Filled.Star
                i - 0.5f <= book.rating -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Filled.StarBorder
            }
            Icon(
                imageVector = icon,
                contentDescription = "book star rating",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        AnimatedVisibility(visible = bookRating != 0f) {
            Text(
                text = bookRating.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }


    if (showRatingDialog) {
        RatingDialog(
            title = book.title,
            initialRating = bookRating,
            onDismissRequest = { showRatingDialog = false },
            onRatingConfirmed = { newRating ->
                bookRating = newRating
                onRatingChanged(newRating)
            }
        )
    }
}


@Composable
fun HorizontalStarRating(
    book: Book,
    modifier: Modifier = Modifier,
    onRatingChanged: (Float) -> Unit
) {
    var showRatingDialog by remember { mutableStateOf(false) }
    var bookRating by remember { mutableFloatStateOf(book.rating) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { showRatingDialog = true }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val icon = when {
                i <= book.rating -> Icons.Filled.Star
                i - 0.5f <= book.rating -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Filled.StarBorder
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )
        }
//        Spacer(modifier = Modifier.width(4.dp))
        AnimatedVisibility(visible = bookRating != 0f) {
            Text(
                text = bookRating.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    if (showRatingDialog) {
        RatingDialog(
            title = book.title,
            initialRating = bookRating,
            onDismissRequest = { showRatingDialog = false },
            onRatingConfirmed = { newRating ->
                bookRating = newRating
                onRatingChanged(newRating)
            }
        )
    }
}