package com.music.spotui.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeChild
import com.music.spotui.ui.theme.LocalMint
import com.music.spotui.ui.theme.LightGlassBorder
import com.music.spotui.ui.theme.LightGlassHighlight

/**
 * Shared blur source for the whole app. App.kt (hosting root) applies the single
 * Modifier.haze pass over the NavHost content Box. ONLY surfaces composed
 * outside that Box (Scaffold bottomBar: nav pill, MiniPlayer) may register as
 * haze children — a hazeChild drawn inside the source blurs itself
 * (self-blur on API 32+, alpha-boosted scrim veil on API ≤ 31).
 * haze 0.7.3 behavior: real blur only from API 32 (RenderNode impl is gated
 * off on API 31 by chrisbanes/haze#77; anything lower renders the blend
 * scrim), which is why scrim-glass rendering exists in GlassCard too.
 */
val LocalHazeState = staticCompositionLocalOf<HazeState?> { null }

/**
 * Glass steepness ladder (Mint Glass 2.0 spec):
 *  G0 flat opaque surface — sheets/popups;
 *  G1 tinted — 0.55 tinted fill, 1dp 0.16 border, no blur;
 *  G2 soft glass — translucent fill, gradient border 0.28->0.06, no blur;
 *  G3 real glass — 24dp background blur + fill (hazeChild);
 *  G4 hero glass — 32dp blur, mint-tinted fill, mint edge + spot glow shadow.
 *
 * RULE: hazeChild blur is legal ONLY for surfaces composed OUTSIDE the
 * Modifier.haze source box (Scaffold bottomBar/overlays in App.kt). Any
 * GlassCard inside the NavHost content must pass [allowBlur] = false — a
 * child of the haze source blurs itself (real self-blur on API 32+, an
 * alpha-boosted haze scrim veil below). With [allowBlur] = false or no haze
 * host available, G3/G4 render as scrim glass: a progressive background
 * veil, same recipe as the bottom-nav scrim; glass border, grade fill and
 * the G4 mint edge/glow shadow stay intact.
 */
enum class GlassGrade(internal val blur: Dp?) {
    G0(null),
    G1(null),
    G2(null),
    G3(24.dp),
    G4(32.dp),
}

/** Spotin't GlassCard — gradable glassmorphism container, dark/light aware. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    grade: GlassGrade = GlassGrade.G2,
    cornerRadius: Dp = 22.dp,
    shape: Shape = RoundedCornerShape(cornerRadius),
    glowBorder: Boolean = false, // kept for legacy call sites; overlaps with G4's mint edge
    allowBlur: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val tokens = LocalMint.current
    val hazeState = LocalHazeState.current

    // Fill per grade. Dark uses low-alpha white over content; light uses an
    // ~0.62 white veil so glass reads as glass on a bright background.
    val fill: Color = when (grade) {
        GlassGrade.G0 -> tokens.surface
        GlassGrade.G1 -> tokens.surface.copy(alpha = 0.55f)
        GlassGrade.G2 -> Color.White.copy(alpha = if (tokens.isDark) 0.08f else 0.62f)
        GlassGrade.G3 -> Color.White.copy(alpha = if (tokens.isDark) 0.10f else 0.62f)
        GlassGrade.G4 -> {
            val base = Color.White.copy(alpha = if (tokens.isDark) 0.12f else 0.62f)
            // Hero grade carries a +5% mint tinge in its fill.
            base.compositeOver(tokens.primary.copy(alpha = if (tokens.isDark) 0.05f else 0.03f))
        }
    }

    val borderBrush: Brush = when {
        grade == GlassGrade.G0 -> Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
        grade == GlassGrade.G1 ->
            if (tokens.isDark) Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.16f)))
            else Brush.horizontalGradient(listOf(LightGlassBorder.copy(alpha = 0.16f)))
        grade == GlassGrade.G4 ->
            // Mint-tinted edge with a sheen falloff.
            if (tokens.isDark) Brush.linearGradient(
                listOf(tokens.glassBorderMint, tokens.primary.copy(alpha = 0.20f))
            )
            else Brush.linearGradient(
                listOf(tokens.glow.copy(alpha = 0.55f), LightGlassHighlight.copy(alpha = 0.90f))
            )
        else ->
            if (tokens.isDark) Brush.verticalGradient(
                listOf(Color.White.copy(alpha = 0.28f), Color.White.copy(alpha = 0.06f))
            )
            else Brush.verticalGradient(
                listOf(LightGlassHighlight.copy(alpha = 0.55f), LightGlassBorder.copy(alpha = 0.14f))
            )
    }
    val borderWidth = when (grade) {
        GlassGrade.G0 -> 0.dp
        GlassGrade.G4 -> 1.2.dp
        else -> 1.dp
    }

    val elevation = when (grade) {
        GlassGrade.G0, GlassGrade.G1 -> 0.dp
        GlassGrade.G2 -> 8.dp
        GlassGrade.G3 -> 16.dp
        GlassGrade.G4 -> 24.dp
    }
    // Spot glow is a G4 signature. Pre-P (API < 28) keep a neutral shadow only:
    // ambientColor/spotColor are ignored by the platform before P.
    val shadowed = if (Build.VERSION.SDK_INT >= 28) {
        Modifier.shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.55f),
            spotColor = when {
                grade == GlassGrade.G4 -> tokens.primary.copy(alpha = 0.2f)
                tokens.isDark -> Color.Black.copy(alpha = 0.4f)
                else -> Color.Black.copy(alpha = 0.14f)
            },
        )
    } else {
        Modifier.shadow(elevation = elevation, shape)
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    } else Modifier

    val base = modifier
        .then(shadowed)
        .clip(shape)

    // Blur-or-scrim: when a haze host exists, the grade asks for blur and the
    // call site opted in, the glass fill rides on top of real 24/32dp blurred
    // content (real blur only from API 32 — haze 0.7.3 scrim fallback below).
    // Otherwise the card renders as scrim glass: a progressive background veil
    // like the bottom-nav scrim (the in-NavHost rule, see class KDoc).
    val glassed = if (grade.blur != null && hazeState != null && allowBlur) {
        base.hazeChild(
            hazeState,
            shape = shape,
            style = HazeStyle(tint = fill, blurRadius = grade.blur, noiseFactor = 0.10f),
        )
    } else {
        if (grade.blur == null) base.background(fill)
        else base.background(
            Brush.verticalGradient(
                listOf(
                    tokens.background.copy(alpha = 0.0f),
                    tokens.background.copy(alpha = 0.55f),
                    tokens.background.copy(alpha = 0.72f),
                )
            )
        )
    }

    Box(
        modifier = glassed
            .border(width = borderWidth, brush = borderBrush, shape = shape)
            .then(clickableModifier),
        content = content,
    )
}
