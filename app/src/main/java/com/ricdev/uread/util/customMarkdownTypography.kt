package com.ricdev.uread.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownTypography

@Composable
fun customMarkdownTypography(
    h1: TextStyle = MaterialTheme.typography.headlineLarge,
    h2: TextStyle = MaterialTheme.typography.headlineMedium,
    h3: TextStyle = MaterialTheme.typography.headlineSmall,
    h4: TextStyle = MaterialTheme.typography.titleLarge,
    h5: TextStyle = MaterialTheme.typography.titleMedium,
    h6: TextStyle = MaterialTheme.typography.titleSmall,
    text: TextStyle = MaterialTheme.typography.bodyLarge,
    code: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
    quote: TextStyle = MaterialTheme.typography.bodyMedium.plus(SpanStyle(fontStyle = FontStyle.Italic)),
    paragraph: TextStyle = MaterialTheme.typography.bodyLarge,
    ordered: TextStyle = MaterialTheme.typography.bodyLarge,
    bullet: TextStyle = MaterialTheme.typography.bodyLarge,
    list: TextStyle = MaterialTheme.typography.bodyLarge
): MarkdownTypography = DefaultMarkdownTypography(
    h1 = h1, h2 = h2, h3 = h3, h4 = h4, h5 = h5, h6 = h6,
    text = text, quote = quote, code = code, paragraph = paragraph,
    ordered = ordered, bullet = bullet, list = list
)