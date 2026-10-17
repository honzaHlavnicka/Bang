import i18n from 'i18next';

// Pokud spotřebitelská aplikace i18n ještě neinicializovala, inicializujeme základní konfiguraci,
// aby byly okamžitě dostupné metody t, addResourceBundle, removeResourceBundle, on, off.
if (!i18n.isInitialized) {
  i18n.init({
    lng: 'cs',
    fallbackLng: 'cs',
    resources: {},
    interpolation: {
      escapeValue: false,
    },
  });
}

export const t = (key: string, opts?: any): string => String(i18n.t(key, opts));

export default i18n;
export { i18n };
