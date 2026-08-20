/**
 * TypeScript typové definice pro Bang SDK.
 * Tento soubor lze použít pro vývoj JavaScript a TypeScript pluginů
 * pro deskoherní engine.
 */

/**
 * Chyba ve hře a její kódy.
 */
declare enum Chyba {
    NEPRIPOJEN_KE_HRE = "NEPRIPOJEN_KE_HRE",
    KARTA_NEEXISTUJE = "KARTA_NEEXISTUJE",
    KARTA_NENI_HRATELNA = "KARTA_NENI_HRATELNA",
    KARTA_NEJDE_ZAHRAT = "KARTA_NEJDE_ZAHRAT",
    HRA_NEEXISTUJE = "HRA_NEEXISTUJE",
    POSTAVA_NENI_NA_VYBER = "POSTAVA_NENI_NA_VYBER",
    NEJSI_NA_TAHU = "NEJSI_NA_TAHU",
    NEMUZES_UKONCIT_TAH = "NEMUZES_UKONCIT_TAH",
    KARTA_NEJDE_SPALIT = "KARTA_NEJDE_SPALIT",
    NENI_VYLOZITELNA = "NENI_VYLOZITELNA",
    KARTU_NEJDE_VYLOZIT = "KARTU_NEJDE_VYLOZIT",
    CHYBA_PROTOKOLU = "CHYBA_PROTOKOLU",
    NEJDE_SI_LIZNOUT = "NEJDE_SI_LIZNOUT",
    SPATNE_HESLO = "SPATNE_HESLO",
    UZ_PRIPOJEN = "UZ_PRIPOJEN",
    DOSLI_KARTY_V_BALICKU = "DOSLI_KARTY_V_BALICKU",
    NEJSI_ADMIN_HRY = "NEJSI_ADMIN_HRY",
    PLNY_SERVER = "PLNY_SERVER",
    VYHOZEN_ZE_HRY = "VYHOZEN_ZE_HRY"
}

/**
 * Prvky UI, které se mohou zobrazit nebo skrýt.
 */
declare enum UIPrvek {
    ZIVOTY = "ZIVOTY",
    UKONCENI_TAHU = "UKONCENI_TAHU",
    POSTAVA = "POSTAVA",
    ROLE = "ROLE",
    VYLOZENE_KARTY = "VYLOZENE_KARTY",
    ODHAZOVACI_BALICEK = "ODHAZOVACI_BALICEK",
    DOBIRACI_BALICEK = "DOBIRACI_BALICEK",
    OHEN = "OHEN"
}

/**
 * Java CompletableFuture reprezentovaný v JavaScriptu.
 */
interface CompletableFuture<T> {
    thenAccept(callback: (value: T) => void): CompletableFuture<void>;
    thenApply<U>(callback: (value: T) => U): CompletableFuture<U>;
}

/**
 * Java List reprezentovaný v JavaScriptu.
 */
interface JavaList<T> {
    get(index: number): T;
    size(): number;
    isEmpty(): boolean;
    add(item: T): boolean;
    clear(): void;
    contains(item: T): boolean;
    remove(index: number): T;
    remove(item: T): boolean;
}

/**
 * Java Stack reprezentovaný v JavaScriptu.
 */
interface JavaStack<T> extends JavaList<T> {
    push(item: T): T;
    pop(): T;
    peek(): T;
}

/**
 * Balíček karet nebo jiných objektů.
 */
interface Balicek<T> {
    zamichej(): void;
    lizni(): T | null;
    lizni(n: number): JavaList<T>;
    nahledni(n: number): JavaList<T>;
    nahledni(): T | null;
    vratNaSpodek(karta: T): void;
    vratNahoru(karta: T): void;
    jePrazdny(): boolean;
    pocet(): number;
    otoc(): void;
}

/**
 * Třída samotné hry (vnitřní logika).
 */
interface Hra {
    novyHrac(): Hrac;
    hracVytvoren(hrac: Hrac): void;
    getHraci(): JavaList<Hrac>;
    getHrajiciHraci(): JavaList<Hrac>;
    getHrac(id: number): Hrac | null;
    getKomunikator(): KomunikatorHry;
    isZahajena(): boolean;
    getHerniPravidla(): any; // Vrací pravidla
    setZahajena(zahajena: boolean): void;
    getBalicek(): Balicek<Karta>;
    skoncil(kdo: Hrac): void;
    vyhral(kdo: Hrac): void;
    prohodBalicky(): void;
    getSpravceTahu(): SpravceTahu;
    getOdhazovaciBalicek(): Balicek<Karta>;
    otocVrchniKartu(): Karta | null;
}

/**
 * Reprezentace jednoho hráče.
 */
interface Hrac {
    priraditRoliNaZacatkuHry(role: Role): void;
    vyberZPostav(p1: Postava, p2: Postava): void;
    odeberZivot(): boolean;
    pridejZivot(): boolean;
    getZivoty(): number;
    setZivoty(zivoty: number): void;
    getMaximumZivotu(): number;
    getRole(): Role;
    getPostava(): Postava;
    getKarty(): JavaList<Karta>;
    getId(): number;
    getJmeno(): string;
    setJmeno(jmeno: string): void;
    jeNaTahu(): boolean;
    setMaximumZivotu(maximumZivotu: number): void;
    setPostava(postava: Postava): Postava;
    setPostava(jmeno: string): void;
    setRole(role: Role): void;
    getHra(): Hra;
    jeZivy(): boolean;
    odehranaKarta(id: string): void;
    spalitKartu(id: string): void;
    vylozitKartu(id: string, idHrace: string): void;
    lizni(): void;
    lizniKontrolovane(): void;
    pridejEfekt(efekt: Efekt): void;
    fyzickaVzdalenostK(komu: Hrac): number;
    vzdalenostK(komu: Hrac): number;
    vzdalenostPod(max: number, iZpetne?: boolean): JavaList<Hrac>;
    konecTahu(): void;
    getVylozeneKarty(): JavaList<Karta>;
    getEfekty(): JavaList<Efekt>;
    toJSON(): string;
    zahajitTah(): void;
    getCelkovyBonusOdstupu(): number;
    getCelkovyBonusDosahu(): number;
    odeberVylozenouKartu(karta: VylozitelnaKarta): void;
    pridejVylozenouKartu(karta: VylozitelnaKarta, kym: Hrac): void;
}

/**
 * Obecná reprezentace karty.
 */
interface Karta {
    getId(): number;
    toJSON(): string;
    predSpalenim(): void;
    getObrazek(): string;
    getJmeno(): string;
    getZadniObrazek(): string;
}

/**
 * Karta, která je hratelná z ruky.
 */
interface HratelnaKarta extends Karta {
    odehrat(hra: Hra, kym: Hrac): boolean;
}

/**
 * Karta, která se vykládá na stůl.
 */
interface VylozitelnaKarta extends Karta {
    vylozit(predKoho: Hrac, kym: Hrac): boolean;
    getEfekt(): Efekt;
    spalitVylozenou(): void;
}

/**
 * Zástupná karta, která má zároveň vlastnosti hratelné i vyložitelné karty.
 */
interface HybridniKarta extends HratelnaKarta, VylozitelnaKarta {}

/**
 * Postava s vlastním popisem a životy.
 */
interface Postava {
    getJmeno(): string;
    name(): string;
    getPopis(): string;
    getMaximumZivotu(): number;
    pridaniPostavy(komu: Hrac): void;
    odebraniPostavy(komu: Hrac): void;
}

/**
 * Role hráče.
 */
interface Role {
    name(): string;
}

/**
 * Správce tahu hry.
 */
interface SpravceTahu {
    getHrajiciHraci(): JavaList<Hrac>;
    dalsiHrac(): Hrac;
    dalsiHracPodleRole(role: Role): Hrac;
    dalsiHracPodlePodminky(podminka: (hrac: Hrac) => boolean): Hrac;
    dalsiHracSUpozornenim(): void;
    eso(): Hrac;
    setNasobicTahu(kolik: number): void;
    getNaTahu(): Hrac;
    vyraditHrace(koho: Hrac): void;
    vratitHrace(koho: Hrac): void;
    pridatHrace(koho: Hrac): void;
    zmenaSmeru(): void;
}

/**
 * Rozhraní pro komunikaci s klientem.
 */
interface KomunikatorHry {
    posliVsem(co: string): void;
    posliVsem(co: string, komuNe: Hrac): void;
    posli(komu: Hrac, co: string): void;
    posliZmenuPostavy(hrac: Hrac): void;
    posliChybu(komu: Hrac, chyba: Chyba): void;
    posliStavovouZpravu(zprava: string): void;
    posliZmenuPoctuKaret(hrac: Hrac): void;
    posliZmenuPoctuZivotu(hrac: Hrac): void;
    posliZahajeniTahu(hrac: Hrac): void;
    posliZmenuJmena(hrac: Hrac): void;
    posliNovehoHrace(hrac: Hrac): void;
    posliZahajeniHry(): void;
    posliSkonceniHrace(hrac: Hrac): void;
    posliVitezstvi(hrac: Hrac): void;
    posliOdebraniKarty(hrac: Hrac, karta: Karta): void;
    posliSpaleniKarty(hrac: Hrac, karta: Karta): void;
    posliNovouKartu(hrac: Hrac, karta: Karta): void;
    posliSpaleniVylozenéKarty(karta: Karta, odkud: Hrac): void;
    posliVylozeniKarty(hrac: Hrac, predKoho: Hrac, karta: Karta): void;
    posliRychleOznameniVsem(oznameni: string, vyjimka: Hrac | null): void;
    posliRychleOznameni(oznameni: string, komu: Hrac): void;
    posliKonecHry(): void;
    posliVysledky(vysledky: Hrac[][]): void;
    posliZadniObrazekLizacihoBalicku(obrazek: string): void;
    pozadejOVyberMoznosti(odKoho: Hrac, moznosti: JavaList<string> | string[], nadpis: string, closable: boolean): CompletableFuture<string>;
    pozadejOKarty(odKoho: Hrac, karty: JavaList<Karta> | Karta[], nadpis: string, min: number, max: number, closable: boolean): CompletableFuture<string>;
    pozadejOHrace(odKoho: Hrac, hraci: JavaList<Hrac> | Hrac[], nadpis: string, min: number, max: number, closable: boolean): CompletableFuture<string>;
    pozadejOdpoved(otazka: string, komu: Hrac): CompletableFuture<string>;
    pozadejOText(odKoho: Hrac, nadpis: string, placeholder: string | null, vychozi: string | null, closable: boolean): CompletableFuture<string>;
}

/**
 * Efekt vyložené karty nebo postavy.
 */
interface Efekt {
    getBonusDosahu?(): number;
    getBonusOdstupu?(): number;
    naZacatekTahu?(hra: Hra, hrac: Hrac): void;
    naKonecTahu?(hra: Hra, hrac: Hrac): void;
    poZtrateZivota?(hra: Hra, hrac: Hrac): void;
    poOdehraniKarty?(hra: Hra, hrac: Hrac, kym: Hrac, karta: Karta): void;
    kdyzNemaKarty?(hra: Hra, hrac: Hrac): void;
    poZabitiKohokoliv?(ja: Hrac, zabity: Hrac): void;
    odebrani?(odKoho: Hrac): void;
    prirazeni?(komu: Hrac): void;
}

/**
 * Struktura JS/TS objektu reprezentujícího kartu.
 */
interface JSKarta {
    getJmeno(): string;
    getObrazek(): string;
    getZadniObrazek?(): string;
    predSpalenim?(): void;
    // Pokud je karta hratelná z ruky
    odehrat?(hra: Hra, kym: Hrac): boolean;
    // Pokud je karta vyložitelná na stůl
    vylozit?(predKoho: Hrac, kym: Hrac): boolean;
    spalitVylozenou?(): void;
    // Pokud má vyložená karta nebo postava efekty (implementuje rozhraní Efekt přímo)
    getBonusDosahu?(): number;
    getBonusOdstupu?(): number;
    naZacatekTahu?(hra: Hra, hrac: Hrac): void;
    naKonecTahu?(hra: Hra, hrac: Hrac): void;
    poZtrateZivota?(hra: Hra, hrac: Hrac): void;
    poOdehraniKarty?(hra: Hra, hrac: Hrac, kym: Hrac, karta: Karta): void;
    kdyzNemaKarty?(hra: Hra, hrac: Hrac): void;
    poZabitiKohokoliv?(ja: Hrac, zabity: Hrac): void;
    odebrani?(odKoho: Hrac): void;
    prirazeni?(komu: Hrac): void;
}

/**
 * Struktura JS/TS objektu reprezentujícího postavu.
 */
interface JSPostava {
    getJmeno(): string;
    name(): string;
    getPopis(): string;
    getMaximumZivotu(): number;
    pridaniPostavy?(komu: Hrac): void;
    odebraniPostavy?(komu: Hrac): void;
}

/**
 * Globální nástroje poskytované sandboxed JS kontextem.
 */
declare const Nastroje: {
    vytvorEfekt(jsObjektEfektu: Efekt): Efekt;
};

/**
 * Hlavní objekt pravidel, který musí JS/TS plugin exportovat / nadefinovat v globálním prostoru.
 */
interface PravidlaPluginu {
    getKartyDoBalicku(): JSKarta[];
    pripravitHrace(hra: Hra, hrac: Hrac): void;
    poSpusteniHry(hra: Hra): void;
    poOdehrani(hra: Hra, kym: Hrac): void;
    dosliZivoty?(hra: Hra, komu: Hrac): void;
    hracChceUkoncitTah?(hra: Hra, kdo: Hrac): boolean;
    hracChceLiznout?(hra: Hra, kdo: Hrac): boolean;
    getPostavy?(): JSPostava[];
    zacalTah?(hra: Hra, komu: Hrac): void;
    skoncilTah?(hra: Hra, komu: Hrac): void;
    muzeSpalit?(hra: Hra, co: Karta): boolean;
    getViditelnePrvky?(hra: Hra): (UIPrvek | keyof typeof UIPrvek)[];
    muzeZahrat?(hra: Hra, co: Karta, kdo: Hrac): boolean;
    getVychoziZadniObrazek?(hra: Hra): string;
    muzeVylozit?(hra: Hra, kdo: Hrac, co: VylozitelnaKarta): boolean;
    spustitPrvniTah?(hra: Hra, spravceTahu: SpravceTahu): void;
    uiButtonClicked?(hra: Hra, hrac: Hrac, uiId: number): void;
}

// Globální deklarace pro TypeScript
declare global {
    const Nastroje: {
        vytvorEfekt(jsObjektEfektu: Efekt): Efekt;
    };
    const Chyba: typeof Chyba;
    const PravidlaPluginu: PravidlaPluginu;
}
export {};
