package com.chaaraapp.wadii.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.wadii.theme.HintColor
import com.wadii.theme.SpaceCard
import com.wadii.theme.SpaceSurface
import com.wadii.theme.SpaceVoid
import com.wadii.theme.StarWhite
import com.wadii.theme.Yellow

data class SpaceTheme(
    val isDark: Boolean,
    val bg: Color,
    val surface: Color,
    val card: Color,
    val text: Color,
    val textDim: Color,
    val accent: Color,
    val iconTint: Color,
    val navUnselectedIcon: Color,
    val navUnselectedText: Color,
    val headerColors: List<Color>,
    val divider: Color,
    val searchBg: Color,
    val searchBorder: Color,
)

val DarkSpaceTheme = SpaceTheme(
    isDark = true,
    bg = SpaceVoid,
    surface = SpaceSurface,
    card = SpaceCard,
    text = StarWhite,
    textDim = HintColor,
    accent = Yellow,
    iconTint = Color.White,
    navUnselectedIcon = Color.White.copy(alpha = 0.55f),
    navUnselectedText = Color.White.copy(alpha = 0.40f),
    headerColors = listOf(Color(0xFF0F1E38), Color(0xFF1A1040), Color(0xFF0C1E3A)),
    divider = Color.White.copy(alpha = 0.08f),
    searchBg = Color.White.copy(alpha = 0.09f),
    searchBorder = Color.White.copy(alpha = 0.18f),
)

// Light space theme: "Pearl Cosmos" — nebula at dawn
val LightSpaceTheme = SpaceTheme(
    isDark = false,
    bg = Color(0xFFF5EEFF),
    surface = Color(0xFFEDE6FF),
    card = Color(0xFFF8F4FF),
    text = Color(0xFF0E0527),
    textDim = Color(0xFF7060A0),
    accent = Yellow,
    iconTint = Color(0xFF0E0527),
    // Nav bar is always dark (deep indigo anchor), so unselected stays white-based
    navUnselectedIcon = Color.White.copy(alpha = 0.55f),
    navUnselectedText = Color.White.copy(alpha = 0.40f),
    headerColors = listOf(Color(0xFFCFBEFF), Color(0xFFBFA8FF), Color(0xFFCDBEFF)),
    divider = Color(0xFF0E0527).copy(alpha = 0.08f),
    searchBg = Color(0xFF0E0527).copy(alpha = 0.06f),
    searchBorder = Color(0xFF0E0527).copy(alpha = 0.14f),
)

object ThemeState {
    private val _isDark = mutableStateOf(true)

    // Reading this inside a @Composable registers a snapshot observation
    val isDark: Boolean get() = _isDark.value

    fun initialize(dark: Boolean) { _isDark.value = dark }
    fun setDark(dark: Boolean) { _isDark.value = dark }
}

val LocalSpaceTheme = compositionLocalOf { DarkSpaceTheme }
