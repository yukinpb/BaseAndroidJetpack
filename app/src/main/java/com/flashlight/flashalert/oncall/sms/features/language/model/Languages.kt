package com.flashlight.flashalert.oncall.sms.features.language.model

import com.flashlight.flashalert.oncall.sms.R

data class LanguageGroup(
    val code: String,
    val flagRes: Int,
    val displayName: Int,
    val children: List<LanguageItem> = emptyList()
)

object Languages {
    val englishChildren = listOf(
        LanguageItem("en-US", R.drawable.english_flag, R.string.language_english_us),
        LanguageItem("en-UK", R.drawable.english_uk_flag, R.string.language_english_uk),
        LanguageItem("en-CA", R.drawable.english_canada_flag, R.string.language_english_ca),
        LanguageItem("en-ZA", R.drawable.english_sa_flag, R.string.language_english_za)
    )
    val languages = listOf(
        LanguageGroup("fr", R.drawable.france_flag, R.string.language_france),
        LanguageGroup("en", R.drawable.english_flag, R.string.language_english, englishChildren),
        LanguageGroup("zh", R.drawable.chinna_flag, R.string.language_china),
        LanguageGroup("in", R.drawable.indo_flag, R.string.language_indonesia),
        LanguageGroup("hi", R.drawable.india_flag, R.string.language_india),
        LanguageGroup("de", R.drawable.germany_flag, R.string.language_germany),
        LanguageGroup("es", R.drawable.spain_flag, R.string.language_spain),
        LanguageGroup("ja", R.drawable.japan_flag, R.string.language_japan),
    )
}

/**
 * Data class to store language information
 */
data class LanguageItem(
    val code: String,  // String resource ID for language name
    val flagRes: Int,       // Drawable resource ID for flag icon
    val displayName: Int // Language name in native text
)
