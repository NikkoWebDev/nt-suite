#!/usr/bin/env python3
"""Mint Glass 2.0 sweep helper.

Replaces legacy hardcoded colours in one screen file with MaterialTheme scheme
references. Import lines are left untouched; a missing MaterialTheme import is
inserted automatically.
Usage: redesign-tokens.py <file.kt>
"""

import pathlib
import re
import sys

TABLE = [
    (
        "r'Color\\(AppBackground\\.toArgb\\(\\)\\)'",
        "MaterialTheme.colorScheme.background",
    ),
    ("Color(0xFF0E0E13)", "MaterialTheme.colorScheme.surface"),
    ("Color(0xFF121212)", "MaterialTheme.colorScheme.surface"),
    ("Color(0xFF1A1A1A)", "MaterialTheme.colorScheme.surfaceContainer"),
    ("Color(0xFF1A1A20)", "MaterialTheme.colorScheme.surfaceContainer"),
    ("Color(0xFF333333)", "MaterialTheme.colorScheme.outlineVariant"),
    ("Color(0xFF3A3A46)", "MaterialTheme.colorScheme.outlineVariant"),
    ("Color(0xFF72728A)", "MaterialTheme.colorScheme.onSurfaceVariant"),
    ("Color(0xFFE57373)", "MaterialTheme.colorScheme.error"),
    ("Color(0xFFFF5252)", "MaterialTheme.colorScheme.error"),
    ("Color(0xFFB3B3B3)", "MaterialTheme.colorScheme.onSurfaceVariant"),
    ("Color(0xFF142B21)", "MaterialTheme.colorScheme.surfaceContainerHigh"),
    ("Color(0xFF09140E)", "MaterialTheme.colorScheme.surfaceContainerHigh"),
    ("Color(0xFF8ABFA3)", "MaterialTheme.colorScheme.onSurfaceVariant"),
    ("Color(0xFF3DFFA0)", "MaterialTheme.colorScheme.primary"),
    ("Color(0xFF070D0A)", "MaterialTheme.colorScheme.onPrimary"),
    ("Color.White", "MaterialTheme.colorScheme.onBackground"),
    ("Color.Gray", "MaterialTheme.colorScheme.onSurfaceVariant"),
    ("MintPrimary", "MaterialTheme.colorScheme.primary"),
    ("AppPalette", "MaterialTheme.colorScheme.primary"),
    ("TextPrimary", "MaterialTheme.colorScheme.onBackground"),
    ("TextSecondary", "MaterialTheme.colorScheme.onSurfaceVariant"),
    ("SurfaceDark", "MaterialTheme.colorScheme.surface"),
    (
        "GlassSurface",
        "MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.35f)",
    ),
    ("GlassBorderMint", "MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)"),
    ("GlassBorder", "MaterialTheme.colorScheme.outlineVariant"),
    ("MintDeep", "MaterialTheme.colorScheme.primaryContainer"),
    ("GridBackground", "MaterialTheme.colorScheme.surfaceVariant"),
    ("AppBackground", "MaterialTheme.colorScheme.background"),
]


def main() -> None:
    path = pathlib.Path(sys.argv[1])
    lines = path.read_text().splitlines()
    out = []
    for line in lines:
        if line.startswith("import "):
            out.append(line)
            continue
        for old, new in TABLE:
            line = line.replace(old, new)
        out.append(line)
    text = "\n".join(out) + "\n"
    if not re.search(
        r"^import androidx\.compose\.material3\.MaterialTheme$", text, re.M
    ):
        lines = text.splitlines()
        # Insert after the last non-theme import to keep grouping tidy.
        idx = 0
        for i, l in enumerate(lines):
            if l.startswith("import ") and not l.startswith("import com.music"):
                idx = i
        lines.insert(idx + 1, "import androidx.compose.material3.MaterialTheme")
        text = "\n".join(lines) + "\n"
    path.write_text(text)
    print(f"tokenized: {path}")


if __name__ == "__main__":
    main()
