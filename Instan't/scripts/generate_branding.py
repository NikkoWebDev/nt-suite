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

def draw_instant_camera(draw, cx, cy, size, border_color, lens_color, flash_color, stroke_w):
    """
    Dibuja el glifo geométrico de Instagram/Instan't:
    Rectángulo exterior redondeado, lente circular concéntrica y punto flash superior derecho.
    """
    w = size * 0.90
    h = size * 0.90
    r = size * 0.24
    box = [cx - w / 2, cy - h / 2, cx + w / 2, cy + h / 2]
    
    # Outer squircle
    draw.rounded_rectangle(box, radius=r, fill=GLASS_SURFACE, outline=border_color, width=stroke_w)
    
    # Inner lens ring
    lens_r = size * 0.25
    lens_box = [cx - lens_r, cy - lens_r, cx + lens_r, cy + lens_r]
    draw.ellipse(lens_box, outline=lens_color, width=stroke_w)
    
    # Inner lens core dot (mint glow)
    core_r = size * 0.08
    core_box = [cx - core_r, cy - core_r, cx + core_r, cy + core_r]
    draw.ellipse(core_box, fill=lens_color)
    
    # Top-right flash dot (cyan)
    flash_r = size * 0.07
    fx = cx + w * 0.28
    fy = cy - h * 0.28
    flash_box = [fx - flash_r, fy - flash_r, fx + flash_r, fy + flash_r]
    draw.ellipse(flash_box, fill=flash_color)

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
        bg.save(os.path.join(folder, "instant_adaptive_background.png"))

        # 2. Adaptive Foreground (108dp)
        fg = Image.new("RGBA", (adaptive_size, adaptive_size), (0, 0, 0, 0))
        fg_draw = ImageDraw.Draw(fg)
        stroke_w = max(2, int(adaptive_size * 0.022))
        draw_instant_camera(
            fg_draw, cx, cy, adaptive_size * 0.46,
            border_color=MINT_COLOR + (255,),
            lens_color=MINT_COLOR + (255,),
            flash_color=CYAN_COLOR + (255,),
            stroke_w=stroke_w
        )
        fg.save(os.path.join(folder, "adaptive_foreground.png"))
        fg.save(os.path.join(folder, "instant_adaptive_foreground.png"))

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
        draw_instant_camera(
            l_draw, icon_size / 2, icon_size / 2, icon_size * 0.58,
            border_color=MINT_COLOR + (255,),
            lens_color=MINT_COLOR + (255,),
            flash_color=CYAN_COLOR + (255,),
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
        draw_instant_camera(
            r_draw, icon_size / 2, icon_size / 2, icon_size * 0.56,
            border_color=MINT_COLOR + (255,),
            lens_color=MINT_COLOR + (255,),
            flash_color=CYAN_COLOR + (255,),
            stroke_w=max(2, int(icon_size * 0.035))
        )
        launcher_round.save(os.path.join(folder, "ic_launcher_round.png"))

    print(f"Generated complete Android Mint Glass icon set for Instan't in {output_base_dir}")

if __name__ == "__main__":
    import sys
    out_dir = sys.argv[1] if len(sys.argv) > 1 else "/home/niko/Proyectos/Instan't/assets/branding"
    generate_icons(out_dir)
