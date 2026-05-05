import fs from 'fs';
import path from 'path';

const BUILD_DIR = './dist'; 
const JAZYKY = ['cs', 'en'];
const VYCHOZI_JAZYK = 'en'; // Sem dej jazyk, který chceš mít na hlavní adrese jako fallback

console.log("----");
console.log("| Probíhá dogenerování html pro jazyky, SEO a konfiguraci");

const indexHtmlPath = path.join(BUILD_DIR, 'index.html');

if (!fs.existsSync(indexHtmlPath)) {
    console.error("| Chyba: index.html nebyl nalezen!");
    console.log("----");
    process.exit(1);
}

const htmlSablona = fs.readFileSync(indexHtmlPath, 'utf8');

// Konfigurace pro různé verze webu
const CONFIG_STANDARD = '<script>window.__APP_CONFIG__ = { isIframe: false, showCookies: true, enableChat: true };</script>';
const CONFIG_IFRAME = '<script>window.__APP_CONFIG__ = { isIframe: true, showCookies: false, enableChat: false };</script>';

/**
 * Funkce pro vložení konfigurace do <head>
 */
function injectConfig(html, configScript) {
    return html.replace('</head>', `    ${configScript}\n  </head>`);
}

console.log("| Zpracování jednotlivých jazyků:");

// 1. Zpracování podsložek pro všechny jazyky (/cs/, /en/)
JAZYKY.forEach(jazyk => {
    const jsonCesta = path.join('./locales', `${jazyk}.json`);
    const preklady = JSON.parse(fs.readFileSync(jsonCesta, 'utf8'));

    let prelozeneHtml = htmlSablona
        .replace(/__LANG__/g, jazyk)
        .replace(/__TITLE__/g, preklady.seo.title)
        .replace(/__DESC__/g, preklady.seo.description);

    // Vložíme standardní konfiguraci
    prelozeneHtml = injectConfig(prelozeneHtml, CONFIG_STANDARD);

    const slozkaJazyka = path.join(BUILD_DIR, jazyk);
    if (!fs.existsSync(slozkaJazyka)){
        fs.mkdirSync(slozkaJazyka, { recursive: true });
    }

    fs.writeFileSync(path.join(slozkaJazyka, 'index.html'), prelozeneHtml);
    console.log(`| | Vygenerováno SEO HTML pro: /${jazyk}/`);
});

// 2. Zpracování kořenového index.html (standardní web)
const vychoziPreklady = JSON.parse(fs.readFileSync(path.join('./locales', `${VYCHOZI_JAZYK}.json`), 'utf8'));
let vychoziHtml = htmlSablona
    .replace(/__LANG__/g, VYCHOZI_JAZYK)
    .replace(/__TITLE__/g, vychoziPreklady.seo.title)
    .replace(/__DESC__/g, vychoziPreklady.seo.description);

vychoziHtml = injectConfig(vychoziHtml, CONFIG_STANDARD);

fs.writeFileSync(indexHtmlPath, vychoziHtml);
console.log(`| Kořenový index.html byl nastaven do výchozího jazyka (${VYCHOZI_JAZYK}) s běžnou konfigurací.`);

// 3. Vytvoření iframe verze (v dist/iframe/index.html)
let iframeHtml = htmlSablona
    .replace(/__LANG__/g, VYCHOZI_JAZYK)
    .replace(/__TITLE__/g, vychoziPreklady.seo.title)
    .replace(/__DESC__/g, vychoziPreklady.seo.description);

iframeHtml = injectConfig(iframeHtml, CONFIG_IFRAME);

const iframeDir = path.join(BUILD_DIR, 'iframe');
if (!fs.existsSync(iframeDir)) {
    fs.mkdirSync(iframeDir, { recursive: true });
}

fs.writeFileSync(path.join(iframeDir, 'index.html'), iframeHtml);
console.log(`| Vygenerována iframe verze v: /iframe/index.html`);

console.log("----");
