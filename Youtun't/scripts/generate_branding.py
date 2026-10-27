#!/usr/bin/env python3
import os
import math
from PIL import Image, ImageDraw, ImageFilter

DENSITIES = {
    "mipmap-mdpi": (48, 108),
    "mipmap-hdpi": (72, 162),
    "mipmap-xhdpi": (96, 216),
    "mipmap-xxhdpi": (144, 324),
    "mipmap-xxxhdpi": (192, 432),
}

MINT_COLOR = (0, 245, 160)       # #00F5A0 Neon Mint
MINT_CYAN = (45, 212, 191)       # #2DD4BF
OBSIDIAN_BG = (8, 10, 15)        # #080A0F Dark Obsidian
GLASS_BORDER = (0, 245, 160, 90) # Subtle mint frosted border
GLASS_SURFACE = (18, 24, 32, 220)

def draw_play_triangle(draw, center_x, center_y, size, color):
    # Equilateral-like triangle pointing right
    h = size * 0.95
    w = size * 0.85
    p1 = (center_x - w * 0.4, center_y - h * 0.5)
    p2 = (center_x + w * 0.6, center_y)
    p3 = (center_x - w * 0.4, center_y + h * 0.5)
    draw.polygon([p1, p2, p3], fill=color)

def generate_icons(output_base_dir):
    os.makedirs(output_base_dir, exist_ok=True)

    for density, (icon_size, adaptive_size) in DENSITIES.items():
        folder = os.path.join(output_base_dir, density)
        os.makedirs(folder, exist_ok=True)

        # 1. Adaptive Background (108dp)
        bg = Image.new("RGBA", (adaptive_size, adaptive_size), OBSIDIAN_BG + (255,))
        bg_draw = ImageDraw.Draw(bg)
        # Subtle radial glow from center
        cx, cy = adaptive_size / 2, adaptive_size / 2
        for r in range(int(adaptive_size * 0.45), 0, -2):
            alpha = int(25 * (1 - r / (adaptive_size * 0.45)))
            bg_draw.ellipse(
                [cx - r, cy - r, cx + r, cy + r],
                fill=(0, 245, 160, alpha)
            )
        bg.save(os.path.join(folder, "adaptiveproduct_youtube_background_color_108.png"))
        bg.save(os.path.join(folder, "revanced_adaptive_background_custom.png"))

        # 2. Adaptive Foreground (108dp)
        fg = Image.new("RGBA", (adaptive_size, adaptive_size), (0, 0, 0, 0))
        fg_draw = ImageDraw.Draw(fg)
        
        # Center glass rounded rectangle badge
        badge_w = adaptive_size * 0.46
        badge_h = adaptive_size * 0.34
        rx, ry = adaptive_size * 0.1, adaptive_size * 0.1
        box = [cx - badge_w / 2, cy - badge_h / 2, cx + badge_w / 2, cy + badge_h / 2]
        
        # Glass fill
        fg_draw.rounded_rectangle(box, radius=rx, fill=GLASS_SURFACE, outline=GLASS_BORDER, width=max(2, int(adaptive_size * 0.015)))
        # Play button in center
        draw_play_triangle(fg_draw, cx + adaptive_size * 0.015, cy, badge_h * 0.5, MINT_COLOR + (255,))
        fg.save(os.path.join(folder, "adaptiveproduct_youtube_foreground_color_108.png"))
        fg.save(os.path.join(folder, "revanced_adaptive_foreground_custom.png"))

        # 3. Composite ic_launcher (Squircle / rounded rect)
        launcher = Image.new("RGBA", (icon_size, icon_size), (0, 0, 0, 0))
        l_draw = ImageDraw.Draw(launcher)
        l_box = [2, 2, icon_size - 3, icon_size - 3]
        radius = int(icon_size * 0.22)
        l_draw.rounded_rectangle(l_box, radius=radius, fill=OBSIDIAN_BG + (255,), outline=GLASS_BORDER, width=max(1, int(icon_size * 0.02)))
        
        # Center glass badge
        b_w = icon_size * 0.55
        b_h = icon_size * 0.40
        b_box = [icon_size / 2 - b_w / 2, icon_size / 2 - b_h / 2, icon_size / 2 + b_w / 2, icon_size / 2 + b_h / 2]
        l_draw.rounded_rectangle(b_box, radius=int(icon_size * 0.08), fill=GLASS_SURFACE, outline=(0, 245, 160, 140), width=max(1, int(icon_size * 0.02)))
        draw_play_triangle(l_draw, icon_size / 2 + icon_size * 0.02, icon_size / 2, b_h * 0.55, MINT_COLOR + (255,))
        launcher.save(os.path.join(folder, "ic_launcher.png"))

        # 4. Composite ic_launcher_round (Circle)
        launcher_round = Image.new("RGBA", (icon_size, icon_size), (0, 0, 0, 0))
        r_draw = ImageDraw.Draw(launcher_round)
        r_box = [2, 2, icon_size - 3, icon_size - 3]
        r_draw.ellipse(r_box, fill=OBSIDIAN_BG + (255,), outline=GLASS_BORDER, width=max(1, int(icon_size * 0.02)))
        r_draw.rounded_rectangle(b_box, radius=int(icon_size * 0.08), fill=GLASS_SURFACE, outline=(0, 245, 160, 140), width=max(1, int(icon_size * 0.02)))
        draw_play_triangle(r_draw, icon_size / 2 + icon_size * 0.02, icon_size / 2, b_h * 0.55, MINT_COLOR + (255,))
        launcher_round.save(os.path.join(folder, "ic_launcher_round.png"))

    print(f"Generated complete Android Mint Glass icon set in {output_base_dir}")

if __name__ == "__main__":
    import sys
    out_dir = sys.argv[1] if len(sys.argv) > 1 else "assets/branding"
    generate_icons(out_dir)
