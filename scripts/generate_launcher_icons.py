#!/usr/bin/env python3
"""Build all launcher PNG variants from the exact flyer artwork, without cropping."""
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parents[1]
SOURCE=ROOT/"Flyerwiz_1795712853891.png"
RES=ROOT/"app/src/main/res"
if not SOURCE.is_file(): raise SystemExit(f"Launcher master not found: {SOURCE}")
master=Image.open(SOURCE).convert("RGBA")
print(f"Master launcher artwork: {SOURCE} ({master.width}x{master.height})")
def fit_no_crop(img,size,margin=0.0):
    canvas=Image.new("RGBA",(size,size),(0,0,0,0))
    available=max(1,int(size*(1-2*margin)))
    scaled=img.copy()
    scaled.thumbnail((available,available),Image.Resampling.LANCZOS)
    canvas.alpha_composite(scaled,((size-scaled.width)//2,(size-scaled.height)//2))
    return canvas
densities={"mdpi":48,"hdpi":72,"xhdpi":96,"xxhdpi":144,"xxxhdpi":192}
for density,size in densities.items():
    p=RES/f"mipmap-{density}/ic_launcher.png"
    p.parent.mkdir(parents=True,exist_ok=True)
    fit_no_crop(master,size,0).save(p)
store=RES/"drawable-nodpi/ic_launcher_store.png"
store.parent.mkdir(parents=True,exist_ok=True)
fit_no_crop(master,512,0).save(store)
# Adaptive version scales the entire artwork down into mask-safe space.
adaptive=RES/"drawable-nodpi/kenari_launcher_adaptive_background.png"
adaptive.parent.mkdir(parents=True,exist_ok=True)
fit_no_crop(master,432,0.12).save(adaptive)
fg=RES/"drawable-nodpi/kenari_launcher_adaptive_foreground.png"
Image.new("RGBA",(432,432),(0,0,0,0)).save(fg)
print("Generated all icon densities from flyer artwork; no crop.")
