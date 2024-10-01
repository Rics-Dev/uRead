package com.ricdev.uread.util

import android.app.LocaleManager
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

class LanguageHelper {
    fun changeLanguage(context: Context, languageCode: String) {
        val locale = when (languageCode) {
            "system" -> Resources.getSystem().configuration.locales[0]
            else -> Locale(languageCode)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList(locale)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
        }
    }
}