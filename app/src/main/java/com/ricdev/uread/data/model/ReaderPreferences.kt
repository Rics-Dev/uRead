package com.ricdev.uread.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.preferences.ReadingProgression
import org.readium.r2.navigator.preferences.TextAlign
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.navigator.preferences.Color as ReadiumColor


data class ReaderPreferences @OptIn(ExperimentalReadiumApi::class) constructor(
    //Font Settings
    val fontSize: Double,
    val letterSpacing: Double,
    val lineHeight: Double,
    val pageMargins: Double,
    val paragraphIndent: Double,
    val paragraphSpacing: Double,
    val wordSpacing: Double,
    val textAlign: TextAlign,
    //ui Settings
    val backgroundColor: Color,
    val textColor: Color,
    val colorHistory: List<Color> = emptyList(),
    //Reader Settings
    val keepScreenOn: Boolean,
    val tapNavigation: Boolean,
    val scroll: Boolean,
    val readingProgression: ReadingProgression,
    val verticalText: Boolean,
    val publisherStyles: Boolean,
    val textNormalization: Boolean,
)

// Extension function to convert ReaderPreferences to EpubPreferences
@OptIn(ExperimentalReadiumApi::class)
fun ReaderPreferences.toEpubPreferences(): EpubPreferences {
    return EpubPreferences(
        fontSize = this.fontSize,
//        fontWeight = this.fontWeight,
        letterSpacing = this.letterSpacing,
        lineHeight = this.lineHeight,
        pageMargins = this.pageMargins,
        paragraphIndent = this.paragraphIndent,
        paragraphSpacing = this.paragraphSpacing,
        wordSpacing = this.wordSpacing,
        textAlign = this.textAlign,
        //ui Settings
        backgroundColor = ReadiumColor(this.backgroundColor.toArgb()),
        textColor = ReadiumColor(this.textColor.toArgb()),
        //Reader Settings
        scroll = this.scroll,
        readingProgression = this.readingProgression,
        verticalText = this.verticalText,
        publisherStyles = this.publisherStyles,
        textNormalization = this.textNormalization,
    )
}