#!/usr/bin/env python3
"""Generate launcher icons from the exact artwork committed at repository root."""
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "kenari_tasikmalaya.png"
RES = ROOT / "app/src/main/res"
if not SOURCE.is_file():
    raise SystemExit(f"Permanent launcher master artwork is missing: {SOURCE}")

master = Image.open(SOURCE).convert("RGBA")
# Preserve exact uploaded artwork. If a replacement isn't square, center-crop
# only to square, without redesigning or adding anything.
w, h = master.size
if w != h:
    side = min(w, h)
    left, top = (w-side)//2, (h-side)//2
    master = master.crop((left, top, left+side, top+side))

densities = {"mdpi":48, "hdpi":72, "xhdpi":96, "xxhdpi":144, "xxxhdpi":192}
for density, size in densities.items():
    target = RES / f"mipmap-{density}" / "ic_launcher.png"
    target.parent.mkdir(parents=True, exist_ok=True)
    master.resize((size, size), Image.Resampling.LANCZOS).save(target)

store = RES / "drawable-nodpi" / "ic_launcher_store.png"
store.parent.mkdir(parents=True, exist_ok=True)
master.resize((512,512), Image.Resampling.LANCZOS).save(store)

# Adaptive foreground keeps the full artwork in the center safe zone.
canvas = Image.new("RGBA", (432,432), (0,0,0,0))
safe = master.resize((300,300), Image.Resampling.LANCZOS)
canvas.alpha_composite(safe, ((432-300)//2, (432-300)//2))
fg = RES / "drawable-nodpi" / "kenari_launcher_foreground.png"
fg.parent.mkdir(parents=True, exist_ok=True)
canvas.save(fg)
print(f"Launcher PNGs generated from exact master: {SOURCE} ({w}x{h})")
