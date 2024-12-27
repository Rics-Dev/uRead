// In ReaderPreferencesUtil.kt
package com.ricdev.uread.data.source.local

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ricdev.uread.data.model.ReaderPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.readium.r2.navigator.preferences.ReadingProgression
import org.readium.r2.navigator.preferences.TextAlign
import org.readium.r2.shared.ExperimentalReadiumApi
import javax.inject.Inject

private val Context.readerPreferencesDataStore by preferencesDataStore(name = "reader_preferences")

class ReaderPreferencesUtil @Inject constructor(
    context: Context
) {
    private val dataStore = context.readerPreferencesDataStore

    companion object {
        val FONT_SIZE = doublePreferencesKey("font_size")
        val LETTER_SPACING = doublePreferencesKey("letter_spacing")
        val LINE_HEIGHT = doublePreferencesKey("line_height")
        val PAGE_MARGINS = doublePreferencesKey("page_margins")
        val PARAGRAPH_INDENT = doublePreferencesKey("paragraph_indent")
        val PARAGRAPH_SPACING = doublePreferencesKey("paragraph_spacing")
        val WORD_SPACING = doublePreferencesKey("word_spacing")
        val TEXT_ALIGN = stringPreferencesKey("text_align")
        val BACKGROUND_COLOR = intPreferencesKey("background_color")
        val TEXT_COLOR = intPreferencesKey("text_color")
        val COLOR_HISTORY = stringPreferencesKey("color_history")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val TAP_NAVIGATION = booleanPreferencesKey("tap_navigation")
        val SCROLL = booleanPreferencesKey("scroll")
        val READING_PROGRESSION = stringPreferencesKey("reading_progression")
        val VERTICAL_TEXT = booleanPreferencesKey("vertical_text")
        val PUBLISHER_STYLES = booleanPreferencesKey("publisher_styles")
        val TEXT_NORMALIZATION = booleanPreferencesKey("text_normalization")


        // Default values
        @OptIn(ExperimentalReadiumApi::class)
        val defaultPreferences = ReaderPreferences(
            fontSize = 1.0,
            letterSpacing = 0.0,
            lineHeight = 1.5,
            pageMargins = 1.0,
            paragraphIndent = 0.0,
            paragraphSpacing = 0.0,
            wordSpacing = 0.0,
            textAlign = TextAlign.JUSTIFY,
            backgroundColor = Color.White,
            textColor = Color.Black,
            colorHistory = emptyList(),
            keepScreenOn = true,
            tapNavigation = false,
            scroll = false,
            readingProgression = ReadingProgression.LTR,
            verticalText = false,
            publisherStyles = false,
            textNormalization = false,
        )
    }


    @OptIn(ExperimentalReadiumApi::class)
    val readerPreferencesFlow: Flow<ReaderPreferences> = dataStore.data.map { preferences ->
        ReaderPreferences(
            fontSize = preferences[FONT_SIZE] ?: defaultPreferences.fontSize,
            letterSpacing = preferences[LETTER_SPACING] ?: defaultPreferences.letterSpacing,
            lineHeight = preferences[LINE_HEIGHT] ?: defaultPreferences.lineHeight,
            pageMargins = preferences[PAGE_MARGINS] ?: defaultPreferences.pageMargins,
            paragraphIndent = preferences[PARAGRAPH_INDENT] ?: defaultPreferences.paragraphIndent,
            paragraphSpacing = preferences[PARAGRAPH_SPACING]
                ?: defaultPreferences.paragraphSpacing,
            wordSpacing = preferences[WORD_SPACING] ?: defaultPreferences.wordSpacing,
            textAlign = TextAlign.valueOf(
                preferences[TEXT_ALIGN] ?: defaultPreferences.textAlign.name
            ),
            backgroundColor = Color(
                preferences[BACKGROUND_COLOR] ?: defaultPreferences.backgroundColor.toArgb()
            ),
            textColor = Color(preferences[TEXT_COLOR] ?: defaultPreferences.textColor.toArgb()),
            colorHistory = preferences[COLOR_HISTORY]?.let { parseColorHistory(it) } ?: emptyList(),
            keepScreenOn = preferences[KEEP_SCREEN_ON] ?: defaultPreferences.keepScreenOn,
            tapNavigation = preferences[TAP_NAVIGATION] ?: defaultPreferences.tapNavigation,
            scroll = preferences[SCROLL] ?: defaultPreferences.scroll,
            readingProgression = ReadingProgression.valueOf(
                preferences[READING_PROGRESSION] ?: defaultPreferences.readingProgression.name
            ),
            verticalText = preferences[VERTICAL_TEXT] ?: defaultPreferences.verticalText,
            publisherStyles = preferences[PUBLISHER_STYLES] ?: defaultPreferences.publisherStyles,
            textNormalization = preferences[TEXT_NORMALIZATION]
                ?: defaultPreferences.textNormalization,
        )
    }

    @OptIn(ExperimentalReadiumApi::class)
    suspend fun updatePreferences(newPreferences: ReaderPreferences) {
        dataStore.edit { preferences ->
            preferences[FONT_SIZE] = newPreferences.fontSize
            preferences[LINE_HEIGHT] = newPreferences.lineHeight
            preferences[LETTER_SPACING] = newPreferences.letterSpacing
            preferences[WORD_SPACING] = newPreferences.wordSpacing



            preferences[PAGE_MARGINS] = newPreferences.pageMargins
            preferences[PARAGRAPH_INDENT] = newPreferences.paragraphIndent
            preferences[PARAGRAPH_SPACING] = newPreferences.paragraphSpacing
            preferences[TEXT_ALIGN] = newPreferences.textAlign.name



            preferences[BACKGROUND_COLOR] = newPreferences.backgroundColor.toArgb()
            preferences[TEXT_COLOR] = newPreferences.textColor.toArgb()



            preferences[COLOR_HISTORY] = serializeColorHistory(newPreferences.colorHistory)



            preferences[KEEP_SCREEN_ON] = newPreferences.keepScreenOn
            preferences[SCROLL] = newPreferences.scroll
            preferences[TAP_NAVIGATION] = newPreferences.tapNavigation
            preferences[READING_PROGRESSION] = newPreferences.readingProgression.name
            preferences[VERTICAL_TEXT] = newPreferences.verticalText
            preferences[PUBLISHER_STYLES] = newPreferences.publisherStyles
            preferences[TEXT_NORMALIZATION] = newPreferences.textNormalization
        }
    }


    suspend fun resetFontPreferences() {
        dataStore.edit { preferences ->
            preferences[FONT_SIZE] = defaultPreferences.fontSize
            preferences[LINE_HEIGHT] = defaultPreferences.lineHeight
            preferences[LETTER_SPACING] = defaultPreferences.letterSpacing
            preferences[WORD_SPACING] = defaultPreferences.wordSpacing

        }
    }

    @OptIn(ExperimentalReadiumApi::class)
    suspend fun resetPagePreferences() {
        dataStore.edit { preferences ->
            preferences[PAGE_MARGINS] = defaultPreferences.pageMargins
            preferences[PARAGRAPH_INDENT] = defaultPreferences.paragraphIndent
            preferences[PARAGRAPH_SPACING] = defaultPreferences.paragraphSpacing
            preferences[TEXT_ALIGN] = defaultPreferences.textAlign.name

        }
    }

    suspend fun resetUiPreferences() {
        dataStore.edit { preferences ->
            preferences[BACKGROUND_COLOR] = defaultPreferences.backgroundColor.toArgb()
            preferences[TEXT_COLOR] = defaultPreferences.textColor.toArgb()
        }
    }

    @OptIn(ExperimentalReadiumApi::class)
    suspend fun resetReaderPreferences() {
        dataStore.edit { preferences ->
            preferences[KEEP_SCREEN_ON] = defaultPreferences.keepScreenOn
            preferences[SCROLL] = defaultPreferences.scroll
            preferences[TAP_NAVIGATION] = defaultPreferences.tapNavigation
            preferences[READING_PROGRESSION] = defaultPreferences.readingProgression.name
            preferences[VERTICAL_TEXT] = defaultPreferences.verticalText
            preferences[PUBLISHER_STYLES] = defaultPreferences.publisherStyles
            preferences[TEXT_NORMALIZATION] = defaultPreferences.textNormalization

        }
    }


    private fun serializeColorHistory(colors: List<Color>): String {
        return colors.joinToString(",") { it.toArgb().toString() }
    }

    private fun parseColorHistory(serialized: String): List<Color> {
        if (serialized.isEmpty()) {
            return emptyList()
        }

        return serialized.split(",")
            .filter { it.isNotEmpty() } // Filter out any empty strings
            .mapNotNull {
                try {
                    Color(it.toInt())
                } catch (e: NumberFormatException) {
                    null // Skip invalid color values
                }
            }
    }

}
