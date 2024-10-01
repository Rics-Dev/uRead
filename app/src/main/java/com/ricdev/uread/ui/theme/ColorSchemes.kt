package com.ricdev.uread.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Default Light and Dark schemes
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF000000), // Black
    onPrimary = Color.White, // White text on black
    primaryContainer = Color(0xFFBDBDBD), // Light gray container
    onPrimaryContainer = Color(0xFF212121), // Darker gray text on light gray
    secondary = Color(0xFF616161), // Medium gray
    onSecondary = Color.White, // White text on medium gray
    secondaryContainer = Color(0xFFE0E0E0), // Light gray container
    onSecondaryContainer = Color(0xFF424242), // Dark gray text on light gray
    tertiary = Color(0xFF757575), // Another shade of gray
    onTertiary = Color.White, // White text on gray
    tertiaryContainer = Color(0xFFEEEEEE), // Lighter gray container
    onTertiaryContainer = Color(0xFF616161), // Medium gray text on light gray
    background = Color.White, // White background
    onBackground = Color.Black, // Black text on white background
    surface = Color.White, // White surface
    onSurface = Color.Black, // Black text on white surface
    surfaceVariant = Color(0xFFE0E0E0), // Light gray variant surface
    onSurfaceVariant = Color(0xFF424242), // Dark gray text on light gray surface
    error = Color(0xFFB00020), // Default error color
    onError = Color.White, // White text on error
    errorContainer = Color(0xFFFCDAD7), // Light red error container
    onErrorContainer = Color(0xFF410002), // Dark text on error container
    outline = Color(0xFFBDBDBD) // Gray outline
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF), // White
    onPrimary = Color.Black, // Black text on white
    primaryContainer = Color(0xFF424242), // Dark gray container
    onPrimaryContainer = Color(0xFFE0E0E0), // Light gray text on dark gray
    secondary = Color(0xFFBDBDBD), // Light gray
    onSecondary = Color.Black, // Black text on light gray
    secondaryContainer = Color(0xFF616161), // Medium gray container
    onSecondaryContainer = Color(0xFFE0E0E0), // Light gray text on medium gray
    tertiary = Color(0xFF9E9E9E), // Gray
    onTertiary = Color.Black, // Black text on gray
    tertiaryContainer = Color(0xFF424242), // Dark gray container
    onTertiaryContainer = Color(0xFFE0E0E0), // Light gray text on dark gray
    background = Color(0xFF121212), // Almost black background
    onBackground = Color.White, // White text on dark background
    surface = Color(0xFF121212), // Almost black surface
    onSurface = Color.White, // White text on dark surface
    surfaceVariant = Color(0xFF424242), // Dark gray variant surface
    onSurfaceVariant = Color(0xFFBDBDBD), // Light gray text on dark surface
    error = Color(0xFFCF6679), // Default dark error color
    onError = Color.Black, // Black text on error
    errorContainer = Color(0xFF93000A), // Dark red error container
    onErrorContainer = Color(0xFFFFDAD6), // Light text on error container
    outline = Color(0xFF616161) // Gray outline
)


// Purple variants
val LightPurpleScheme = lightColorScheme(
    primary = Color(0xFF9C27B0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E5F5),
    onPrimaryContainer = Color(0xFF3E001E),
    secondary = Color(0xFF7B1FA2),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1BEE7),
    onSecondaryContainer = Color(0xFF2A0033),
    tertiary = Color(0xFFAB47BC),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF3E5F5),
    onTertiaryContainer = Color(0xFF3E0043),
    background = Color(0xFFFCF8FD),
    onBackground = Color(0xFF1D1B1E),
    surface = Color(0xFFFCF8FD),
    onSurface = Color(0xFF1D1B1E),
    surfaceVariant = Color(0xFFEADFEA),
    onSurfaceVariant = Color(0xFF49454E),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF7A757F)
)

val DarkPurpleScheme = darkColorScheme(
    primary = Color(0xFFCE93D8),
    onPrimary = Color(0xFF5B0058),
    primaryContainer = Color(0xFF7F007C),
    onPrimaryContainer = Color(0xFFFFD6FA),
    secondary = Color(0xFFBA68C8),
    onSecondary = Color(0xFF5B0058),
    secondaryContainer = Color(0xFF7B1FA2),
    onSecondaryContainer = Color(0xFFF3E5F5),
    tertiary = Color(0xFFD1C4E9),
    onTertiary = Color(0xFF381E72),
    tertiaryContainer = Color(0xFF4F378B),
    onTertiaryContainer = Color(0xFFEADDFF),
    background = Color(0xFF1D1B1E),
    onBackground = Color(0xFFE6E1E6),
    surface = Color(0xFF1D1B1E),
    onSurface = Color(0xFFE6E1E6),
    surfaceVariant = Color(0xFF49454E),
    onSurfaceVariant = Color(0xFFCBC4CF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF958E99)
)


// Teal variants
val LightTealScheme = lightColorScheme(
    primary = Color(0xFF009688),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Color(0xFF002022),
    secondary = Color(0xFF00796B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF80CBC4),
    onSecondaryContainer = Color(0xFF00251F),
    tertiary = Color(0xFF26A69A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2F1),
    onTertiaryContainer = Color(0xFF002D2A),
    background = Color(0xFFE0F2F1),
    onBackground = Color(0xFF001F1E),
    surface = Color(0xFFE0F2F1),
    onSurface = Color(0xFF001F1E),
    surfaceVariant = Color(0xFFDAE5E3),
    onSurfaceVariant = Color(0xFF3F4948),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF6F7978)
)

val DarkTealScheme = darkColorScheme(
    primary = Color(0xFF80CBC4),
    onPrimary = Color(0xFF003733),
    primaryContainer = Color(0xFF004D46),
    onPrimaryContainer = Color(0xFFA7F3EC),
    secondary = Color(0xFF4DB6AC),
    onSecondary = Color(0xFF003733),
    secondaryContainer = Color(0xFF00504A),
    onSecondaryContainer = Color(0xFF70F7EE),
    tertiary = Color(0xFF4DB6AC),
    onTertiary = Color(0xFF00332F),
    tertiaryContainer = Color(0xFF004D46),
    onTertiaryContainer = Color(0xFF70F7EE),
    background = Color(0xFF001F1E),
    onBackground = Color(0xFFA7F3EC),
    surface = Color(0xFF001F1E),
    onSurface = Color(0xFFA7F3EC),
    surfaceVariant = Color(0xFF3F4948),
    onSurfaceVariant = Color(0xFFBEC9C7),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF899391)
)

// Sepia variants (for a warm, paper-like feel)
val LightSepiaScheme = lightColorScheme(
    primary = Color(0xFF8B4513),  // SaddleBrown
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDEB887),  // BurlyWood
    onPrimaryContainer = Color(0xFF3E2723),  // Brown900
    secondary = Color(0xFFD2691E),  // Chocolate
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE4B5),  // Moccasin
    onSecondaryContainer = Color(0xFF3E2723),  // Brown900
    tertiary = Color(0xFFCD853F),  // Peru
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFAF0E6),  // Linen
    onTertiaryContainer = Color(0xFF3E2723),  // Brown900
    background = Color(0xFFFDF5E6),  // OldLace
    onBackground = Color(0xFF3E2723),  // Brown900
    surface = Color(0xFFFDF5E6),  // OldLace
    onSurface = Color(0xFF3E2723),  // Brown900
    surfaceVariant = Color(0xFFE6D8CC),
    onSurfaceVariant = Color(0xFF4E342E),  // Brown800
    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFCDAD7),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF8D6E63)  // Brown300
)

val DarkSepiaScheme = darkColorScheme(
    primary = Color(0xFFDEB887),  // BurlyWood
    onPrimary = Color(0xFF3E2723),  // Brown900
    primaryContainer = Color(0xFF8B4513),  // SaddleBrown
    onPrimaryContainer = Color(0xFFFFF8DC),  // Cornsilk
    secondary = Color(0xFFFFE4B5),  // Moccasin
    onSecondary = Color(0xFF3E2723),  // Brown900
    secondaryContainer = Color(0xFFD2691E),  // Chocolate
    onSecondaryContainer = Color(0xFFFFF8DC),  // Cornsilk
    tertiary = Color(0xFFFAF0E6),  // Linen
    onTertiary = Color(0xFF3E2723),  // Brown900
    tertiaryContainer = Color(0xFFCD853F),  // Peru
    onTertiaryContainer = Color(0xFFFFF8DC),  // Cornsilk
    background = Color(0xFF3E2723),  // Brown900
    onBackground = Color(0xFFFDF5E6),  // OldLace
    surface = Color(0xFF3E2723),  // Brown900
    onSurface = Color(0xFFFDF5E6),  // OldLace
    surfaceVariant = Color(0xFF4E342E),  // Brown800
    onSurfaceVariant = Color(0xFFE6D8CC),
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFFA1887F)  // Brown200
)

// Night Mode variants (for comfortable night reading)
val LightGreyScheme = lightColorScheme(
    primary = Color(0xFF263238),  // BlueGrey900
    onPrimary = Color(0xFFECEFF1),  // BlueGrey50
    primaryContainer = Color(0xFF455A64),  // BlueGrey700
    onPrimaryContainer = Color(0xFFECEFF1),  // BlueGrey50
    secondary = Color(0xFF37474F),  // BlueGrey800
    onSecondary = Color(0xFFECEFF1),  // BlueGrey50
    secondaryContainer = Color(0xFF546E7A),  // BlueGrey600
    onSecondaryContainer = Color(0xFFECEFF1),  // BlueGrey50
    tertiary = Color(0xFF78909C),  // BlueGrey400
    onTertiary = Color(0xFF102027),  // BlueGrey900Dark
    tertiaryContainer = Color(0xFFCFD8DC),  // BlueGrey100
    onTertiaryContainer = Color(0xFF263238),  // BlueGrey900
    background = Color(0xFFECEFF1),  // BlueGrey50
    onBackground = Color(0xFF263238),  // BlueGrey900
    surface = Color(0xFFECEFF1),  // BlueGrey50
    onSurface = Color(0xFF263238),  // BlueGrey900
    surfaceVariant = Color(0xFFB0BEC5),  // BlueGrey200
    onSurfaceVariant = Color(0xFF37474F),  // BlueGrey800
    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFCDAD7),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF78909C)  // BlueGrey400
)

val DarkGreyScheme = darkColorScheme(
    primary = Color(0xFF78909C),  // BlueGrey400
    onPrimary = Color(0xFF102027),  // BlueGrey900Dark
    primaryContainer = Color(0xFF455A64),  // BlueGrey700
    onPrimaryContainer = Color(0xFFCFD8DC),  // BlueGrey100
    secondary = Color(0xFF90A4AE),  // BlueGrey300
    onSecondary = Color(0xFF102027),  // BlueGrey900Dark
    secondaryContainer = Color(0xFF546E7A),  // BlueGrey600
    onSecondaryContainer = Color(0xFFECEFF1),  // BlueGrey50
    tertiary = Color(0xFFB0BEC5),  // BlueGrey200
    onTertiary = Color(0xFF263238),  // BlueGrey900
    tertiaryContainer = Color(0xFF37474F),  // BlueGrey800
    onTertiaryContainer = Color(0xFFECEFF1),  // BlueGrey50
    background = Color(0xFF102027),  // BlueGrey900Dark
    onBackground = Color(0xFFCFD8DC),  // BlueGrey100
    surface = Color(0xFF102027),  // BlueGrey900Dark
    onSurface = Color(0xFFCFD8DC),  // BlueGrey100
    surfaceVariant = Color(0xFF37474F),  // BlueGrey800
    onSurfaceVariant = Color(0xFFB0BEC5),  // BlueGrey200
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF78909C)  // BlueGrey400
)

// Parchment variants (for an antique book feel)
val LightParchmentScheme = lightColorScheme(
    primary = Color(0xFF8D6E63),  // Brown300
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7CCC8),  // Brown100
    onPrimaryContainer = Color(0xFF3E2723),  // Brown900
    secondary = Color(0xFFA1887F),  // Brown200
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFEBE9),  // Brown50
    onSecondaryContainer = Color(0xFF3E2723),  // Brown900
    tertiary = Color(0xFFBCAAA4),  // Brown200
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFF5F5F5),  // Grey100
    onTertiaryContainer = Color(0xFF3E2723),  // Brown900
    background = Color(0xFFFFFBE6),  // Custom light parchment color
    onBackground = Color(0xFF3E2723),  // Brown900
    surface = Color(0xFFFFFBE6),  // Custom light parchment color
    onSurface = Color(0xFF3E2723),  // Brown900
    surfaceVariant = Color(0xFFF0E8D9),  // Custom parchment variant
    onSurfaceVariant = Color(0xFF4E342E),  // Brown800
    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFCDAD7),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF8D6E63)  // Brown300
)

val DarkParchmentScheme = darkColorScheme(
    primary = Color(0xFFD7CCC8),  // Brown100
    onPrimary = Color(0xFF3E2723),  // Brown900
    primaryContainer = Color(0xFF8D6E63),  // Brown300
    onPrimaryContainer = Color(0xFFFFFBE6),  // Custom light parchment color
    secondary = Color(0xFFEFEBE9),  // Brown50
    onSecondary = Color(0xFF3E2723),  // Brown900
    secondaryContainer = Color(0xFFA1887F),  // Brown200
    onSecondaryContainer = Color(0xFFFFFBE6),  // Custom light parchment color
    tertiary = Color(0xFFF5F5F5),  // Grey100
    onTertiary = Color(0xFF3E2723),  // Brown900
    tertiaryContainer = Color(0xFFBCAAA4),  // Brown200
    onTertiaryContainer = Color(0xFFFFFBE6),  // Custom light parchment color
    background = Color(0xFF362F2D),  // Custom dark parchment color
    onBackground = Color(0xFFFFFBE6),  // Custom light parchment color
    surface = Color(0xFF362F2D),  // Custom dark parchment color
    onSurface = Color(0xFFFFFBE6),  // Custom light parchment color
    surfaceVariant = Color(0xFF4E342E),  // Brown800
    onSurfaceVariant = Color(0xFFF0E8D9),  // Custom parchment variant
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFFA1887F)  // Brown200
)






// Pastel Pink Scheme
val LightPinkScheme = lightColorScheme(
    primary = Color(0xFFFFC1CC), // Pastel Pink
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE4E9), // Lighter Pastel Pink
    onPrimaryContainer = Color(0xFF4A001F), // Darker Pink
    secondary = Color(0xFFFFAFC8), // Soft Pink
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE4E9),
    onSecondaryContainer = Color(0xFF4A001F),
    tertiary = Color(0xFFFFCCD7), // Light Pastel Pink
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE4E9),
    onTertiaryContainer = Color(0xFF4A001F),
    background = Color(0xFFFFF0F5), // Very Light Pink Background
    onBackground = Color(0xFF1D1B1E),
    surface = Color(0xFFFFF0F5),
    onSurface = Color(0xFF1D1B1E),
    surfaceVariant = Color(0xFFFFE4E9),
    onSurfaceVariant = Color(0xFF4A001F),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFFFFAFC8) // Soft Pink
)

val DarkPinkScheme = darkColorScheme(
    primary = Color(0xFFFFAFC8), // Soft Pink
    onPrimary = Color(0xFF4A001F),
    primaryContainer = Color(0xFF85002F), // Darker Pink
    onPrimaryContainer = Color(0xFFFFE4E9),
    secondary = Color(0xFFFF8FAB), // Deeper Pastel Pink
    onSecondary = Color(0xFF4A001F),
    secondaryContainer = Color(0xFF85002F),
    onSecondaryContainer = Color(0xFFFFE4E9),
    tertiary = Color(0xFFFFCCD7),
    onTertiary = Color(0xFF4A001F),
    tertiaryContainer = Color(0xFF85002F),
    onTertiaryContainer = Color(0xFFFFE4E9),
    background = Color(0xFF1D1B1E),
    onBackground = Color(0xFFFFE4E9),
    surface = Color(0xFF1D1B1E),
    onSurface = Color(0xFFFFE4E9),
    surfaceVariant = Color(0xFF4A001F),
    onSurfaceVariant = Color(0xFFFFAFC8),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFFFFAFC8) // Soft Pink
)

// Pastel Yellow Scheme
val LightYellowScheme = lightColorScheme(
    primary = Color(0xFFFFF9C4), // Pastel Yellow
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFFFFDE7), // Lighter Pastel Yellow
    onPrimaryContainer = Color(0xFF3F3F00), // Dark Yellow
    secondary = Color(0xFFFFF59D), // Soft Yellow
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFFDE7),
    onSecondaryContainer = Color(0xFF3F3F00),
    tertiary = Color(0xFFFFF8E1), // Light Pastel Yellow
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFFFDE7),
    onTertiaryContainer = Color(0xFF3F3F00),
    background = Color(0xFFFFFFF5), // Very Light Yellow Background
    onBackground = Color(0xFF1D1B1E),
    surface = Color(0xFFFFFFF5),
    onSurface = Color(0xFF1D1B1E),
    surfaceVariant = Color(0xFFFFFDE7),
    onSurfaceVariant = Color(0xFF3F3F00),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFFFFF59D) // Soft Yellow
)

val DarkYellowScheme = darkColorScheme(
    primary = Color(0xFFFFF59D), // Soft Yellow
    onPrimary = Color(0xFF3F3F00),
    primaryContainer = Color(0xFF787800), // Dark Yellow
    onPrimaryContainer = Color(0xFFFFFDE7),
    secondary = Color(0xFFFFF176), // Deeper Pastel Yellow
    onSecondary = Color(0xFF3F3F00),
    secondaryContainer = Color(0xFF787800),
    onSecondaryContainer = Color(0xFFFFFDE7),
    tertiary = Color(0xFFFFF8E1),
    onTertiary = Color(0xFF3F3F00),
    tertiaryContainer = Color(0xFF787800),
    onTertiaryContainer = Color(0xFFFFFDE7),
    background = Color(0xFF1D1B1E),
    onBackground = Color(0xFFFFFDE7),
    surface = Color(0xFF1D1B1E),
    onSurface = Color(0xFFFFFDE7),
    surfaceVariant = Color(0xFF787800),
    onSurfaceVariant = Color(0xFFFFF59D),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFFFFF59D) // Soft Yellow
)

// Lavender Blue Scheme
val LightBlueScheme = lightColorScheme(
    primary = Color(0xFFE6E6FA), // Lavender Blue
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFF5F5FF), // Lighter Lavender Blue
    onPrimaryContainer = Color(0xFF2F2F4F), // Darker Blue
    secondary = Color(0xFFD8BFD8), // Thistle
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFF5F5FF),
    onSecondaryContainer = Color(0xFF2F2F4F),
    tertiary = Color(0xFFE0FFFF), // Light Cyan
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFF5F5FF),
    onTertiaryContainer = Color(0xFF2F2F4F),
    background = Color(0xFFF0F8FF), // Alice Blue Background
    onBackground = Color(0xFF1D1B1E),
    surface = Color(0xFFF0F8FF),
    onSurface = Color(0xFF1D1B1E),
    surfaceVariant = Color(0xFFF5F5FF),
    onSurfaceVariant = Color(0xFF2F2F4F),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFFD8BFD8) // Thistle
)

val DarkBlueScheme = darkColorScheme(
    primary = Color(0xFFD8BFD8), // Thistle
    onPrimary = Color(0xFF2F2F4F),
    primaryContainer = Color(0xFF8A2BE2), // BlueViolet
    onPrimaryContainer = Color(0xFFF5F5FF),
    secondary = Color(0xFF9370DB), // MediumPurple
    onSecondary = Color(0xFF2F2F4F),
    secondaryContainer = Color(0xFF8A2BE2),
    onSecondaryContainer = Color(0xFFF5F5FF),
    tertiary = Color(0xFF8A2BE2),
    onTertiary = Color(0xFF2F2F4F),
    tertiaryContainer = Color(0xFF9370DB),
    onTertiaryContainer = Color(0xFFF5F5FF),
    background = Color(0xFF1D1B1E),
    onBackground = Color(0xFFF5F5FF),
    surface = Color(0xFF1D1B1E),
    onSurface = Color(0xFFF5F5FF),
    surfaceVariant = Color(0xFF2F2F4F),
    onSurfaceVariant = Color(0xFFD8BFD8),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFFD8BFD8) // Thistle
)

// Crimson Red Scheme
val LightRedScheme = lightColorScheme(
    primary = Color(0xFFDC143C), // Crimson Red
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE6E6), // Lighter Crimson
    onPrimaryContainer = Color(0xFF3F000F), // Dark Red
    secondary = Color(0xFFFF7F7F), // Salmon
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE6E6),
    onSecondaryContainer = Color(0xFF3F000F),
    tertiary = Color(0xFFFFA07A), // Light Salmon
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFFE6E6),
    onTertiaryContainer = Color(0xFF3F000F),
    background = Color(0xFFFFF0F0), // Very Light Red Background
    onBackground = Color(0xFF1D1B1E),
    surface = Color(0xFFFFF0F0),
    onSurface = Color(0xFF1D1B1E),
    surfaceVariant = Color(0xFFFFE6E6),
    onSurfaceVariant = Color(0xFF3F000F),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFFFF7F7F) // Salmon
)

val DarkRedScheme = darkColorScheme(
    primary = Color(0xFFFF7F7F), // Salmon
    onPrimary = Color(0xFF3F000F),
    primaryContainer = Color(0xFF8B0000), // Dark Red
    onPrimaryContainer = Color(0xFFFFE6E6),
    secondary = Color(0xFFFF6347), // Tomato
    onSecondary = Color(0xFF3F000F),
    secondaryContainer = Color(0xFF8B0000),
    onSecondaryContainer = Color(0xFFFFE6E6),
    tertiary = Color(0xFFFFA07A),
    onTertiary = Color(0xFF3F000F),
    tertiaryContainer = Color(0xFF8B0000),
    onTertiaryContainer = Color(0xFFFFE6E6),
    background = Color(0xFF1D1B1E),
    onBackground = Color(0xFFFFE6E6),
    surface = Color(0xFF1D1B1E),
    onSurface = Color(0xFFFFE6E6),
    surfaceVariant = Color(0xFF3F000F),
    onSurfaceVariant = Color(0xFFFF7F7F),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFFFF7F7F) // Salmon
)

// Emerald Green Scheme
val LightGreenScheme = lightColorScheme(
    primary = Color(0xFF50C878), // Emerald Green
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6FFE6), // Lighter Emerald Green
    onPrimaryContainer = Color(0xFF003D00), // Dark Green
    secondary = Color(0xFF98FB98), // Pale Green
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFE6FFE6),
    onSecondaryContainer = Color(0xFF003D00),
    tertiary = Color(0xFFBDFCC9), // Light Green
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFE6FFE6),
    onTertiaryContainer = Color(0xFF003D00),
    background = Color(0xFFF0FFF0), // Very Light Green Background
    onBackground = Color(0xFF1D1B1E),
    surface = Color(0xFFF0FFF0),
    onSurface = Color(0xFF1D1B1E),
    surfaceVariant = Color(0xFFE6FFE6),
    onSurfaceVariant = Color(0xFF003D00),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF98FB98) // Pale Green
)

val DarkGreenScheme = darkColorScheme(
    primary = Color(0xFF98FB98), // Pale Green
    onPrimary = Color(0xFF003D00),
    primaryContainer = Color(0xFF004F00), // Dark Green
    onPrimaryContainer = Color(0xFFE6FFE6),
    secondary = Color(0xFF32CD32), // Lime Green
    onSecondary = Color(0xFF003D00),
    secondaryContainer = Color(0xFF004F00),
    onSecondaryContainer = Color(0xFFE6FFE6),
    tertiary = Color(0xFFBDFCC9),
    onTertiary = Color(0xFF003D00),
    tertiaryContainer = Color(0xFF004F00),
    onTertiaryContainer = Color(0xFFE6FFE6),
    background = Color(0xFF1D1B1E),
    onBackground = Color(0xFFE6FFE6),
    surface = Color(0xFF1D1B1E),
    onSurface = Color(0xFFE6FFE6),
    surfaceVariant = Color(0xFF003D00),
    onSurfaceVariant = Color(0xFF98FB98),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF98FB98) // Pale Green
)
