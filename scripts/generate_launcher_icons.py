#!/usr/bin/env python3
"""Generate Kenari Android launcher icons from kenari_tasikmalaya.png, never crop."""
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "kenari_tasikmalaya.png"
RES = ROOT / "app/src/main/res"

if not SOURCE.is_file():
    raise SystemExit(f"Launcher master image not found: {SOURCE}")

master = Image.open(SOURCE).convert("RGBA")
print(f"Launcher master: {SOURCE} ({master.width}x{master.height})")

def contain(image: Image.Image, size: int, margin_ratio: float = 0.0) -> Image.Image:
    """Keep full source composition and aspect ratio on a centered transparent square."""
    if not (0.0 <= margin_ratio < 0.5):
        raise ValueError("margin_ratio must be in [0, 0.5)")
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    max_edge = max(1, int(round(size * (1 - 2 * margin_ratio))))
    scaled = image.copy()
    scaled.thumbnail((max_edge, max_edge), Image.Resampling.LANCZOS)
    x = (size - scaled.width) // 2
    y = (size - scaled.height) // 2
    canvas.alpha_composite(scaled, (x, y))
    return canvas

# Older launcher PNG resources can also be masked by some launchers. Keep the
# complete composition inside a modest transparent safe border, never edge-crop.
densities = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
for density, size in densities.items():
    target = RES / f"mipmap-{density}/ic_launcher.png"
    target.parent.mkdir(parents=True, exist_ok=True)
    contain(master, size, margin_ratio=0.08).save(target)

# Store/resolution-independent icon is a 512 square, with the entire master fitted.
store = RES / "drawable-nodpi/ic_launcher_store.png"
store.parent.mkdir(parents=True, exist_ok=True)
contain(master, 512, margin_ratio=0.02).save(store)

# Adaptive icons are clipped to a launcher mask. Fit the full artwork into the
# Android safe area (about the central 66%), leaving transparent padding.
adaptive_bg = RES / "drawable-nodpi/kenari_launcher_adaptive_background.png"
adaptive_bg.parent.mkdir(parents=True, exist_ok=True)
contain(master, 432, margin_ratio=0.17).save(adaptive_bg)

adaptive_fg = RES / "drawable-nodpi/kenari_launcher_adaptive_foreground.png"
adaptive_fg.parent.mkdir(parents=True, exist_ok=True)
Image.new("RGBA", (432, 432), (0, 0, 0, 0)).save(adaptive_fg)

print("Generated all launcher assets from kenari_tasikmalaya.png using contain fit; no cropping.")
