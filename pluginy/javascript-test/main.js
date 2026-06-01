// --- Privátní část pluginu (Java engine ji nevidí a nevolá) ---

// Pomocná funkce pro vytvoření základních vlastností karty
function vytvorZakladKarty(jmeno, obrazek) {
    return {
        getJmeno: function() { return jmeno; },
        getObrazek: function() { return obrazek; }
    };
}


// --- Veřejná část pluginu (Tento objekt si Java vytáhne do adaptéru) ---

const PravidlaPluginu = {

    // 1. GENEROVÁNÍ KARET DO BALÍČKU
    ziskejKartyDoBalicku: function() {
        let balicek = [];

        // Přidáme 5x Obecnou kartu (Hnědá - má pouze odehrat)
        for (let i = 0; i < 5; i++) {
            let karta = vytvorZakladKarty("Testovací Třesk", "bang");
            karta.odehrat = function(hra, kym) {
                return true; 
            };
            balicek.push(karta);
        }

        // Přidáme 5x Vyložitelnou kartu (Modrá - má pouze vyložit)
        for (let i = 0; i < 5; i++) {
            let karta = vytvorZakladKarty("Zkušební Ochrana", "barel");
            karta.vylozit = function(predKoho, kym) {
                return true;
            };
            balicek.push(karta);
        }

        // Přidáme 5x Hybridní kartu (Má odehrat i vyložit)
        for (let i = 0; i < 5; i++) {
            let karta = vytvorZakladKarty("Konečný Verdikt", "smrt");
            karta.odehrat = function(hra, kym) {
                return true;
            };
            karta.vylozit = function(predKoho, kym) {
                return true;
            };
            balicek.push(karta);
        }

        return balicek;
    },

    // 2. LOGIKA PRAVIDEL HRY

    // Dá každému hráči na začátku jednu kartu z balíčku
    pripravitHrace: function(hra, hrac) {
        // Tuto metodu (a další na hráči) nezapomeň v Javě označit @HostAccess.Export
        hrac.lizni(); 
    },

    // Povolí hráči líznout si kartu, když chce
    hracChceLiznout: function(hra, kdo) {
        return true;
    },

    // Povolí hráči ukončit tah, když chce
    hracChceUkoncitTah: function(hra, kdo) {
        return true;
    },

    // Povolí hrát jakoukoliv kartu během tahu
    muzeZahrat: function(hra, co, kdo) {
        return true;
    }

};
