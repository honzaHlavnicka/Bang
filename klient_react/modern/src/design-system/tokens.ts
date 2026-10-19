/**
 * Design Tokens & Constants for Bang! Modern Client
 * Definice barev, sémantických tříd a stylů podle DESIGN_GUIDE.md
 */

export const colors = {
  canvas: "#161311",
  surface: "#221c18",
  surfaceRaised: "#2d2520",
  surfaceHover: "#3a302a",
  borderSubtle: "#382f28",
  borderActive: "#54463c",
  textMain: "#f5ede3",
  textMuted: "#b5a596",
  textSubtle: "#7a6c60",
  accentAmber: "#d97706",
  accentAmberHover: "#f59e0b",
  accentSage: "#3f6b4e",
  accentSageHover: "#508462",
  accentTerracotta: "#b33f2b",
  accentTerracottaHover: "#cb4b35",
} as const;

export const themeClasses = {
  // Základní plochy
  canvas: "bg-[#161311] text-[#f5ede3] min-h-screen selection:bg-amber-600/30 selection:text-amber-200",
  surface: "bg-[#221c18] border border-[#382f28] rounded-xl shadow-md",
  surfaceRaised: "bg-[#2d2520] border border-[#423830] rounded-2xl shadow-lg",
  surfaceSubtle: "bg-[#1c1714] border border-[#322923] rounded-lg",

  // Texty
  heading: "font-semibold text-[#f5ede3] tracking-tight",
  body: "text-sm text-[#f5ede3]",
  muted: "text-xs text-[#b5a596]",
  subtle: "text-xs text-[#7a6c60]",

  // Tlačítka (základní stavební kameny)
  buttonPrimary:
    "inline-flex items-center justify-center gap-2 px-4 py-2 text-sm font-semibold text-white bg-amber-600 hover:bg-amber-500 active:scale-[0.98] rounded-xl shadow-sm border border-amber-500/50 transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-amber-500 disabled:opacity-50 disabled:pointer-events-none cursor-pointer",
  buttonSecondary:
    "inline-flex items-center justify-center gap-2 px-4 py-2 text-sm font-medium text-[#f5ede3] bg-[#2d2520] hover:bg-[#3a302a] active:scale-[0.98] rounded-xl border border-[#423830] hover:border-[#54463c] transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-amber-500/70 disabled:opacity-50 disabled:pointer-events-none cursor-pointer",
  buttonSage:
    "inline-flex items-center justify-center gap-2 px-4 py-2 text-sm font-semibold text-white bg-emerald-700 hover:bg-emerald-600 active:scale-[0.98] rounded-xl shadow-sm border border-emerald-600/50 transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-500 disabled:opacity-50 disabled:pointer-events-none cursor-pointer",
  buttonTerracotta:
    "inline-flex items-center justify-center gap-2 px-4 py-2 text-sm font-semibold text-white bg-[#b33f2b] hover:bg-[#cb4b35] active:scale-[0.98] rounded-xl shadow-sm border border-red-500/30 transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-red-500 disabled:opacity-50 disabled:pointer-events-none cursor-pointer",
  buttonGhost:
    "inline-flex items-center justify-center gap-2 px-3 py-1.5 text-sm font-medium text-[#b5a596] hover:text-[#f5ede3] hover:bg-[#2d2520] active:scale-[0.98] rounded-lg transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-amber-500/70 disabled:opacity-50 disabled:pointer-events-none cursor-pointer",

  // Vstupy
  input:
    "w-full px-3.5 py-2.5 text-sm bg-[#1a1512] border border-[#382f28] hover:border-[#4a3e35] focus:border-amber-500 focus:ring-1 focus:ring-amber-500 rounded-xl text-[#f5ede3] placeholder-[#7a6c60] transition-colors outline-none",

  // Karty
  cardPlayable:
    "border-amber-500/80 shadow-[0_4px_16px_rgba(217,119,6,0.25)] hover:-translate-y-2 cursor-grab active:cursor-grabbing",
  cardStandard:
    "border-[#382f28] hover:border-[#54463c] hover:-translate-y-1",
  cardDisabled:
    "opacity-65 border-[#2c241e] cursor-default grayscale-[0.2]",

  // Dropzóny
  dropZoneActive:
    "border-amber-500 bg-amber-500/10 scale-[1.02] shadow-[0_0_20px_rgba(217,119,6,0.2)]",
  dropZoneIdle:
    "border-dashed border-[#382f28] bg-[#1d1815]/60 hover:border-[#54463c]",
} as const;
