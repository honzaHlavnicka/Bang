#!/usr/bin/env python3
"""
Vytvoří responsivní verze pozadí pro různé velikosti zařízení
Použití: python3 create_responsive_images.py

Poznámka: soubor vytvořen s pomocí AI
"""

import os
from PIL import Image
import sys

# Cesta ke složce s obrázky
IMG_DIR = "./public/img"

# Definice breakpointů a jejich rozměrů
BREAKPOINTS = {
    "mobile": {
        "max_width": 480,
        "suffix": "-mobile"
    },
    "tablet": {
        "max_width": 768,
        "suffix": "-tablet"
    },
    "desktop": {
        "max_width": 1024,
        "suffix": "-desktop"
    },
    "large": {
        "max_width": 1920,
        "suffix": "-large"
    }
}

# Obrázky k optimalizaci
BACKGROUNDS = [
    "pozadi2.jpg",
    "pozadi-tmave.jpg"
]

def get_responsive_height(width: int, original_width: int, original_height: int) -> int:
    """Vypočítá proporcionální výšku"""
    return int((width / original_width) * original_height)

def optimize_image(input_path: str, output_path: str, max_width: int, quality: int = 85):
    """Optimalizuje obrázek pro danou šířku"""
    try:
        img = Image.open(input_path)
        original_width, original_height = img.size
        
        # Pokud je obrázek menší, nezvětšujeme ho
        if original_width <= max_width:
            print(f"  ℹ️  {output_path}: Přeskakuji (originál je menší)")
            return False
        
        # Vypočítej výšku se zachováním poměru stran
        new_height = get_responsive_height(max_width, original_width, original_height)
        
        # Změň velikost
        img_resized = img.resize((max_width, new_height), Image.Resampling.LANCZOS)
        
        # Ulož s optimalizací
        if output_path.endswith('.webp'):
            img_resized.save(output_path, 'WEBP', quality=quality, method=6)
        else:
            img_resized.save(output_path, 'JPEG', quality=quality, optimize=True)
        
        original_size = os.path.getsize(input_path) / 1024
        new_size = os.path.getsize(output_path) / 1024
        compression = ((original_size - new_size) / original_size * 100) if original_size > 0 else 0
        
        print(f"  ✅ {output_path}")
        print(f"     Rozměry: {original_width}x{original_height} → {max_width}x{new_height}")
        print(f"     Velikost: {original_size:.1f}KB → {new_size:.1f}KB (komprese: {compression:.0f}%)")
        return True
        
    except Exception as e:
        print(f"  ❌ Chyba při zpracování {input_path}: {e}")
        return False

def main():
    if not os.path.exists(IMG_DIR):
        print(f"❌ Složka {IMG_DIR} neexistuje!")
        sys.exit(1)
    
    print("🎨 Vytváření responsivních verzí pozadí...\n")
    
    processed = 0
    
    for bg in BACKGROUNDS:
        base_path = os.path.join(IMG_DIR, bg)
        
        # Zpracuj JPG verzi
        if bg.endswith('.jpg'):
            print(f"📝 {bg}")
            
            for breakpoint, config in BREAKPOINTS.items():
                width = config['max_width']
                suffix = config['suffix']
                
                # Bez přípony
                name_without_ext = bg.rsplit('.', 1)[0]
                output_jpg = os.path.join(IMG_DIR, f"{name_without_ext}{suffix}.jpg")
                output_webp = os.path.join(IMG_DIR, f"{name_without_ext}{suffix}.webp")
                
                # Vytvoř JPG
                if optimize_image(base_path, output_jpg, width):
                    processed += 1
                
                # Vytvoř WebP (lepší komprese)
                if optimize_image(base_path, output_webp, width):
                    processed += 1
            
            print()
    
    print(f"✨ Hotovo! Vytvořeno {processed} optimalizovaných obrázků.\n")
    
    # Vytiskni seznam souborů
    print("📂 Vytvořené soubory:")
    for bg in BACKGROUNDS:
        name = bg.rsplit('.', 1)[0]
        for bp, config in BREAKPOINTS.items():
            suffix = config['suffix']
            jpg_file = f"{name}{suffix}.jpg"
            webp_file = f"{name}{suffix}.webp"
            jpg_path = os.path.join(IMG_DIR, jpg_file)
            webp_path = os.path.join(IMG_DIR, webp_file)
            
            jpg_exists = "✓" if os.path.exists(jpg_path) else "✗"
            webp_exists = "✓" if os.path.exists(webp_path) else "✗"
            
            print(f"  {jpg_exists} {jpg_file}")
            print(f"  {webp_exists} {webp_file}")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n⚠️  Přerušeno uživatelem.")
        sys.exit(1)
