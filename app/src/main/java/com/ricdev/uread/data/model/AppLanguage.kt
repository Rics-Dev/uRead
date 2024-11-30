package com.ricdev.uread.data.model

enum class AppLanguage(val code: String, val displayName: String) {
    SYSTEM("system", "System Default"),
    ENGLISH("en", "English"),
    SWEDISH("sv", "Svenska"),
    FRENCH("fr", "Français"),
    GERMAN("de", "Deutsch"),
    DUTCH("nl", "Nederlands"),
    ITALIAN("it", "Italiano"),
    SPANISH("es", "Español"),
    PORTUGUESE("pt", "Português"),
    TURKISH("tr", "Türkçe"),
    CHINESE("zh", "中文"),
    JAPANESE("ja", "日本語"),
    KOREAN("ko", "한국어"),
    RUSSIAN("ru", "Русский"),
    ARABIC("ar", "العربية"),
    HINDI("hi", "हिन्दी");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code == code } ?: SYSTEM
    }
}