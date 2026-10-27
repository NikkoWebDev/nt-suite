package com.music.spotui.ui.theme

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.music.spotui.data.preferences.getThemeMode
import com.music.spotui.data.preferences.keyThemeMode

/**
 * Mode token bundle: every non-scheme colour a screen or glass surface needs,
 * resolved once per dark/light turn. Screens read this instead of raw literals.
 */
@Immutable
data class MintTokens(
    val isDark: Boolean,
    val primary: Color,
    val accent: Color,
    val glow: Color,
    val deep: Color,
    val onPrimary: Color,
    val background: Color,
    val surface: Color,
    val grid: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val glassBorder: Color,
    val glassBorderMint: Color,
    val glassSurface: Color,
    val glowInner: Color,
    val glowOuter: Color,
)

private fun mintTokensDark() = MintTokens(
    isDark = true,
    primary = MintPrimary,
    accent = MintAccent,
    glow = MintGlow,
    deep = MintDeep,
    onPrimary = MintOnPrimaryDark,
    background = AppBackground,
    surface = SurfaceDark,
    grid = GridBackground,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    glassBorder = GlassBorder,
    glassBorderMint = GlassBorderMint,
    glassSurface = GlassSurface,
    glowInner = GlowInnerDark,
    glowOuter = GlowOuterDark,
)

private fun mintTokensLight() = MintTokens(
    isDark = false,
    primary = MintPrimaryLight,
    accent = MintAccentLight,
    glow = MintGlowLight,
    deep = Color(0xFFC7F6DC),
    onPrimary = MintOnPrimaryLight,
    background = LightBackground,
    surface = LightSurface,
    grid = LightGrid,
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    glassBorder = LightGlassBorder,
    glassBorderMint = MintGlowLight,
    glassSurface = LightGlassFill,
    glowInner = LightGlowInner,
    glowOuter = LightGlowOuter,
)

val LocalMint = staticCompositionLocalOf { mintTokensDark() }

private val MintDarkColorScheme = darkColorScheme(
    primary = MintPrimary,
    onPrimary = MintOnPrimaryDark,
    primaryContainer = MintDeep,
    onPrimaryContainer = MintPrimary,
    secondary = MintAccent,
    onSecondary = MintOnPrimaryDark,
    secondaryContainer = MintDeep,
    onSecondaryContainer = Color(0xFFB7F6D6),
    tertiary = MintGlow,
    onTertiary = Color(0xFF031C10),
    tertiaryContainer = MintDeep,
    onTertiaryContainer = Color(0xFFB7F6D6),
    background = AppBackground,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = GridBackground,
    onSurfaceVariant = TextSecondary,
    surfaceDim = Color(0xFF0A120D),
    surfaceBright = Color(0xFF15241C),
    surfaceContainerLowest = Color(0xFF070D0A),
    surfaceContainerLow = Color(0xFF0E1B15),
    surfaceContainer = GridBackground,
    surfaceContainerHigh = Color(0xFF142B21),
    surfaceContainerHighest = Color(0xFF1A3428),
    outline = Color(0xFF3D5A4B),
    outlineVariant = Color(0xFF1FFFFFFF),
    inverseSurface = TextPrimary,
    inverseOnSurface = AppBackground,
    inversePrimary = MintPrimaryLight,
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF2B0A08),
    errorContainer = Color(0xFF3A1210),
    onErrorContainer = Color(0xFFFFB4AB),
    scrim = AppBackground,
    surfaceTint = MintPrimary,
)

private val MintLightColorScheme = lightColorScheme(
    primary = MintPrimaryLight,
    onPrimary = MintOnPrimaryLight,
    primaryContainer = Color(0xFFC7F6DC),
    onPrimaryContainer = Color(0xFF06452B),
    secondary = MintAccentLight,
    onSecondary = MintOnPrimaryLight,
    secondaryContainer = Color(0xFFC9F4DC),
    onSecondaryContainer = Color(0xFF063B25),
    tertiary = MintGlowLight,
    onTertiary = MintOnPrimaryLight,
    tertiaryContainer = Color(0xFFC4F1DB),
    onTertiaryContainer = Color(0xFF064A2F),
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightGrid,
    onSurfaceVariant = TextSecondaryLight,
    surfaceDim = Color(0xFFDDEBE2),
    surfaceBright = LightSurface,
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF6FBF8),
    surfaceContainer = Color(0xFFECF6F0),
    surfaceContainerHigh = Color(0xFFDFF0E7),
    surfaceContainerHighest = Color(0xFFD3EAE0),
    outline = Color(0xFF3D5A4B),
    outlineVariant = Color(0xFFBFD6C9),
    inverseSurface = SurfaceDark,
    inverseOnSurface = LightBackground,
    inversePrimary = MintPrimary,
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFCD8DC),
    onErrorContainer = Color(0xFF410E0B),
    scrim = Color(0xFF072516),
    surfaceTint = MintPrimaryLight,
)

@Composable
fun mintColorScheme(dark: Boolean) = if (dark) MintDarkColorScheme else MintLightColorScheme

@Composable
fun SpotuiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Dynamic colour stays off: Mint Glass is the identity
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    // Manual mode from Settings wins over the system; SYSTEM follows darkTheme.
    var themeMode by remember { mutableStateOf(getThemeMode(context)) }
    DisposableEffect(context) {
        val prefs: SharedPreferences = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == keyThemeMode) themeMode = getThemeMode(context)
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    val dark = when (themeMode.name) {
        "DARK" -> true
        "LIGHT" -> false
        else -> darkTheme
    }
    val colorScheme = mintColorScheme(dark)
    val tokens = remember(dark) { if (dark) mintTokensDark() else mintTokensLight() }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !dark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !dark
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(LocalMint provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = {
                androidx.compose.material3.ProvideTextStyle(
                    value = androidx.compose.material3.LocalTextStyle.current.copy(
                        fontFamily = Montserrat,
                        letterSpacing = (-0.2).sp,
                        color = tokens.textPrimary,
                    ),
                    content = content,
                )
            },
        )
    }
}
