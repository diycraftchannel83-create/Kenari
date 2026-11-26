#!/usr/bin/env python3
"""Generate permanent Android launcher PNGs from the repository's Kenari icon artwork."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app/src/main/res"
S = 1024
bg = Image.new("RGB", (S,S), "#063d22")
d = ImageDraw.Draw(bg)
# Deep green / gold emblem frame
d.rounded_rectangle((26,26,998,998), radius=205, fill="#063d22", outline="#d6a72c", width=24)
d.rounded_rectangle((54,54,970,970), radius=178, outline="#f7dc68", width=8)
# scenic background bands
d.polygon([(60,470),(250,330),(410,440),(650,270),(960,460),(960,970),(60,970)], fill="#0b522f")
d.polygon([(60,520),(220,380),(370,520),(540,400),(700,490),(960,390),(960,970),(60,970)], fill="#174d31")
# hills / foliage
for x in range(80,950,100):
    d.ellipse((x,540+(x%4)*18,x+150,740+(x%3)*12), fill="#1c6738")
# branch
d.line([(115,650),(800,565)], fill="#8a5b32", width=50)
d.line([(115,650),(800,565)], fill="#b68a52", width=9)
# stylized golden canary body and wing
d.ellipse((250,225,680,650), fill="#f3c526")
d.ellipse((295,210,690,590), fill="#ffd94b")
d.ellipse((320,360,610,585), fill="#e4a91d")
d.polygon([(330,390),(180,535),(410,510)], fill="#f9d54b")
d.ellipse((555,245,750,420), fill="#ffd94b")
d.polygon([(720,300),(840,345),(725,390)], fill="#f1b526")
d.ellipse((650,278,710,338), fill="#ffffff")
d.ellipse((668,294,700,326), fill="#14231a")
d.ellipse((680,304,690,314), fill="#ffffff")
# crown
d.polygon([(640,635),(680,560),(725,615),(770,550),(820,635)], fill="#e6bd42")
d.line([(640,635),(820,635)], fill="#f6dc6b", width=15)
# logo typography
try:
    f1=ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSerif-Bold.ttf",125)
    f2=ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSerif.ttf",70)
    f3=ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",30)
except OSError:
    f1=f2=f3=ImageFont.load_default()
d.text((512,700),"Kenari",font=f1,anchor="mm",fill="#ffdf58",stroke_width=2,stroke_fill="#4b3511")
d.text((512,810),"Tasikmalaya",font=f2,anchor="mm",fill="#fff4d3")
d.rounded_rectangle((160,875,864,940),radius=35,fill="#e5bd3f",outline="#fff1a0",width=4)
d.text((512,908),"CANARY BREEDING  •  GENEALOGY",font=f3,anchor="mm",fill="#063d22")
# density PNGs
sizes={"mdpi":48,"hdpi":72,"xhdpi":96,"xxhdpi":144,"xxxhdpi":192}
for density,size in sizes.items():
    out=RES/f"mipmap-{density}/ic_launcher.png"
    out.parent.mkdir(parents=True,exist_ok=True)
    bg.resize((size,size),Image.Resampling.LANCZOS).save(out)
store=RES/"drawable-nodpi/ic_launcher_store.png"
store.parent.mkdir(parents=True,exist_ok=True)
bg.resize((512,512),Image.Resampling.LANCZOS).save(store)
# Adaptive foreground artwork on transparent 432x432 canvas, Android safe-zone
fg=Image.new("RGBA",(432,432),(0,0,0,0))
art=bg.resize((292,292),Image.Resampling.LANCZOS).convert("RGBA")
fg.alpha_composite(art,((432-292)//2,(432-292)//2))
out=RES/"drawable-nodpi/kenari_launcher_foreground.png"
out.parent.mkdir(parents=True,exist_ok=True)
fg.save(out)
print("Generated mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi, adaptive foreground, and 512px store icon.")
