package com.example.uread.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.navigator.preferences.Color as ReadiumColor


data class ReaderPreferences(
    val fontSize: Double,
    val pageMargins: Double,
    val scroll: Boolean,
    val backgroundColor: Color,
    val textColor: Color
)

// Extension function to convert ReaderPreferences to EpubPreferences
@OptIn(ExperimentalReadiumApi::class)
fun ReaderPreferences.toEpubPreferences(): EpubPreferences {
    return EpubPreferences(
        fontSize = this.fontSize,
        pageMargins = this.pageMargins,
        scroll = this.scroll,
        backgroundColor = ReadiumColor(this.backgroundColor.toArgb()),
        textColor = ReadiumColor(this.textColor.toArgb()),
    )
}