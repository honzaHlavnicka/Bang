import i18n from 'i18next';
import csTranslation from './locales/cs.json';
import enTranslation from './locales/en.json';

// Inicializace výchozích jazykových balíčků pro bezchybný překlad hlášek a chyb
if (!i18n.isInitialized) {
  i18n.init({
    lng: 'cs',
    fallbackLng: 'cs',
    supportedLngs: ['cs', 'en'],
    resources: {
      cs: { translation: csTranslation },
      en: { translation: enTranslation },
    },
    interpolation: {
      escapeValue: false,
    },
  });
} else {
  if (!i18n.hasResourceBundle?.('cs', 'translation')) {
    i18n.addResourceBundle?.('cs', 'translation', csTranslation, true, false);
  }
  if (!i18n.hasResourceBundle?.('en', 'translation')) {
    i18n.addResourceBundle?.('en', 'translation', enTranslation, true, false);
  }
}

export const t = (key: string, opts?: any): string => String(i18n.t(key, opts));

export default i18n;
export { i18n };
