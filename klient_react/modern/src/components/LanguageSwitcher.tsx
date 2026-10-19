import React, { useState, useEffect } from "react";
import { Globe } from "lucide-react";
import { getLanguage, setLanguage, i18n } from "../i18n";

export const LanguageSwitcher: React.FC<{ className?: string }> = ({ className = "" }) => {
  const [currentLang, setCurrentLang] = useState<"cs" | "en">(getLanguage());

  useEffect(() => {
    const handleLanguageChange = (lng: string) => {
      setCurrentLang(lng.toLowerCase().startsWith("en") ? "en" : "cs");
    };

    i18n.on?.("languageChanged", handleLanguageChange);
    return () => {
      i18n.off?.("languageChanged", handleLanguageChange);
    };
  }, []);

  const toggleLanguage = (lang: "cs" | "en") => {
    setLanguage(lang);
    setCurrentLang(lang);
  };

  return (
    <div
      className={`inline-flex items-center gap-1 p-1 bg-[#1c1613] border border-[#3d3229] rounded-xl text-xs select-none shadow-sm ${className}`}
      title="Změnit jazyk / Change language"
    >
      <Globe className="w-3.5 h-3.5 text-amber-400 ml-1.5 mr-0.5 shrink-0" />
      <button
        type="button"
        onClick={() => toggleLanguage("cs")}
        className={`px-2 py-0.5 rounded-lg font-bold transition-all cursor-pointer ${
          currentLang === "cs"
            ? "bg-amber-600 text-[#f5ede3] shadow-sm"
            : "text-[#b5a596] hover:text-[#f5ede3]"
        }`}
      >
        CZ
      </button>
      <button
        type="button"
        onClick={() => toggleLanguage("en")}
        className={`px-2 py-0.5 rounded-lg font-bold transition-all cursor-pointer ${
          currentLang === "en"
            ? "bg-amber-600 text-[#f5ede3] shadow-sm"
            : "text-[#b5a596] hover:text-[#f5ede3]"
        }`}
      >
        EN
      </button>
    </div>
  );
};

export default LanguageSwitcher;
