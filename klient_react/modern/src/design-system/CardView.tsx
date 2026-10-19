import React, { useState } from "react";
import { Check } from "lucide-react";

export type CardSize = "xs" | "sm" | "md" | "lg";

export interface CardViewProps {
  image: string;
  name?: string;
  size?: CardSize;
  isPlayable?: boolean;
  isSelected?: boolean;
  isRotated?: boolean;
  disabled?: boolean;
  badge?: React.ReactNode;
  onClick?: (e: React.MouseEvent<HTMLDivElement>) => void;
  className?: string;
  style?: React.CSSProperties;
}

export const CardView: React.FC<CardViewProps> = ({
  image,
  name,
  size = "md",
  isPlayable = false,
  isSelected = false,
  isRotated = false,
  disabled = false,
  badge,
  onClick,
  className = "",
  style,
}) => {
  const [imgError, setImgError] = useState(false);

  // Inteligentní normalizace cesty k obrázku
  let imageUrl = image;
  if (!imageUrl.startsWith("/") && !imageUrl.startsWith("http")) {
    if (imageUrl.startsWith("postavy/")) {
      imageUrl = `/img/karty/${imageUrl}`;
    } else {
      imageUrl = `/img/karty/${imageUrl}`;
    }
  }
  if (!imageUrl.endsWith(".png") && !imageUrl.endsWith(".jpg") && !imageUrl.endsWith(".webp")) {
    imageUrl = `${imageUrl}.png`;
  }

  // Přesné rozměry karet odpovídající Bang! kartám (poměr 660 : 1035)
  // s přirozeným jemným zaoblením rohů skutečných papírových karet
  const sizeClass = {
    xs: "w-10 sm:w-11 md:w-12 aspect-[660/1035] rounded-[4px] text-[9px]",
    sm: "w-14 sm:w-16 md:w-20 aspect-[660/1035] rounded-[5px] text-[10px]",
    md: "w-18 sm:w-22 md:w-28 aspect-[660/1035] rounded-[6px] text-xs",
    lg: "w-36 sm:w-44 md:w-52 aspect-[660/1035] rounded-[8px] text-sm",
  }[size];

  // Zřetelné interaktivní stavy (včetně silného a nepřehlédnutelného zvýraznění výběru)
  let stateClass = "border-[#382f28] shadow-sm";
  if (isSelected) {
    stateClass =
      "border-amber-300 ring-4 ring-amber-400 shadow-[0_0_25px_rgba(251,191,36,0.65)] -translate-y-2.5 scale-105 z-30 cursor-pointer";
  } else if (isPlayable && !disabled) {
    stateClass =
      "border-amber-500/80 ring-1 ring-amber-500/40 hover:border-amber-300 hover:ring-2 hover:ring-amber-400 hover:-translate-y-1.5 cursor-pointer shadow-md";
  } else if (disabled) {
    stateClass = "border-[#2b231d] opacity-50 grayscale-[0.3]";
  }

    const transitionClass = className.includes("transition-none")
      ? "transition-none"
      : "transition-all duration-150";

    return (
      <div
        onClick={disabled ? undefined : onClick}
        style={style}
        className={`relative shrink-0 select-none overflow-hidden ${transitionClass} bg-[#1e1814] border ${sizeClass} ${stateClass} ${
          isRotated ? "rotate-2" : ""
        } ${className}`}
        title={name || undefined}
      >
      {!imgError ? (
        <img
          src={imageUrl}
          alt={name || "Karta"}
          onError={() => setImgError(true)}
          className="w-full h-full object-contain rounded-[inherit] pointer-events-none"
          loading="lazy"
        />
      ) : (
        // Fallback pro chybějící obrázek
        <div className="w-full h-full p-2 flex flex-col justify-between items-center text-center bg-[#29221c] border border-dashed border-[#4a3e35]">
          <span className="text-xs text-amber-400 font-serif">🃏</span>
          <span className="font-semibold text-[#f5ede3] leading-tight line-clamp-3 text-[11px]">
            {name || "Karta"}
          </span>
          <span className="text-[8px] text-[#7a6c60] uppercase tracking-wider">Bang!</span>
        </div>
      )}

      {/* Silný indikátor výběru s fajfkou */}
      {isSelected && (
        <div className="absolute top-1.5 right-1.5 w-6 h-6 rounded-full bg-amber-400 text-black flex items-center justify-center font-bold text-xs shadow-lg border border-black/30 pointer-events-none z-20">
          <Check className="w-4 h-4 stroke-[3]" />
        </div>
      )}

      {/* Volitelný odznak v rohu (např. klávesová zkratka) */}
      {!isSelected && badge && (
        <div className="absolute top-1 right-1 pointer-events-none z-10">{badge}</div>
      )}
    </div>
  );
};

export default CardView;
