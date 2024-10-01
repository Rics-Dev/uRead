package com.ricdev.uread.presentation.bookDetails.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.presentation.bookDetails.BookDetailsViewModel
import com.ricdev.uread.presentation.sharedComponents.dialogs.RatingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReview(book: Book, viewModel: BookDetailsViewModel) {
    var showReviewModal by remember { mutableStateOf(false) }
    var showDeleteReviewDialog by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf(book.review ?: "") }
    var showRatingDialog by remember { mutableStateOf(false) }
    var bookRating by remember { mutableFloatStateOf(book.rating) }

    Column(
        modifier = Modifier
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.review),
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = Modifier
                    .clickable(onClick = { showRatingDialog = true }),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.StarOutline,
                    contentDescription = "rating",
                    Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${book.rating}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { showReviewModal = true }, enabled = book.review != null),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = if (book.review != null && book.review != "") Alignment.Start else Alignment.CenterHorizontally
            ) {
                if (book.review != null && book.review != "") {
                    Text(
                        text = book.review,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.no_review_yet_add_one),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (book.review == null || book.review == "") {
                    Button(
                        onClick = { showReviewModal = true },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit review")
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.add_review))
                    }
                }

            }
        }
    }

    if (showReviewModal) {
        ModalBottomSheet(
            shape = BottomSheetDefaults.HiddenShape,
            dragHandle = null,
            onDismissRequest = {
                viewModel.updateBook(book.copy(review = reviewText))
                showReviewModal = false
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { it != SheetValue.PartiallyExpanded }
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.updateBook(book.copy(review = reviewText))
                                showReviewModal = false
                            },
                        ) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                        }
                        Text(
                            text = stringResource(R.string.book_review),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    IconButton(
                        onClick = {
                            showDeleteReviewDialog = true
                        },
                    ) {
                        Icon(
                            Icons.Outlined.DeleteOutline,
                            contentDescription = "Delete review",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    BasicTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.padding(2.dp)) {
                                if (reviewText.isEmpty()) {
                                    Text(
                                        stringResource(R.string.write_your_review_here),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            viewModel.updateBook(book.copy(review = reviewText))
                            showReviewModal = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }




    if (showDeleteReviewDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteReviewDialog = false },
            title = {
                Text(stringResource(R.string.delete_review))
            },
            text = {
                Text(stringResource(R.string.are_you_sure_you_want_to_delete_this_review))
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    onClick = {
                        viewModel.updateBook(book.copy(review = null))
                        showDeleteReviewDialog = false
                        showReviewModal = false
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteReviewDialog = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showRatingDialog) {
        RatingDialog(
            title = book.title,
            initialRating = bookRating,
            onDismissRequest = { showRatingDialog = false },
            onRatingConfirmed = { newRating ->
                bookRating = newRating
                viewModel.updateBook(book.copy(rating = newRating))
            }
        )
    }
}