package com.ricdev.uread.util

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.ricdev.uread.data.model.AppLanguage
import java.util.Locale

//class LanguageHelper {
//    fun changeLanguage(context: Context, languageCode: String) {
//        val locale = try{
//            when (languageCode) {
//                "system" -> Resources.getSystem().configuration.locales[0]
//                else -> Locale.forLanguageTag(languageCode)
//            }
//        } catch (e: Exception){
//            // Fallback to locale if invalid
//            Resources.getSystem().configuration.locales[0]
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            context.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList(locale)
//        } else {
//            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
//        }
//    }
//}



//Experimental
class LanguageHelper {
    fun changeLanguage(context: Context, language: AppLanguage) {
        val locale = when (language) {
            AppLanguage.SYSTEM -> {
                // Use LocaleManager to get the system default locale
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val localeManager = context.getSystemService(LocaleManager::class.java)
                    localeManager.systemLocales.get(0) ?: Locale.getDefault()
                } else {
                    // Fallback for older versions
                    Locale.getDefault()
                }
            }
            else -> Locale.forLanguageTag(language.code)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val localeManager = context.getSystemService(LocaleManager::class.java)

                // If selecting system language, use empty LocaleList to reset to system default
                val localeList = if (language == AppLanguage.SYSTEM) {
                    LocaleList.getEmptyLocaleList()
                } else {
                    LocaleList(locale)
                }

                localeManager.applicationLocales = localeList
            } else {
                AppCompatDelegate.setApplicationLocales(
                    if (language == AppLanguage.SYSTEM) {
                        LocaleListCompat.getEmptyLocaleList()
                    } else {
                        LocaleListCompat.create(locale)
                    }
                )
            }
        } catch (e: Exception) {
            Log.e("LanguageHelper", "Failed to change language", e)
        }
    }


    // Context wrapper for more robust locale handling
    fun updateBaseContextLocale(context: Context,language: AppLanguage): Context {
        val locale = when (language) {
            AppLanguage.SYSTEM -> {
                // Use LocaleManager to get the system default locale
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val localeManager = context.getSystemService(LocaleManager::class.java)
                    localeManager.systemLocales.get(0) ?: Locale.getDefault()
                } else {
                    // Fallback for older versions
                    Locale.getDefault()
                }
            }
            else -> Locale.forLanguageTag(language.code)
        }
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }



}