package com.music.spotui.ui.theme

import androidx.compose.ui.graphics.Color

// Spotin't "Mint Glass 2.0" — canonical palette. Dark preserves the 1.x identity;
// light is the new contrast-safe companion. Every app colour must be reachable
// from here (tokens) or from MaterialTheme.colorScheme — no raw literals in screens.

// ===== DARK (canonical) =====
val MintPrimary = Color(0xFF3DFFA0)         // Vibrant mint — primary dark CTA/glow
val MintAccent = Color(0xFF00E87A)          // CTA accent + glowing edges
val MintGlow = Color(0xFF1DB87A)            // Soft mint radiance
val MintDeep = Color(0xFF052E1B)            // Deep green shadow / containers

val AppBackground = Color(0xFF070D0A)       // Near-black emerald-tinted base
val SurfaceDark = Color(0xFF0D1A14)         // Glass base surface
val GridBackground = Color(0xFF13241C)      // Grid / card backdrop
val AppPalette = Color(0xFF3DFFA0)          // Legacy alias of MintPrimary

// Glassmorphism overlays (dark)
val GlassSurface = Color(0x1AFFFFFF)        // White 10% glass fill
val GlassSurfaceHighlight = Color(0x263DFFA0) // Mint 15% highlight
val GlassBorder = Color(0xFF33FFFFFF)       // White 20% border
val GlassBorderMint = Color(0xFF663DFFA0)   // Mint 40% radiant border

// Dark text
val TextPrimary = Color(0xFFF2FFF8)         // Mint-white
val TextSecondary = Color(0xFF8ABFA3)       // Muted mint

// Radial background glow (dark): inner -> outer
val GlowInnerDark = Color(0xFF0D2E1B)
val GlowOuterDark = AppBackground

// Text over artwork stays legible in both modes (art is its own world).
val TextOnArtwork = Color(0xFFFFFFFF)
val TextOnArtworkMuted = Color(0xCCFFFFFF)
val TextOnArtworkSubtle = Color(0x99FFFFFF)
// Black fades/scrims compositing over artwork (player canvas, hero headers).
val ArtworkFadeBlack = Color(0xFF000000)

// ===== LIGHT (new) =====
val LightBackground = Color(0xFFF4FAF6)
val LightSurface = Color(0xFFFFFFFF)
val LightGrid = Color(0xFFE3F2E9)
val LightGlowInner = Color(0xFFDCF3E7)
val LightGlowOuter = LightBackground

// Light-mode mints: darkened enough that AA holds — white/graphic ≥ 3:1 on
// light containers, and the ink they carry is the deep mint ink below
// (#04150C), never white. Raw #3DFFA0 never carries text on light surfaces.
val MintPrimaryLight = Color(0xFF059B5F)
val MintAccentLight = Color(0xFF04944F)
val MintGlowLight = Color(0xFF2FBF86)

val TextPrimaryLight = Color(0xFF0B2317)
val TextSecondaryLight = Color(0xFF4F6E5F)

// Light glass: opaque-ish white fill (~62% when used), dark-green border + white sheen.
val LightGlassFill = Color(0xFFFFFFFF)
val LightGlassBorder = Color(0xFF072516)    // Used at ~12% alpha by glass grades
val LightGlassHighlight = Color(0xFFFFFFFF) // Used at ~90% alpha on top edges

val MintOnPrimaryDark = Color(0xFF031C10)   // Ink on dark-mode mint
val MintOnPrimaryLight = Color(0xFF04150C)  // Ink on light-mode mint: deep mint ink, never white

// Search "Browse all" corporate tiles: mint-family tonal ramp replacing the
// Spotify multi-hue grid. Ink per tile is fixed by measured WCAG contrast:
// the bright half (A–E) takes the deep ink, the deep end (F–H) takes white.
val MintTileA = Color(0xFF06BE70)
val MintTileB = Color(0xFF059B5F)
val MintTileC = Color(0xFF2FBF86)
val MintTileD = Color(0xFF14A878)
val MintTileE = Color(0xFF1DB87A)
val MintTileF = Color(0xFF07693F)
val MintTileG = Color(0xFF067D4C)
val MintTileH = Color(0xFF12503A)

// Third-party brand identity — icon tints and service cards only, never app chrome.
val BrandYoutubeRed = Color(0xFFFF0033)
val BrandDeezerPurple = Color(0xFFA238FF)
val BrandTidalTeal = Color(0xFF00C7B7)
// Verified badge (Apple-blue) — artwork-adjacent chip.
val BrandVerifiedBlue = Color(0xFF4A90E2)
// Liked-songs header gradient (artwork zone, artwork-energy purple).
val LikedSongsPurpleA = Color(0xFF8E6FE0)
val LikedSongsPurpleB = Color(0xFF3B2A82)

// ===== Scheme-legacy aliases (kept so older references compile; dark values) =====
val Purple80 = MintPrimary
val PurpleGrey80 = TextSecondary
val Pink80 = MintAccent
val Purple40 = MintGlow
val PurpleGrey40 = SurfaceDark
val Pink40 = MintDeep
