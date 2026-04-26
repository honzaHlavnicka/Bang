import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

// Naimportování externích JSON souborů
import csTranslation from './locales/cs.json';
import enTranslation from './locales/en.json';

i18n.use(initReactI18next).init({
  resources: {
    cs: { translation: csTranslation },
    en: { translation: enTranslation }
  },
  lng: "en",
  fallbackLng: "en",
  interpolation: {
    escapeValue: false
  }
});

export default i18n;

// Typová magie pro napovídání v editoru
declare module 'i18next' {
  interface CustomTypeOptions {
    defaultNS: 'translation';
    resources: {
      translation: typeof csTranslation;
    };
  }
}