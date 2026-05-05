/**
 * Globální konfigurace aplikace.
 * Načítá se z window.__APP_CONFIG__, který je injektován v postbuild fázi.
 */

export interface AppConfig {
    isIframe: boolean;
    showCookies: boolean;
    enableChat: boolean;
}

// Výchozí konfigurace (pro lokální vývoj nebo pokud chybí window.__APP_CONFIG__)
const defaultConfig: AppConfig = {
    isIframe: false,
    showCookies: true,
    enableChat: true,
};

// @ts-expect-error - window.__APP_CONFIG__ není v TS definován
const windowConfig = window.__APP_CONFIG__;

let config: AppConfig = { ...defaultConfig, ...windowConfig };

// Dev hack pro lokální testování iframe módu: ?iframe=true
if (import.meta.env.DEV) {
    const params = new URLSearchParams(window.location.search);
    if (params.get('iframe') === 'true') {
        config = {
            ...config,
            isIframe: true,
            showCookies: false,
            enableChat: false,
        };
        console.log('🛠️ Dev Hack: Aplikace běží v simulovaném iframe módu.');
    }
}

export default config;
