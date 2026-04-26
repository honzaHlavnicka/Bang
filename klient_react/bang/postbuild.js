import fs from 'fs';
import path from 'path';

const BUILD_DIR = './dist'; 
const JAZYKY = ['cs', 'en']; 

const indexHtmlPath = path.join(BUILD_DIR, 'index.html');

if (!fs.existsSync(indexHtmlPath)) {
    console.error("Chyba: index.html nebyl nalezen! Ujisti se, že se build dokončil.");
    process.exit(1);
}

const htmlObsah = fs.readFileSync(indexHtmlPath, 'utf8');

JAZYKY.forEach(jazyk => {
    const slozkaJazyka = path.join(BUILD_DIR, jazyk);
    
    // 1. Vytvoř složku /cs, /en... pokud neexistuje
    if (!fs.existsSync(slozkaJazyka)){
        fs.mkdirSync(slozkaJazyka, { recursive: true });
    }

    // 2. Vlož do ní kopii index.html
    fs.writeFileSync(path.join(slozkaJazyka, 'index.html'), htmlObsah);
    console.log(`Vytvořena podsložka a index.html pro jazyk: /${jazyk}/`);
});