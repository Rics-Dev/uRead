package com.ricdev.uread.presentation.bookDetails.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.ricdev.uread.data.model.Book

@Composable
fun BookDescription(book: Book) {
    book.description?.let { description ->
        var expanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(modifier = Modifier.fillMaxWidth()) {
                val annotatedString = buildAnnotatedString {
                    val spanned =
                        HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    append(spanned.toString())

                    spanned.getSpans(0, spanned.length, Any::class.java).forEach { span ->
                        val start = spanned.getSpanStart(span)
                        val end = spanned.getSpanEnd(span)
                        when (span) {
                            is android.text.style.StyleSpan -> {
                                when (span.style) {
                                    android.graphics.Typeface.BOLD -> withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(spanned.subSequence(start, end))
                                    }

                                    android.graphics.Typeface.ITALIC -> withStyle(
                                        style = SpanStyle(
                                            fontStyle = FontStyle.Italic
                                        )
                                    ) {
                                        append(spanned.subSequence(start, end))
                                    }
                                }
                            }
                        }
                    }
                }
                // Text
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                // Gradient overlay when not expanded
                if (!expanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp) // Adjust the height to cover the last lines
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background
                                    )
                                ),
                                shape = RectangleShape
                            )
                    )
                }
            }

            // Expand/Collapse Icon
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "expand",
                modifier = Modifier
                    .clickable { expanded = !expanded }
            )
        }
    }
}