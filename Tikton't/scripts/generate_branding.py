#!/usr/bin/env python3
"""
Tikton't Branding Asset Generator
Estética: Mint Glass & Dark Obsidian (#080A0F base, #00F5A0 neon mint, #58D8FF cyan, degradados translúcidos)
Genera iconos para Android: Adaptive Background, Adaptive Foreground, ic_launcher y ic_launcher_round.
"""

import os
import sys
import io
import shutil
import subprocess
from PIL import Image, ImageDraw, ImageFilter

DENSITIES = {
    "mipmap-mdpi": (48, 108),
    "mipmap-hdpi": (72, 162),
    "mipmap-xhdpi": (96, 216),
    "mipmap-xxhdpi": (144, 324),
    "mipmap-xxxhdpi": (192, 432),
}

# Paleta nikko.dev / Mint Glass & Dark Obsidian
OBSIDIAN_BG = (8, 10, 15)          # #080A0F Dark Obsidian
EMERALD_ACCENT = (11, 41, 32)      # #0B2920 Emerald Dark Glow
MINT_COLOR = (0, 245, 160)         # #00F5A0 Neon Mint
CYAN_COLOR = (88, 216, 255)        # #58D8FF Neon Cyan
GLASS_BORDER = (0, 245, 160, 160)  # Frosted Mint border
GLASS_SURFACE = (17, 24, 39, 230)  # #111827 Dark Glass

PATH_D = (
    "m176.4 82.54a33.89 33.89 0 0 1-2.93-1.706 41.2 41.2 0 0 1-7.526-6.394"
    "c-5.613-6.422-7.709-12.94-8.481-17.5h0.0311c-0.645-3.786-0.3784-6.236-0.338-6.236"
    "h-25.57v98.86c0 1.327 0 2.639-0.0558 3.935 0 0.1613-0.0155 0.3101-0.0249 0.4838"
    " 0 0.0713 0 0.1457-0.0155 0.2202v0.0558a21.71 21.71 0 0 1-10.92 17.23"
    " 21.34 21.34 0 0 1-10.58 2.791c-11.91 0-21.56-9.712-21.56-21.71"
    " 0-11.99 9.654-21.71 21.56-21.71a21.37 21.37 0 0 1 6.639 1.051l0.0311-26.03"
    "a47.49 47.49 0 0 0-36.59 10.7 50.17 50.17 0 0 0-10.95 13.5"
    "c-1.079 1.861-5.151 9.337-5.644 21.47-0.3101 6.887 1.758 14.02 2.744 16.97"
    "v0.062c0.6202 1.737 3.024 7.663 6.94 12.66a51.95 51.95 0 0 0 11.07 10.45"
    "v-0.062l0.06203 0.062c12.38 8.41 26.1 7.858 26.1 7.858 2.375-0.0961 10.33 0 19.37-4.283"
    " 10.02-4.748 15.73-11.82 15.73-11.82a49.14 49.14 0 0 0 8.571-14.24"
    "c2.313-6.081 3.086-13.37 3.086-16.29v-52.44c0.3101 0.1861 4.441 2.918 4.441 2.918"
    "s5.951 3.814 15.24 6.298c6.661 1.768 15.64 2.14 15.64 2.14v-25.38"
    "c-3.144 0.3411-9.53-0.6512-16.07-3.91z"
)

def render_vector_glyph(target_size):
    """
    Renderiza el glifo de Tikton't con precisión geométrica:
    - Offset cromático cian #58D8FF
    - Offset cromático mint neón (#3DFFA0 -> #00F5A0)
    - Núcleo Obsidian Glass translúcido (#111827 / #162235) con borde mint
    - Corte diagonal de cristal (crystal slit) y facetas reflectivas neón
    """
    svg = f"""<svg width="{target_size}" height="{target_size}" viewBox="0 0 256 256" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <linearGradient id="mintGrad" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#3DFFA0" />
          <stop offset="100%" stop-color="#00F5A0" />
        </linearGradient>

        <linearGradient id="glassBody" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#162235" stop-opacity="0.95" />
          <stop offset="100%" stop-color="#0B131F" stop-opacity="0.95" />
        </linearGradient>

        <mask id="cutMask">
          <rect x="0" y="0" width="256" height="256" fill="white" />
          <!-- Corte diagonal de cristal a través del vástago -->
          <polygon points="108,125 166,142 164,150 106,133" fill="black" />
        </mask>
      </defs>

      <g mask="url(#cutMask)">
        <!-- Sombra y acento cromático cian -->
        <path d="{PATH_D}" transform="translate(-4, -3)" fill="#58D8FF" opacity="0.95" />

        <!-- Acento cromático mint neón -->
        <path d="{PATH_D}" transform="translate(4, 3)" fill="url(#mintGrad)" opacity="0.95" />

        <!-- Núcleo central Dark Obsidian Glass -->
        <path d="{PATH_D}" fill="url(#glassBody)" stroke="#00F5A0" stroke-width="1.8" stroke-opacity="0.8" />
      </g>

      <!-- Líneas de faceta reflectiva luminosa en el corte de cristal -->
      <line x1="108" y1="125" x2="166" y2="142" stroke="#58D8FF" stroke-width="1.6" stroke-linecap="round" opacity="0.9" />
      <line x1="106" y1="133" x2="164" y2="150" stroke="#00F5A0" stroke-width="1.6" stroke-linecap="round" opacity="0.9" />
    </svg>"""

    if shutil.which("resvg"):
        p = subprocess.run(
            ["resvg", "--resources-dir", ".", "-", "-c"],
            input=svg.encode("utf-8"),
            stdout=subprocess.PIPE,
            check=True
        )
        return Image.open(io.BytesIO(p.stdout)).convert("RGBA")
    elif shutil.which("rsvg-convert"):
        p = subprocess.run(
            ["rsvg-convert", "-w", str(target_size), "-h", str(target_size)],
            input=svg.encode("utf-8"),
            stdout=subprocess.PIPE,
            check=True
        )
        return Image.open(io.BytesIO(p.stdout)).convert("RGBA")
    else:
        raise RuntimeError("No SVG rasterizer (resvg or rsvg-convert) found in environment")

def create_adaptive_background(size):
    """
    Genera el fondo adaptativo Android (108dp base):
    Dark Obsidian #080A0F con gradiente radial esmeralda #0B2920 en el centro.
    """
    bg = Image.new("RGBA", (size, size), OBSIDIAN_BG + (255,))
    cx, cy = size / 2, size / 2

    glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)

    spot_r = int(size * 0.32)
    glow_draw.ellipse([cx - spot_r, cy - spot_r, cx + spot_r, cy + spot_r], fill=EMERALD_ACCENT + (230,))

    mint_r = int(size * 0.18)
    glow_draw.ellipse([cx - mint_r, cy - mint_r, cx + mint_r, cy + mint_r], fill=MINT_COLOR + (48,))

    glow = glow.filter(ImageFilter.GaussianBlur(radius=size * 0.16))
    return Image.alpha_composite(bg, glow)

def create_adaptive_foreground(size):
    """
    Genera el foreground adaptativo Android (108dp base):
    Glifo centrado dentro de la safe zone de Android sobre fondo transparente.
    """
    glyph_sz = int(size * 0.90)
    glyph = render_vector_glyph(glyph_sz)

    fg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    gx = (size - glyph_sz) // 2
    gy = (size - glyph_sz) // 2
    fg.alpha_composite(glyph, (gx, gy))
    return fg

def create_legacy_squircle(icon_size):
    """
    Genera el icono estándar ic_launcher (Squircle redondeado):
    Dark Obsidian con borde Mint frosted glass y glifo central.
    """
    stroke_w = max(1, int(icon_size * 0.025))
    inset = stroke_w + 1
    box = [inset, inset, icon_size - inset, icon_size - inset]
    radius = int(icon_size * 0.22)

    base = Image.new("RGBA", (icon_size, icon_size), (0, 0, 0, 0))
    base_draw = ImageDraw.Draw(base)
    base_draw.rounded_rectangle(box, radius=radius, fill=OBSIDIAN_BG + (255,))

    glow = Image.new("RGBA", (icon_size, icon_size), (0, 0, 0, 0))
    g_draw = ImageDraw.Draw(glow)
    cx, cy = icon_size / 2, icon_size / 2
    r_glow = int(icon_size * 0.35)
    g_draw.ellipse([cx - r_glow, cy - r_glow, cx + r_glow, cy + r_glow], fill=EMERALD_ACCENT + (200,))
    r_mint = int(icon_size * 0.18)
    g_draw.ellipse([cx - r_mint, cy - r_mint, cx + r_mint, cy + r_mint], fill=MINT_COLOR + (50,))
    glow = glow.filter(ImageFilter.GaussianBlur(radius=icon_size * 0.14))

    mask = Image.new("L", (icon_size, icon_size), 0)
    m_draw = ImageDraw.Draw(mask)
    m_draw.rounded_rectangle(box, radius=radius, fill=255)
    empty = Image.new("RGBA", (icon_size, icon_size), (0, 0, 0, 0))
    glow_clipped = Image.composite(glow, empty, mask)

    img = Image.alpha_composite(base, glow_clipped)

    draw = ImageDraw.Draw(img)
    draw.rounded_rectangle(box, radius=radius, outline=GLASS_BORDER, width=stroke_w)

    glyph_sz = int(icon_size * 0.82)
    glyph = render_vector_glyph(glyph_sz)
    gx = (icon_size - glyph_sz) // 2
    gy = (icon_size - glyph_sz) // 2
    img.alpha_composite(glyph, (gx, gy))
    return img

def create_legacy_round(icon_size):
    """
    Genera el icono circular ic_launcher_round:
    Dark Obsidian circular con borde Mint frosted glass y glifo central.
    """
    stroke_w = max(1, int(icon_size * 0.025))
    inset = stroke_w + 1
    box = [inset, inset, icon_size - inset, icon_size - inset]

    base = Image.new("RGBA", (icon_size, icon_size), (0, 0, 0, 0))
    base_draw = ImageDraw.Draw(base)
    base_draw.ellipse(box, fill=OBSIDIAN_BG + (255,))

    glow = Image.new("RGBA", (icon_size, icon_size), (0, 0, 0, 0))
    g_draw = ImageDraw.Draw(glow)
    cx, cy = icon_size / 2, icon_size / 2
    r_glow = int(icon_size * 0.35)
    g_draw.ellipse([cx - r_glow, cy - r_glow, cx + r_glow, cy + r_glow], fill=EMERALD_ACCENT + (200,))
    r_mint = int(icon_size * 0.18)
    g_draw.ellipse([cx - r_mint, cy - r_mint, cx + r_mint, cy + r_mint], fill=MINT_COLOR + (50,))
    glow = glow.filter(ImageFilter.GaussianBlur(radius=icon_size * 0.14))

    mask = Image.new("L", (icon_size, icon_size), 0)
    m_draw = ImageDraw.Draw(mask)
    m_draw.ellipse(box, fill=255)
    empty = Image.new("RGBA", (icon_size, icon_size), (0, 0, 0, 0))
    glow_clipped = Image.composite(glow, empty, mask)

    img = Image.alpha_composite(base, glow_clipped)

    draw = ImageDraw.Draw(img)
    draw.ellipse(box, outline=GLASS_BORDER, width=stroke_w)

    glyph_sz = int(icon_size * 0.78)
    glyph = render_vector_glyph(glyph_sz)
    gx = (icon_size - glyph_sz) // 2
    gy = (icon_size - glyph_sz) // 2
    img.alpha_composite(glyph, (gx, gy))
    return img

def generate_icons(output_base_dir):
    os.makedirs(output_base_dir, exist_ok=True)

    for density, (icon_size, adaptive_size) in DENSITIES.items():
        folder = os.path.join(output_base_dir, density)
        os.makedirs(folder, exist_ok=True)

        # 1. Adaptive Background (108dp)
        bg = create_adaptive_background(adaptive_size)
        bg.save(os.path.join(folder, "revanced_adaptive_background_custom.png"))
        bg.save(os.path.join(folder, "adaptive_background.png"))
        bg.save(os.path.join(folder, "tiktont_adaptive_background.png"))

        # 2. Adaptive Foreground (108dp)
        fg = create_adaptive_foreground(adaptive_size)
        fg.save(os.path.join(folder, "revanced_adaptive_foreground_custom.png"))
        fg.save(os.path.join(folder, "adaptive_foreground.png"))
        fg.save(os.path.join(folder, "tiktont_adaptive_foreground.png"))

        # 3. Composite ic_launcher (Squircle)
        launcher = create_legacy_squircle(icon_size)
        launcher.save(os.path.join(folder, "ic_launcher.png"))

        # 4. Composite ic_launcher_round (Circle)
        launcher_round = create_legacy_round(icon_size)
        launcher_round.save(os.path.join(folder, "ic_launcher_round.png"))

        print(f"Generated {density} (launcher: {icon_size}x{icon_size}, adaptive: {adaptive_size}x{adaptive_size})")

    print(f"\nConjunto completo de assets Mint Glass / Dark Obsidian generado en: {output_base_dir}")

if __name__ == "__main__":
    out_dir = sys.argv[1] if len(sys.argv) > 1 else "/home/niko/Proyectos/n't/Tikton't/assets/branding"
    generate_icons(out_dir)
