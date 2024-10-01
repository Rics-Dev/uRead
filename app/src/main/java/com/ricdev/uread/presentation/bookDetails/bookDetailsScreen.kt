package com.ricdev.uread.presentation.bookDetails


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ricdev.uread.R
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.presentation.bookDetails.components.BookDescription
import com.ricdev.uread.presentation.bookDetails.components.BookReview
import com.ricdev.uread.presentation.bookDetails.components.EditMetadataModal
import com.ricdev.uread.presentation.bookDetails.components.ReadingProgress
import com.ricdev.uread.presentation.bookDetails.components.ReadingStats
import com.ricdev.uread.presentation.sharedComponents.dialogs.RatingDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    navController: NavController,
    viewModel: BookDetailsViewModel = hiltViewModel(),
) {

    val book by viewModel.book.collectAsStateWithLifecycle()
    var showMetadataModal by remember { mutableStateOf(false) }



    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = { Text("") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Faded background cover image
            book?.coverPath?.let { coverPath ->
                Image(
                    painter = rememberAsyncImagePainter(coverPath),
                    contentDescription = "Book cover",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.2f),
                    contentScale = ContentScale.Crop
                )
            }

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.background
                            ),
                            startY = 0f,
                            endY = 1000f
                        )
                    )
            )

            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                book?.let { book ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BookCover(book, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(16.dp))
                        BookInfo(book, modifier = Modifier.weight(2f), viewModel)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    BookDescription(book)
                    Spacer(modifier = Modifier.height(16.dp))
                    ReadingProgress(viewModel, book)
                    Spacer(modifier = Modifier.height(24.dp))
                    ReadingStats(book, viewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                    BookReview(book, viewModel)
                }
            }
        }
    }





    if (showMetadataModal) {
        EditMetadataModal(
            book = book,
            viewModel = viewModel,
            onDismiss = { showMetadataModal = false }
        )
    }


}

@Composable
fun BookCover(book: Book, modifier: Modifier = Modifier) {
    Image(
        painter = rememberAsyncImagePainter(book.coverPath),
        contentDescription = "Book cover",
        modifier = modifier
            .height(200.dp)
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(8.dp))

    )
}

@Composable
fun BookInfo(book: Book, modifier: Modifier = Modifier, viewModel: BookDetailsViewModel) {

    var showRatingDialog by remember { mutableStateOf(false) }
    var bookRating by remember { mutableFloatStateOf(book.rating) }


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(book.title, style = MaterialTheme.typography.titleLarge, maxLines = 2)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonOutline,
                contentDescription = "author",
                Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                book.authors,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        book.publisher?.let { publisher ->
            book.publishDate?.let { publishDate ->
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val date = LocalDate.parse(publishDate, formatter)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PeopleAlt,
                            contentDescription = "publisher",
                            Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            publisher,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            modifier = Modifier
                                .width(100.dp),
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = "publisher",
                            Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "$date",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                }
            }
        }

        book.language?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Translate,
                    contentDescription = "author",
                    Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier
                .clickable(
                    onClick = {
                        showRatingDialog = true
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.StarOutline,
                contentDescription = "rating",
                Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "${book.rating}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }


        book.numberOfPages?.let { Text(stringResource(R.string.pages, it), style = MaterialTheme.typography.bodySmall) }
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













