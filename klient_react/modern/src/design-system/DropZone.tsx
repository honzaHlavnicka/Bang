import React from "react";
import { themeClasses } from "./tokens";

export interface DropZoneProps {
  id: string;
  label: string;
  hint?: string;
  icon?: React.ReactNode;
  isActive?: boolean; // Aktivní při přetažení karty nad tuto zónu
  isHighlighted?: boolean; // Zvýrazněno, pokud je karta vybraná
  variant?: "table" | "fire" | "player";
  onClick?: () => void;
  children?: React.ReactNode;
  className?: string;
}

export const DropZone: React.FC<DropZoneProps> = ({
  id,
  label,
  hint,
  icon,
  isActive = false,
  isHighlighted = false,
  variant = "table",
  onClick,
  children,
  className = "",
}) => {
  const variantStyles = {
    table: isActive
      ? themeClasses.dropZoneActive
      : isHighlighted
      ? "border-amber-500/50 bg-amber-500/5"
      : themeClasses.dropZoneIdle,
    fire: isActive
      ? "border-red-500 bg-red-500/20 scale-[1.03] shadow-[0_0_20px_rgba(239,68,68,0.3)] animate-pulse"
      : isHighlighted
      ? "border-red-500/50 bg-red-500/10"
      : "border-dashed border-red-900/60 bg-[#241311]/60 hover:border-red-700/70",
    player: isActive
      ? "border-amber-400 bg-amber-500/15 ring-2 ring-amber-400"
      : isHighlighted
      ? "border-amber-500/40"
      : "border-[#382f28]",
  }[variant];

  return (
    <div
      id={id}
      data-dropzone={id}
      onClick={onClick}
      className={`relative flex flex-col items-center justify-center p-3 rounded-2xl transition-all duration-150 text-center select-none ${variantStyles} ${className}`}
    >
      {icon && <span className="mb-1 text-lg">{icon}</span>}
      <span className="text-xs font-semibold tracking-wide text-[#f5ede3]">{label}</span>
      {hint && <span className="text-[10px] text-[#b5a596] mt-0.5">{hint}</span>}
      {children}
    </div>
  );
};

export default DropZone;
