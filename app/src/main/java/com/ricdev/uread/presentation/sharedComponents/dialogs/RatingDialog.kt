package com.ricdev.uread.presentation.sharedComponents.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.ricdev.uread.R
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun RatingDialog(
    title: String,
    initialRating: Float,
    onDismissRequest: () -> Unit,
    onRatingConfirmed: (Float) -> Unit
) {
    var tempRating by remember { mutableFloatStateOf(initialRating) }

    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
            tempRating = initialRating
        },
        title = { Text(title, maxLines = 1) },
        text = {
            Column {
                ScrollableRatingStars(
                    rating = tempRating,
                    onRatingChanged = { tempRating = it }
                )
                Text(
                    text = stringResource(
                        R.string.rating, String.format(
                            Locale.getDefault(),
                            if (tempRating % 1 == 0.5f || tempRating % 1 == 0.0f) "%.1f" else "%.2f",
                            tempRating
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )

            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { tempRating = 0f }
                ) {
                    Icon(imageVector = Icons.Outlined.Clear, contentDescription = "Set to 0")
                }
                Row {
                    TextButton(
                        onClick = {
                            onDismissRequest()
                            tempRating = initialRating
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            onRatingConfirmed(tempRating)
                            onDismissRequest()
                        }
                    ) {
                        Text(stringResource(R.string.set_rating))
                    }
                }
            }
        }
    )
}



fun Float.roundToQuarter(): Float = (this * 4).roundToInt() / 4f

@Composable
fun ScrollableRatingStars(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var size by remember { mutableStateOf(Size.Zero) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .onSizeChanged { size = it.toSize() }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    change.consume()
                    val starWidth = size.width / 5
                    val touchX = change.position.x.coerceIn(0f, size.width)
                    val newRating = (touchX / starWidth).coerceIn(0f, 5f)
                    onRatingChanged(newRating.roundToQuarter())
                }
            }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..5) {
                val starValue = i.toFloat()
                val icon = when {
                    rating >= starValue -> Icons.Filled.Star
                    rating >= starValue - 0.25f -> Icons.Filled.Star // Three-quarter filled star
                    rating >= starValue - 0.5f -> Icons.AutoMirrored.Filled.StarHalf
                    rating >= starValue - 0.75f -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Filled.StarBorder
                }
                val tint = when {
                    rating >= starValue -> MaterialTheme.colorScheme.primary
                    rating >= starValue - 0.25f -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    rating >= starValue - 0.5f -> MaterialTheme.colorScheme.primary
                    rating >= starValue - 0.75f -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
                Icon(
                    imageVector = icon,
                    contentDescription = "Rating Star",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            val newRating =
                                if (rating == starValue) starValue - 0.25f else starValue
                            onRatingChanged(newRating.roundToQuarter())
                        },
                    tint = tint
                )
            }
        }
    }
}