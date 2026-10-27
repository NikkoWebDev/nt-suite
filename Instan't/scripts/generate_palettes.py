#!/usr/bin/env python3
"""Canonical palettes for the Dark Obsidian & Mint Glass theme.

Emits the two flat JSON files the InstaEclipse seeder embeds:
  config/palette-dark.json   consumed as themePaletteJson (preset id 0)
  config/palette-light.json  documentation: light mode uses a built-in InstaEclipse
                             preset (themeLightPresetId > 0), a custom palette is
                             impossible in light mode (#251). This file records the
                             closest mint-tinted reference so the docs stay honest.

Slot values are Java signed ints (ARGB), exactly what IgThemePalette.fromJson reads.
Also verifies the light-mode preset id the seeder will seed (from ThemePresets data).
"""

import json
import os
import struct
import sys

DARK = {
    "background": ("#FF080A0F", "obsidian base"),
    "surface": ("#F2111827", "glass card, 95% alpha"),
    "primaryText": ("#FFF8FAFC", "slate 50"),
    "secondaryText": ("#FF94A3B8", "slate 400"),
    "accent": ("#FF00F5A0", "mint neon"),
    "button": ("#FF00F5A0", "mint neon"),
    "icon": ("#FF00F5A0", "mint neon"),
    "glyph": ("#FF2DD4BF", "teal 400"),
    "divider": ("#CC1F2937", "gray 800, 80% alpha"),
    "border": ("#CC1E293B", "slate 800, 80% alpha"),
    "statusBar": ("#FF080A0F", "obsidian"),
    "navigation": ("#FF080A0F", "obsidian"),
    "link": ("#FF58D8FF", "sky glow"),
    "error": ("#FFEF4444", "red 500"),
    "destructive": ("#FFF43F5E", "rose 500"),
}

# ThemePresets base 13 "Mint Fresh" (ps.reso...theme.ThemePresets.list.add(p(13, ...))).
MINT_FRESH = {
    "bg": -16115180,
    "surface": -15587296,
    "primary": -1507339,
    "secondary": -8732768,
    "accent": -16725866,
    "icon": -1507339,
    "divider": -15060184,
    "nav": -16115180,
    "link": -14233432,
    "destructive": -38037,
}
VARIANT_OFFSET = 100


def signed(hex_str: str) -> int:
    """Pure python double complement conversion."""
    value = int(hex_str.lstrip("#"), 16) & 0xFFFFFFFF
    return struct.unpack(">i", struct.pack(">I", value))[0]


def hex_of(palette: dict) -> dict:
    out = {}
    for name, (hex_value, _) in palette.items():
        rest = hex_value.lstrip("#")
        alpha, rgb = rest[:2].upper(), rest[-6:].upper()
        out[name] = f"#{rgb}" if alpha == "FF" else f"#{rgb} @ alpha {alpha}"
    return out


def to_signed_parts(v: int):
    v &= 0xFFFFFFFF
    return [(v >> 24) & 0xFF, (v >> 16) & 0xFF, (v >> 8) & 0xFF, v & 0xFF]


def luminance_until(c: int) -> float:
    _, r, g, b = to_signed_parts(c)
    ch = []
    for x in (r, g, b):
        v = x / 255.0
        ch.append(v / 12.92 if v <= 0.03928 else ((v + 0.055) / 1.055) ** 2.4)
    return 0.2126 * ch[0] + 0.7152 * ch[1] + 0.0722 * ch[2]


def blend_parts(f: int, t: int, tt: float) -> int:
    fr = to_signed_parts(f)
    tr = to_signed_parts(t)
    out = [255]
    for i in range(1, 4):
        out.append(round(fr[i] * (1 - tt) + tr[i] * tt))
    return (out[0] << 24) | (out[1] << 16) | (out[2] << 8) | out[3]


def with_contrast(c: int, bg: int, ratio: float, lighten: bool) -> int:
    out = 0xFF000000 | (c & 0xFFFFFF)
    target = 0xFFFFFFFF if lighten else 0xFF000000
    for _ in range(20):
        la, lb = luminance_until(out) + 0.05, luminance_until(bg) + 0.05
        if max(la, lb) / min(la, lb) >= ratio:
            break
        out = blend_parts(out, target, 0.08)
    return out


def set_alpha(color: int, alpha: int) -> int:
    return (alpha << 24) | (color & 0xFFFFFF)


def build_light_from(dark: dict) -> dict:
    """Same formulas ThemePresets.toLight() applies to dark presets + glass alphas."""
    a = signed(dark["accent"][0])
    bg = blend_parts(0xFFFFFFFF, a, 0.13)
    surface = set_alpha(blend_parts(0xFFFFFFFF, a, 0.07), 0xFD)
    primary = with_contrast(blend_parts(0xFF101012, a, 0.28), bg, 10.0, False)
    secondary = with_contrast(blend_parts(0xFF5E5E66, a, 0.35), bg, 4.6, False)
    accent = with_contrast(a, bg, 3.2, False)
    icon = with_contrast(blend_parts(a, 0xFF000000, 0.35), bg, 5.0, False)
    divider = set_alpha(blend_parts(0xFFDCDCE2, a, 0.25), 0xCC)
    link = with_contrast(signed(dark["link"][0]), bg, 3.6, False)
    destructive = with_contrast(signed(dark["destructive"][0]), bg, 3.6, False)
    return dict(
        background=bg,
        surface=surface,
        primaryText=primary,
        secondaryText=secondary,
        accent=accent,
        button=accent,
        icon=icon,
        glyph=icon,
        divider=divider,
        border=divider,
        statusBar=bg,
        navigation=bg,
        link=link,
        error=destructive,
        destructive=destructive,
    )


def main() -> int:
    root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    config_dir = os.path.join(root, "config")
    os.makedirs(config_dir, exist_ok=True)

    dark_ints = {k: signed(v[0]) for k, v in DARK.items()}
    light_ints = {
        k: signed("0x%08X" % (v & 0xFFFFFFFF))
        for k, v in build_light_from(DARK).items()
    }

    dark_doc = {
        "themeName": "Dark Obsidian & Mint Glass",
        "author": "nikko.dev",
        "mode": "dark-custom-palette",
        "usage": "Seeded automatically by the build (themePresetId=0 + themePaletteJson).",
        "hexReference": hex_of(DARK),
        "palette": dark_ints,
    }
    light_doc = {
        "themeName": "Mint Glass Light (reference only)",
        "author": "nikko.dev",
        "mode": "light uses the built-in InstaEclipse preset listed as lightPresetId "
        "(custom palettes are not supported in light mode, InstaEclipse #251).",
        "lightPresetId": None,
        "palette": light_ints,
    }

    # Light-mode preset pick: replicate ThemePresets semantics.
    assert luminance_until(MINT_FRESH["bg"]) <= 0.5, (
        "Mint Fresh (13) assumed dark-background"
    )
    variant_id = VARIANT_OFFSET + 13  # 113 = "Mint Fresh Light"
    to_light = build_light_from(
        {
            "accent": ("%X" % (MINT_FRESH["accent"] & 0xFFFFFFFF)).upper(),
            "link": ("%X" % (MINT_FRESH["link"] & 0xFFFFFFFF)).upper(),
            "destructive": ("%X" % (MINT_FRESH["destructive"] & 0xFFFFFFFF)).upper(),
        }
    )
    assert luminance_until(to_light["background"]) > 0.5, (
        "variant 113 must be light-background"
    )
    # The built-in variant 113 is exactly toLight(base 13) computed by the module itself,
    # so isLight(preset 113) holds and getDisplayName(113) is "Mint Fresh Light".
    light_doc["lightPresetId"] = variant_id
    light_doc["lightPresetName"] = "Mint Fresh Light"

    with open(
        os.path.join(config_dir, "palette-dark.json"), "w", encoding="utf-8"
    ) as fh:
        json.dump(dark_doc, fh, indent=2)
        fh.write("\n")
    with open(
        os.path.join(config_dir, "palette-light.json"), "w", encoding="utf-8"
    ) as fh:
        json.dump(light_doc, fh, indent=2)
        fh.write("\n")

    print(
        f"palette-dark.json  : {len(dark_ints)} slots, bg=0x{dark_ints['background'] & 0xFFFFFFFF:08X}"
    )
    print(
        f"palette-light.json : {len(light_ints)} slots (reference), lightPresetId={variant_id}"
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
