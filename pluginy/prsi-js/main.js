// --- Prší JS Implementation ---

const PrsiBarva = {
    CERVENE: "CERVENE",
    ZELENE: "ZELENE",
    KULE: "KULE",
    ZALUDY: "ZALUDY"
};

const PrsiBarvaInfo = {
    CERVENE: { imagePrefix: "cervene", nazev: "červené" },
    ZELENE: { imagePrefix: "zelene", nazev: "zelené" },
    KULE: { imagePrefix: "kule", nazev: "kule" },
    ZALUDY: { imagePrefix: "zalud", nazev: "žaludy" }
};

const PrsiHodnota = {
    SEDMA: "SEDMA",
    OSMA: "OSMA",
    DEVITKA: "DEVITKA",
    DESITKA: "DESITKA",
    KRAL: "KRAL",
    ESO: "ESO",
    SVRSEK: "SVRSEK",
    SPODEK: "SPODEK"
};

const ValueMap = {
    "sedma": PrsiHodnota.SEDMA,
    "osma": PrsiHodnota.OSMA,
    "devitka": PrsiHodnota.DEVITKA,
    "desitka": PrsiHodnota.DESITKA,
    "kral": PrsiHodnota.KRAL,
    "eso": PrsiHodnota.ESO,
    "svrsek": PrsiHodnota.SVRSEK,
    "spodek": PrsiHodnota.SPODEK
};

// State (localized to this context/game)
let pocetKaretNaLiznuti = 0;
let aktivniBarva = null;
let aktivniHodnota = null;
let poradiVyher = [];

/**
 * Získá informace o kartě (barvu a hodnotu) z objektu karty.
 * Funguje jak pro čisté JS objekty, tak pro PolyglotKarta z Javy.
 */
function getCardInfo(card) {
    if (!card) return null;
    
    // Zkusíme vytáhnout informace z JS objektu (pokud je to on)
    if (card.barva && card.hodnota) {
        return card;
    }

    // Pokud je to Java objekt (PolyglotKarta), zkusíme získat jméno
    let name = "";
    try {
        name = card.getJmeno();
    } catch (e) {
        // Pokud selže getJmeno, zkusíme to přes toJSON
        try {
            let json = JSON.parse(card.toJSON());
            name = json.jmeno;
        } catch (e2) {
            return null;
        }
    }

    if (name && name.startsWith("marias/")) {
        let content = name.substring(7); // hodnota_barvaPrefix
        let underscoreIndex = content.lastIndexOf("_");
        if (underscoreIndex === -1) return null;

        let hStr = content.substring(0, underscoreIndex);
        let bPrefix = content.substring(underscoreIndex + 1);

        let h = ValueMap[hStr];
        let b = null;
        if (bPrefix === "cervene") b = PrsiBarva.CERVENE;
        else if (bPrefix === "zelene") b = PrsiBarva.ZELENE;
        else if (bPrefix === "kule") b = PrsiBarva.KULE;
        else if (bPrefix === "zalud") b = PrsiBarva.ZALUDY;

        if (h && b) {
            return { barva: b, hodnota: h };
        }
    }
    return null;
}

function vytvorKartu(barva, hodnota) {
    return {
        barva: barva,
        hodnota: hodnota,

        getJmeno: function() {
            let hPart = hodnota.toLowerCase();
            let bPart = PrsiBarvaInfo[barva].imagePrefix;
            return "marias/" + hPart + "_" + bPart;
        },
        getObrazek: function() {
            return this.getJmeno();
        },
        odehrat: function(hra, kym) {
            // ... (zbytek beze změny)
            // Kontrola jestli může hrát na sedmu
            if (pocetKaretNaLiznuti > 0 && hodnota !== PrsiHodnota.SEDMA) {
                return false;
            }

            let odhazovaci = hra.getOdhazovaciBalicek();
            if (odhazovaci.jePrazdny()) {
                this.aplikujEfekt(hra, kym);
                return true;
            }

            // Svršek lze hrát na cokoliv
            if (hodnota === PrsiHodnota.SVRSEK) {
                this.aplikujEfekt(hra, kym);
                return true;
            }

            // Shoda barvy nebo hodnoty
            if (barva === aktivniBarva || hodnota === aktivniHodnota) {
                this.aplikujEfekt(hra, kym);
                return true;
            }

            return false;
        },
        aplikujEfekt: function(hra, kym) {
            aktivniBarva = barva;
            aktivniHodnota = hodnota;

            if (hodnota === PrsiHodnota.SEDMA) {
                if (barva === PrsiBarva.CERVENE) {
                    pocetKaretNaLiznuti += 4;
                } else {
                    pocetKaretNaLiznuti += 2;
                }
            } else if (hodnota === PrsiHodnota.ESO) {
                hra.getSpravceTahu().eso();
            } else if (hodnota === PrsiHodnota.SVRSEK) {
                let moznosti = ["Kule", "Zelené", "Červené", "Žaludy"];
                hra.getKomunikator().posliStavovouZpravu(kym.getJmeno() + " vybírá barvu...");
                
                hra.getKomunikator().pozadejOVyberMoznosti(kym, moznosti, "Na co chceš změnit?", false)
                    .thenAccept((odpoved) => {
                        let vybranaBarva;
                        if (odpoved === "0") vybranaBarva = PrsiBarva.KULE;
                        else if (odpoved === "1") vybranaBarva = PrsiBarva.ZELENE;
                        else if (odpoved === "2") vybranaBarva = PrsiBarva.CERVENE;
                        else if (odpoved === "3") vybranaBarva = PrsiBarva.ZALUDY;
                        else vybranaBarva = PrsiBarva.CERVENE;
                        
                        aktivniBarva = vybranaBarva;
                        hra.getKomunikator().posliStavovouZpravu(kym.getJmeno() + " si vybral barvu: " + PrsiBarvaInfo[vybranaBarva].nazev);
                        hra.getKomunikator().posliRychleOznameniVsem(PrsiBarvaInfo[vybranaBarva].nazev, kym);
                    });
            }
        }
    };
}

const PravidlaPluginu = {

    getKartyDoBalicku: function() {
        let balicek = [];
        for (let b in PrsiBarva) {
            for (let h in PrsiHodnota) {
                balicek.push(vytvorKartu(PrsiBarva[b], PrsiHodnota[h]));
            }
        }
        return balicek;
    },

    pripravitHrace: function(hra, hrac) {
        for (let i = 0; i < 4; i++) {
            hrac.lizni();
        }
    },

    poSpusteniHry: function(hra) {
        pocetKaretNaLiznuti = 0;
        poradiVyher = [];
        
        let karta = null;
        let info = null;
        let pokusu = 0;
        
        while (pokusu < 32) {
            karta = hra.otocVrchniKartu();
            if (!karta) break;
            
            info = getCardInfo(karta);
            if (info && info.hodnota !== PrsiHodnota.SVRSEK) {
                break;
            }
            pokusu++;
        }
        
        if (info) {
            aktivniBarva = info.barva;
            aktivniHodnota = info.hodnota;
        } else {
            // Nouzový plán, pokud se nepodařilo inicializovat barvu rozumně
            aktivniBarva = PrsiBarva.CERVENE;
            aktivniHodnota = PrsiHodnota.DEVITKA;
        }
    },

    poOdehrani: function(hra, kym) {
        hra.getSpravceTahu().dalsiHracSUpozornenim();
        
        if (kym.getKarty().isEmpty()) {
            let hracId = kym.getId();
            let uzTamJe = poradiVyher.some(h => h.getId() === hracId);

            if (!uzTamJe) {
                poradiVyher.push(kym);
                
                let hraci = hra.getHraci();
                let pocetHrajicich = 0;
                for (let i = 0; i < hraci.size(); i++) {
                    let h = hraci.get(i);
                    let hId = h.getId();
                    if (!poradiVyher.some(v => v.getId() === hId)) {
                        pocetHrajicich++;
                    }
                }
                
                if (pocetHrajicich <= 1) {
                    this.ukoncitHru(hra);
                }
            }
        }
    },

    hracChceLiznout: function(hra, hrac) {
        if (hrac.jeNaTahu()) {
            if (pocetKaretNaLiznuti > 0) {
                for (let i = 0; i < pocetKaretNaLiznuti; i++) {
                    hrac.lizni();
                }
                pocetKaretNaLiznuti = 0;
            } else {
                hrac.lizni();
            }
            hra.getSpravceTahu().dalsiHracSUpozornenim();
            return true;
        }
        return false;
    },

    hracChceUkoncitTah: function(hra, hrac) {
        return false;
    },

    muzeZahrat: function(hra, co, hrac) {
        let info = getCardInfo(co);
        if (pocetKaretNaLiznuti > 0) {
            return info && info.hodnota === PrsiHodnota.SEDMA;
        }
        return true;
    },

    getViditelnePrvky: function(hra) {
        return ["ODHAZOVACI_BALICEK", "DOBIRACI_BALICEK"];
    },

    getVychoziZadniObrazek: function(hra) {
        return "marias/zezadu";
    },

    ukoncitHru: function(hra) {
        let hraci = hra.getHraci();
        let zbyvajici = [];
        for (let i = 0; i < hraci.size(); i++) {
            let h = hraci.get(i);
            let hId = h.getId();
            if (!poradiVyher.some(v => v.getId() === hId)) {
                zbyvajici.push(h);
            }
        }
        
        let vysledky = [];
        for (let h of poradiVyher) {
            vysledky.push([h]);
        }
        if (zbyvajici.length > 0) {
            vysledky.push(zbyvajici);
        }
        
        hra.getKomunikator().posliVysledky(vysledky);
        hra.getKomunikator().posliKonecHry();
    }
};
