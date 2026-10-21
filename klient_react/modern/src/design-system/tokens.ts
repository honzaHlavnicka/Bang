/**
 * Design Tokens & Constants for Bang! Modern Client
 * Definice barev, sémantických tříd a stylů podle DESIGN_GUIDE.md
 */

export const colors = {
  canvas: "#16120f",
  surface: "#221913",
  surfaceRaised: "#291e17",
  surfaceHover: "#35271e",
  borderSubtle: "#443427",
  borderActive: "#6b5038",
  textMain: "#f5ede3",
  textMuted: "#bcae9f",
  textSubtle: "#7d6e60",
  accentAmber: "#d97706",
  accentAmberHover: "#f59e0b",
  accentSage: "#3a6845",
  accentSageHover: "#488257",
  accentTerracotta: "#b33f2b",
  accentTerracottaHover: "#cb4b35",
} as const;

export const themeClasses = {
  // Základní plochy – teplý stůl, matné dřevo, kůže, deskové komponenty
  canvas: "bg-[#16120f] text-[#f5ede3] min-h-screen selection:bg-amber-600/30 selection:text-amber-200",
  surface: "bg-[#211710] border border-[#3d2f25] rounded-2xl shadow-lg",
  surfaceRaised: "bg-[#281d15] border border-[#48372b] rounded-2xl shadow-xl",
  surfaceSubtle: "bg-[#18100a] border border-[#33241a] rounded-xl",

  // Texty – teplá slonová kost a zemitá patina
  heading: "font-semibold text-[#f5ede3] tracking-tight",
  body: "text-sm text-[#f5ede3]",
  muted: "text-xs text-[#bcae9f]",
  subtle: "text-xs text-[#7d6e60]",

  // Tlačítka – plochá, bez přechodů, čistá herní tlačítka
  buttonPrimary:
    "inline-flex items-center justify-center gap-2 px-4 py-2 text-sm font-semibold text-white bg-amber-600 hover:bg-amber-500 active:scale-[0.98] rounded-xl shadow-sm border border-amber-500/50 transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-amber-500 disabled:opacity-50 disabled:pointer-events-none cursor-pointer",
  buttonSecondary:
    "inline-flex items-center justify-center gap-2 px-4 py-2 text-sm font-medium text-[#f5ede3] bg-[#2d2520] hover:bg-[#3a302a] active:scale-[0.98] rounded-xl border border-[#423830] hover:border-[#54463c] transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-amber-500/70 disabled:opacity-50 disabled:pointer-events-none cursor-pointer",
  buttonSage:
    "inline-flex items-center justify-center gap-2 px-4 py-2 text-sm font-semibold text-white bg-emerald-700 hover:bg-emerald-600 active:scale-[0.98] rounded-xl shadow-sm border border-emerald-600/50 transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-500 disabled:opacity-50 disabled:pointer-events-none cursor-pointer",
  buttonTerracotta:
    "inline-flex items-center justify-center gap-2 px-4 py-2 text-sm font-semibold text-white bg-[#b33f2b] hover:bg-[#cb4b35] active:scale-[0.98] rounded-xl shadow-sm border border-red-500/30 transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-red-500 disabled:opacity-50 disabled:pointer-events-none cursor-pointer",
  buttonGhost:
    "inline-flex items-center justify-center gap-2 px-3 py-1.5 text-sm font-medium text-[#bcae9f] hover:text-[#f5ede3] hover:bg-[#2d2520] active:scale-[0.98] rounded-lg transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-amber-500/70 disabled:opacity-50 disabled:pointer-events-none cursor-pointer",

  // Vstupy – zapuštěné dřevěné destičky
  input:
    "w-full px-3.5 py-2.5 text-sm bg-[#150e09] border border-[#3c2c20] hover:border-[#523d2c] focus:border-amber-500/80 focus:ring-1 focus:ring-amber-500/50 rounded-xl text-[#f5ede3] placeholder-[#7d6e60] transition-colors outline-none",

  // Karty
  cardPlayable:
    "border-amber-500/80 shadow-[0_4px_16px_rgba(217,119,6,0.3)] hover:-translate-y-2 cursor-grab active:cursor-grabbing",
  cardStandard:
    "border-[#382f28]/70 hover:border-[#5c4635] hover:-translate-y-1",
  cardDisabled:
    "border-[#2e2219] opacity-50 grayscale-[0.4] cursor-default",

  // Dropzóny
  dropZoneActive:
    "border-amber-500 bg-amber-600/15 scale-[1.02] shadow-[0_0_20px_rgba(217,119,6,0.3)]",
  dropZoneIdle:
    "border-dashed border-[#3d2d20] bg-[#18110c]/70 hover:border-[#543e2c]",
} as const;
