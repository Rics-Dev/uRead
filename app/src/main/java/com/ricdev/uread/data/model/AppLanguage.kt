package com.ricdev.uread.data.model

enum class AppLanguage(val code: String, val displayName: String) {
    SYSTEM("system", "System Default"),
    ENGLISH("en", "English"),
    FRENCH("fr", "Français"),
    GERMAN("de", "Deutsch"),
    ITALIAN("it", "Italiano"),
    SPANISH("es", "Español"),
    TURKISH("tr", "Türkçe"),
    CHINESE("zh", "中文"),
    JAPANESE("ja", "日本語"),
    RUSSIAN("ru", "Русский");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code == code } ?: SYSTEM
    }
}