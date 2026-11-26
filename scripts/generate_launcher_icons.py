#!/usr/bin/env python3
"""Generate Android launcher PNG sizes from the exact root master artwork."""
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "kenari_tasikmalaya.png"
RES = ROOT / "app/src/main/res"
if not SOURCE.exists():
    raise SystemExit(f"Launcher artwork missing: {SOURCE}")
master = Image.open(SOURCE).convert("RGBA")
# Use master unchanged in composition; resize/crop only if necessary for square outputs.
densities={"mdpi":48,"hdpi":72,"xhdpi":96,"xxhdpi":144,"xxxhdpi":192}
for density,size in densities.items():
    out=RES/f"mipmap-{density}/ic_launcher.png"
    out.parent.mkdir(parents=True,exist_ok=True)
    master.resize((size,size),Image.Resampling.LANCZOS).save(out)
store=RES/"drawable-nodpi/ic_launcher_store.png"
store.parent.mkdir(parents=True,exist_ok=True)
master.resize((512,512),Image.Resampling.LANCZOS).save(store)
# Adaptive icon uses the exact master artwork as the background layer, not the
# old vector bird. Transparent foreground prevents any overlay/recomposition.
adaptive=master.resize((432,432),Image.Resampling.LANCZOS)
adaptive_bg=RES/"drawable-nodpi/kenari_launcher_adaptive_background.png"
adaptive_bg.parent.mkdir(parents=True,exist_ok=True)
adaptive.save(adaptive_bg)
transparent=Image.new("RGBA",(432,432),(0,0,0,0))
adaptive_fg=RES/"drawable-nodpi/kenari_launcher_adaptive_foreground.png"
transparent.save(adaptive_fg)
print("Generated all launcher PNGs from the exact committed master artwork:",SOURCE)
