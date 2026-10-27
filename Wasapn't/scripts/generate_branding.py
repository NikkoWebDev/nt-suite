#!/usr/bin/env python3
import os
import math
from PIL import Image, ImageDraw

DENSITIES = {
    "mipmap-mdpi": (48, 108),
    "mipmap-hdpi": (72, 162),
    "mipmap-xhdpi": (96, 216),
    "mipmap-xxhdpi": (144, 324),
    "mipmap-xxxhdpi": (192, 432),
}

# Paleta nikko.dev
MINT_COLOR = (0, 245, 160)       # #00F5A0 Neon Mint
CYAN_COLOR = (88, 216, 255)      # #58D8FF Neon Cyan
MINT_CYAN = (45, 212, 191)       # #2DD4BF Emerald/Teal
OBSIDIAN_BG = (8, 10, 15)        # #080A0F Dark Obsidian
GLASS_BORDER = (0, 245, 160, 140)# Mint frosted border
GLASS_SURFACE = (17, 24, 39, 230)# #111827 Dark Glass

def draw_wasapnt_glyph(draw, cx, cy, size, border_color, accent_color, phone_color, stroke_w):
    """
    Dibuja el glifo de Wasapn't:
    Globo de conversación estilizado con cola inferior izquierda + auricular de teléfono minimalista neón.
    """
    # Radio del globo de conversación
    r = size * 0.42
    
    # 1. Globo de conversación principal (círculo casi completo con cola)
    # Cuerpo circular
    bubble_box = [cx - r, cy - r * 0.95, cx + r, cy + r * 1.05]
    draw.ellipse(bubble_box, fill=GLASS_SURFACE, outline=border_color, width=stroke_w)
    
    # Cola inferior izquierda del globo
    tail_pts = [
        (cx - r * 0.65, cy + r * 0.68),
        (cx - r * 0.98, cy + r * 1.12),
        (cx - r * 0.28, cy + r * 0.88)
    ]
    draw.polygon(tail_pts, fill=GLASS_SURFACE)
    draw.line([tail_pts[0], tail_pts[1], tail_pts[2]], fill=border_color, width=stroke_w)

    # 2. Auricular telefónico estilizado en ángulo (~45 grados)
    # Dibujado con formas geométricas conectadas o curvas
    phone_len = size * 0.44
    phone_w = size * 0.14
    
    # Puntos angulares del auricular
    # Auricular superior, receptor y mango
    p1 = (cx - size * 0.16, cy + size * 0.14)  # Base / micrófono
    p2 = (cx + size * 0.14, cy - size * 0.16)  # Tope / auricular
    mid = (cx + size * 0.02, cy + size * 0.02) # Centro arqueado
    
    # Dibujar línea curva gruesa representando el mango
    draw.line([p1, mid, p2], fill=accent_color, width=int(stroke_w * 1.6), joint="curve")
    
    # Microfono (cápsula inferior)
    mic_r = size * 0.075
    draw.ellipse([p1[0] - mic_r, p1[1] - mic_r, p1[0] + mic_r, p1[1] + mic_r], fill=phone_color)
    
    # Parlante (cápsula superior)
    ear_r = size * 0.075
    draw.ellipse([p2[0] - ear_r, p2[1] - ear_r, p2[0] + ear_r, p2[1] + ear_r], fill=phone_color)

def generate_icons(output_base_dir):
    os.makedirs(output_base_dir, exist_ok=True)

    for density, (icon_size, adaptive_size) in DENSITIES.items():
        folder = os.path.join(output_base_dir, density)
        os.makedirs(folder, exist_ok=True)

        # 1. Adaptive Background (108dp)
        bg = Image.new("RGBA", (adaptive_size, adaptive_size), OBSIDIAN_BG + (255,))
        bg_draw = ImageDraw.Draw(bg)
        cx, cy = adaptive_size / 2, adaptive_size / 2
        for r in range(int(adaptive_size * 0.48), 0, -2):
            alpha = int(32 * (1 - r / (adaptive_size * 0.48)))
            bg_draw.ellipse(
                [cx - r, cy - r, cx + r, cy + r],
                fill=(0, 245, 160, alpha)
            )
        bg.save(os.path.join(folder, "adaptive_background.png"))
        bg.save(os.path.join(folder, "wasapnt_adaptive_background.png"))

        # 2. Adaptive Foreground (108dp)
        fg = Image.new("RGBA", (adaptive_size, adaptive_size), (0, 0, 0, 0))
        fg_draw = ImageDraw.Draw(fg)
        stroke_w = max(2, int(adaptive_size * 0.024))
        draw_wasapnt_glyph(
            fg_draw, cx, cy, adaptive_size * 0.50,
            border_color=MINT_COLOR + (255,),
            accent_color=MINT_COLOR + (255,),
            phone_color=CYAN_COLOR + (255,),
            stroke_w=stroke_w
        )
        fg.save(os.path.join(folder, "adaptive_foreground.png"))
        fg.save(os.path.join(folder, "wasapnt_adaptive_foreground.png"))

        # 3. Composite ic_launcher (Squircle)
        launcher = Image.new("RGBA", (icon_size, icon_size), (0, 0, 0, 0))
        l_draw = ImageDraw.Draw(launcher)
        l_box = [2, 2, icon_size - 3, icon_size - 3]
        radius = int(icon_size * 0.24)
        l_draw.rounded_rectangle(l_box, radius=radius, fill=OBSIDIAN_BG + (255,), outline=GLASS_BORDER, width=max(1, int(icon_size * 0.02)))
        
        # Subtle internal glow
        for r in range(int(icon_size * 0.38), 0, -2):
            alpha = int(24 * (1 - r / (icon_size * 0.38)))
            l_draw.ellipse(
                [icon_size / 2 - r, icon_size / 2 - r, icon_size / 2 + r, icon_size / 2 + r],
                fill=(0, 245, 160, alpha)
            )
        draw_wasapnt_glyph(
            l_draw, icon_size / 2, icon_size / 2, icon_size * 0.62,
            border_color=MINT_COLOR + (255,),
            accent_color=MINT_COLOR + (255,),
            phone_color=CYAN_COLOR + (255,),
            stroke_w=max(2, int(icon_size * 0.035))
        )
        launcher.save(os.path.join(folder, "ic_launcher.png"))

        # 4. Composite ic_launcher_round (Circle)
        launcher_round = Image.new("RGBA", (icon_size, icon_size), (0, 0, 0, 0))
        r_draw = ImageDraw.Draw(launcher_round)
        r_box = [2, 2, icon_size - 3, icon_size - 3]
        r_draw.ellipse(r_box, fill=OBSIDIAN_BG + (255,), outline=GLASS_BORDER, width=max(1, int(icon_size * 0.02)))
        for r in range(int(icon_size * 0.38), 0, -2):
            alpha = int(24 * (1 - r / (icon_size * 0.38)))
            r_draw.ellipse(
                [icon_size / 2 - r, icon_size / 2 - r, icon_size / 2 + r, icon_size / 2 + r],
                fill=(0, 245, 160, alpha)
            )
        draw_wasapnt_glyph(
            r_draw, icon_size / 2, icon_size / 2, icon_size * 0.60,
            border_color=MINT_COLOR + (255,),
            accent_color=MINT_COLOR + (255,),
            phone_color=CYAN_COLOR + (255,),
            stroke_w=max(2, int(icon_size * 0.035))
        )
        launcher_round.save(os.path.join(folder, "ic_launcher_round.png"))

    print(f"Generated complete Android Mint Glass icon set for Wasapn't in {output_base_dir}")

if __name__ == "__main__":
    import sys
    out_dir = sys.argv[1] if len(sys.argv) > 1 else "/home/niko/Proyectos/wasapn't/assets/branding"
    generate_icons(out_dir)
