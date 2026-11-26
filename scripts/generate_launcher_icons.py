#!/usr/bin/env python3
"""Generate Android launcher assets without cropping the master artwork."""
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "kenari_tasikmalaya.png"
RES = ROOT / "app/src/main/res"
if not SOURCE.is_file():
    raise SystemExit(f"Permanent launcher master artwork is missing: {SOURCE}")

master = Image.open(SOURCE).convert("RGBA")

def contain_on_square(image: Image.Image, size: int, margin_ratio: float = 0.08) -> Image.Image:
    """Scale artwork proportionally to fit a square canvas; never crop."""
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    max_side = max(1, round(size * (1.0 - 2.0 * margin_ratio)))
    image.thumbnail((max_side, max_side), Image.Resampling.LANCZOS)
    x = (size - image.width) // 2
    y = (size - image.height) // 2
    canvas.alpha_composite(image, (x, y))
    return canvas

densities = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
for density, size in densities.items():
    out = RES / f"mipmap-{density}/ic_launcher.png"
    out.parent.mkdir(parents=True, exist_ok=True)
    contain_on_square(master, size, margin_ratio=0.06).save(out)

store = RES / "drawable-nodpi/ic_launcher_store.png"
store.parent.mkdir(parents=True, exist_ok=True)
contain_on_square(master, 512, margin_ratio=0.06).save(store)

# Adaptive icon artwork stays entirely within the Android safe zone (central 2/3).
adaptive_bg = RES / "drawable-nodpi/kenari_launcher_adaptive_background.png"
adaptive_bg.parent.mkdir(parents=True, exist_ok=True)
contain_on_square(master, 432, margin_ratio=0.17).save(adaptive_bg)

adaptive_fg = RES / "drawable-nodpi/kenari_launcher_adaptive_foreground.png"
Image.new("RGBA", (432, 432), (0, 0, 0, 0)).save(adaptive_fg)

print(f"Generated no-crop launcher PNGs from master artwork: {SOURCE}")
print(f"Master dimensions: {master.width}x{master.height}; all outputs use proportional contain scaling.")
