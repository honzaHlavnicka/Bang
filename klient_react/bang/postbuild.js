import fs from 'fs';
import path from 'path';

const BUILD_DIR = './dist'; 
const JAZYKY = ['cs', 'en'];
const VYCHOZI_JAZYK = 'en'; // Sem dej jazyk, který chceš mít na hlavní adrese jako fallback

console.log("----");
console.log("| Probíhá dogenerování html pro jazyky a SEO");


const indexHtmlPath = path.join(BUILD_DIR, 'index.html');

if (!fs.existsSync(indexHtmlPath)) {
    console.error("| Chyba: index.html nebyl nalezen!");
    console.log("----");
    process.exit(1);
}

const htmlSablona = fs.readFileSync(indexHtmlPath, 'utf8');

console.log("| Zpracování jednotlivých jazyků:");

// 1. Zpracování podsložek pro všechny jazyky (/cs/, /en/)
JAZYKY.forEach(jazyk => {
    const jsonCesta = path.join('./locales', `${jazyk}.json`);
    const preklady = JSON.parse(fs.readFileSync(jsonCesta, 'utf8'));

    let prelozeneHtml = htmlSablona
        .replace(/__LANG__/g, jazyk)
        .replace(/__TITLE__/g, preklady.seo.title)
        .replace(/__DESC__/g, preklady.seo.description);

    const slozkaJazyka = path.join(BUILD_DIR, jazyk);
    if (!fs.existsSync(slozkaJazyka)){
        fs.mkdirSync(slozkaJazyka, { recursive: true });
    }

    fs.writeFileSync(path.join(slozkaJazyka, 'index.html'), prelozeneHtml);
    console.log(`| | Vygenerováno SEO HTML pro: /${jazyk}/`);
});

// 2. Zpracování kořenového index.html
const vychoziPreklady = JSON.parse(fs.readFileSync(path.join('./locales', `${VYCHOZI_JAZYK}.json`), 'utf8'));
let vychoziHtml = htmlSablona
    .replace(/__LANG__/g, VYCHOZI_JAZYK)
    .replace(/__TITLE__/g, vychoziPreklady.seo.title)
    .replace(/__DESC__/g, vychoziPreklady.seo.description);

fs.writeFileSync(indexHtmlPath, vychoziHtml);
console.log(`| Kořenový index.html byl nastaven do výchozího jazyka (${VYCHOZI_JAZYK}).`);
console.log("----");
