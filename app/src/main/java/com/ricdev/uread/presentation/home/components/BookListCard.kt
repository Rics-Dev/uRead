package com.ricdev.uread.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.FileType
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.presentation.home.HomeViewModel
import com.ricdev.uread.presentation.sharedComponents.dialogs.ReadingDatesDialog
import com.ricdev.uread.presentation.sharedComponents.dialogs.ReadingStatusDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookListCard(
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
    var showReadingStatusDialog by remember { mutableStateOf(false) }
    var showReadingDatesDialog by remember { mutableStateOf(false) }
    var isStartDate by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isClicked) 1.02f else 1f,
        animationSpec = tween(durationMillis = 100), label = ""
    )

    fun formatReadingTime(timeInMillis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(timeInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
        return when {
            hours > 0 -> String.format(Locale.getDefault(), "%d h %d min", hours, minutes)
            minutes > 0 -> String.format(Locale.getDefault(), "%d min %d sec", minutes, seconds)
            else -> String.format(Locale.getDefault(), "%d sec", seconds)
        }
    }

    fun formatReadingDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }




    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .scale(scale)
            .border(2.dp, borderColor, shape = RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = {
                    scope.launch {
                        if (selectionMode) {
                            isClicked = true
                            delay(100L)
                            toggleSelection(book)
                            isClicked = false
                        } else if (!isLoading) {
                            isClicked = true
                            delay(100L)
                            updateLastOpened(book)
                            openBook(book)
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
        elevation = CardDefaults.cardElevation(if (selected) 8.dp else 4.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x7F000000)) // 50% transparent gray
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .width(70.dp)
                        .background(Color.LightGray)
                ) {
                    val request = ImageRequest.Builder(LocalContext.current)
                        .data(book.coverPath)
                        .size(200)
                        .scale(Scale.FILL)
                        .build()
                    AsyncImage(
                        model = request,
                        contentDescription = "Book cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .zIndex(1f)
                                .fillMaxSize()
                                .background(Color(0x7F000000)) // 50% transparent gray
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        AnimatedVisibility(visible = appPreferences.showReadingStatus) {
                            ReadingStatusIcon(
                                status = book.readingStatus,
                                onClick = {
                                    showReadingStatusDialog = true
                                },
                            )
                        }
                    }
                    AnimatedVisibility(visible = !appPreferences.showRating) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.width(if (appPreferences.showReadingDates) 125.dp else 200.dp),
                            text = book.authors,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        AnimatedVisibility(visible = appPreferences.showReadingDates) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                horizontalAlignment = Alignment.End
                            ) {

                                Text(
                                    text = stringResource(
                                        R.string.started,
                                        book.startReadingDate?.let { formatReadingDate(it) }
                                            ?: stringResource(R.string.not_yet)),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.clickable(onClick = {
                                        isStartDate = true; showReadingDatesDialog = true
                                    })
                                )
                            }

                        }
                    }

                    AnimatedVisibility(visible = !appPreferences.showRating && !appPreferences.showReadingDates) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    AnimatedVisibility(visible = appPreferences.showRating || appPreferences.showReadingDates) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedVisibility(visible = appPreferences.showRating) {
                                HorizontalStarRating(
                                    book = book,
                                    onRatingChanged = {
                                        viewModel.updateBook(book.copy(rating = it))
                                    },
                                    modifier = Modifier
                                        .width(90.dp)

                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            AnimatedVisibility(
                                visible = appPreferences.showReadingDates
                            ) {
                                Text(
                                    text = stringResource(
                                        R.string.finished_1,
                                        book.endReadingDate?.let { formatReadingDate(it) }
                                            ?: stringResource(R.string.not_yet)),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.clickable(onClick = {
                                        isStartDate = false; showReadingDatesDialog = true
                                    })
                                )

                            }
                        }
                    }




                    Text(
                        text = if (book.readingTime > 0) {
                            stringResource(
                                R.string.completed_reading_time,
                                book.progression.toInt(),
                                formatReadingTime(
                                    book.readingTime
                                )
                            )
                        } else {
                            stringResource(R.string.completed, book.progression.toInt())
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    AnimatedVisibility(visible = !appPreferences.showRating && !appPreferences.showReadingDates) {
                        Spacer(modifier = Modifier.height(8.dp))

                    }
                    AnimatedVisibility(visible = appPreferences.showRating || appPreferences.showReadingDates) {
                        Spacer(modifier = Modifier.height(4.dp))

                    }
                    if (book.progression != 0f) {
                        LinearProgressIndicator(
                            progress = { book.progression / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50.dp)),
                            color = MaterialTheme.colorScheme.background.copy(alpha = 1f),
                            trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
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

        }

    }



    if (showReadingDatesDialog) {
        ReadingDatesDialog(
            initialDate = if (isStartDate) book.startReadingDate else book.endReadingDate,
            onDateSelected = { newDate ->
                if (isStartDate) {
                    viewModel.updateBook(book.copy(startReadingDate = newDate))
                } else {
                    viewModel.updateBook(book.copy(endReadingDate = newDate))
                }
                showReadingDatesDialog = false
            },
            onDismiss = { showReadingDatesDialog = false },
            isStartDate = isStartDate
        )
    }


    if (showReadingStatusDialog) {
        ReadingStatusDialog(
            currentStatus = book.readingStatus,
            onStatusSelected = { newStatus ->
                viewModel.updateBook(
                    book.copy(readingStatus = newStatus),
                    updatedReadingStatus = true
                )
                showReadingStatusDialog = false
            },
            onDismiss = { showReadingStatusDialog = false }
        )
    }


}




@Composable
fun ReadingStatusIcon(
    status: ReadingStatus?,
    onClick: () -> Unit
) {
    val icon = when (status) {
        ReadingStatus.NOT_STARTED -> Icons.Outlined.Book
        ReadingStatus.IN_PROGRESS -> Icons.Outlined.AutoStories
        ReadingStatus.FINISHED -> Icons.Outlined.CheckCircle
        null -> Icons.AutoMirrored.Filled.Help
    }

    Icon(
        imageVector = icon,
        contentDescription = "Reading status",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .size(24.dp)
            .clickable(onClick = onClick)
    )
}