package com.ricdev.uread.presentation.pdfReader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ricdev.uread.data.model.Book

@Composable
fun PdfReaderTopBar(
    book: Book?,
    onBackClick: () -> Unit
) {


    Column(
        modifier = Modifier
            .shadow(8.dp)
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 32.dp, bottom = 8.dp)
    ) {
        // Back arrow row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            book?.title?.let {
                Text(
                    maxLines = 1,
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
            }
        }

        // Title and page count
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            book?.title?.let {
//                Text(
//                    maxLines = 1,
//                    text = it,
//                    style = MaterialTheme.typography.titleMedium,
//                    overflow = TextOverflow.Ellipsis,
//                    color = Color.Black
//                )
//            }
//        }
    }
}