import { i18n } from "@bang/core";
import modernCs from "./locales/cs.json";
import modernEn from "./locales/en.json";

// Zaregistrování modern překladů do globální i18n instance z core
if (i18n && i18n.addResourceBundle) {
  i18n.addResourceBundle("cs", "translation", modernCs, true, true);
  i18n.addResourceBundle("en", "translation", modernEn, true, true);
}

// Inicializace preferovaného jazyka z localStorage
if (typeof localStorage !== "undefined") {
  const savedLang = localStorage.getItem("preferredLanguage");
  if (savedLang && (savedLang === "cs" || savedLang === "en") && i18n.changeLanguage) {
    i18n.changeLanguage(savedLang);
  }
}

export function setLanguage(lang: "cs" | "en") {
  if (typeof localStorage !== "undefined") {
    localStorage.setItem("preferredLanguage", lang);
  }
  if (i18n.changeLanguage) {
    i18n.changeLanguage(lang);
  }
}

import { useState, useEffect } from "react";

export function getLanguage(): "cs" | "en" {
  const current = (i18n?.language || "cs").toLowerCase();
  return current.startsWith("en") ? "en" : "cs";
}

export function useCurrentLanguage(): "cs" | "en" {
  const [lang, setLang] = useState<"cs" | "en">(getLanguage());

  useEffect(() => {
    const handleLanguageChange = (lng: string) => {
      setLang(lng.toLowerCase().startsWith("en") ? "en" : "cs");
    };

    i18n.on?.("languageChanged", handleLanguageChange);
    return () => {
      i18n.off?.("languageChanged", handleLanguageChange);
    };
  }, []);

  return lang;
}

export const t = (key: string, opts?: any): string => {
  if (i18n && typeof i18n.t === "function") {
    return String(i18n.t(key, opts));
  }
  return key;
};

export { i18n };
export default i18n;
