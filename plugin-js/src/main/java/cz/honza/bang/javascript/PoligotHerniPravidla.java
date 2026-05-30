package cz.honza.bang.javascript;

import cz.honza.bang.sdk.Balicek;
import cz.honza.bang.sdk.HerniPravidla;
import cz.honza.bang.sdk.Hra;
import cz.honza.bang.sdk.Hrac;
import cz.honza.bang.sdk.Karta;
import cz.honza.bang.sdk.Postava;
import cz.honza.bang.sdk.SpravceTahu;
import cz.honza.bang.sdk.UIPrvek;
import cz.honza.bang.sdk.VylozitelnaKarta;
import java.util.Stack;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/**
 * Adaptér napojující JavaScriptové metody na Java HerniPravidla.
 *
 * @author honza
 */
public class PoligotHerniPravidla implements HerniPravidla {

    private final Hra hra;
    private final Value jsFunkce;
    private final Context graalContext;

    public PoligotHerniPravidla(Hra hra, Value jsFunkce, Context graalContext) {
        this.hra = hra;
        this.jsFunkce = jsFunkce;
        this.graalContext = graalContext;
    }

    @Override
    public void poSpusteniHry() {
        if (jsFunkce.hasMember("poSpusteniHry")) {
            jsFunkce.invokeMember("poSpusteniHry", hra);
        }
        // Pokud metoda není implementována, prostě neděláme nic.
    }

    @Override
    public void pripravitHrace(Hrac hrac) {
        if (jsFunkce.hasMember("pripravitHrace")) {
            jsFunkce.invokeMember("pripravitHrace", hra, hrac);
        }
        // Pokud chybí, hráč se nepřipraví. To může být problém pro běh hry,
        // plugin by TUTO metodu měl ideálně implementovat.
    }

    @Override
    public void poOdehrani(Hrac kym) {
        if (jsFunkce.hasMember("poOdehrani")) {
            jsFunkce.invokeMember("poOdehrani", hra, kym);
        }
    }

    @Override
    public void dosliZivoty(Hrac komu) {
        if (jsFunkce.hasMember("dosliZivoty")) {
            jsFunkce.invokeMember("dosliZivoty", hra, komu);
        }
    }

    @Override
    public boolean hracChceUkoncitTah(Hrac kdo) {
        if (jsFunkce.hasMember("hracChceUkoncitTah")) {
            Value vysledek = jsFunkce.getMember("hracChceUkoncitTah").execute(hra, kdo);
            if (vysledek.isBoolean()) {
                return vysledek.asBoolean();
            }
        }
        // Výchozí bezpečnostní chování, pokud plugin mlčí
        return true;
    }

    @Override
    public boolean hracChceLiznout(Hrac kdo) {
        if (jsFunkce.hasMember("hracChceLiznout")) {
            Value vysledek = jsFunkce.getMember("hracChceLiznout").execute(hra, kdo);
            if (vysledek.isBoolean()) {
                return vysledek.asBoolean();
            }
        }
        return true;
    }

    @Override
    public void pripravBalicek(Balicek<Karta> balicek) {
        if (jsFunkce.hasMember("ziskejKartyDoBalicku")) {
            
            Value poleJsKaret = jsFunkce.getMember("kartyDoBalicku").execute();
            
            if (poleJsKaret.hasArrayElements()) {

                for (int i = 0; i < poleJsKaret.getArraySize(); i++) {
                    
                    Value jsKartaObjekt = poleJsKaret.getArrayElement(i);
                    Karta novaJavaKarta = Tovarna.vytvorKartuZJs(hra, balicek, jsKartaObjekt);
                    balicek.vratNahoru(novaJavaKarta);
                }
                
                balicek.zamichej();
            }
        } else {
            //Nemá funkci, tak asi nemá karty.
        }
    }

    // --- Metody S defaultní implementací (použijeme HerniPravidla.super) ---
    @Override
    public void pripravBalicekPostav(Stack<Postava> balicekPostav) {
        if (jsFunkce.hasMember("pripravBalicekPostav")) {
            jsFunkce.getMember("pripravBalicekPostav").execute(hra, balicekPostav);
        } else {
            HerniPravidla.super.pripravBalicekPostav(balicekPostav);
        }
    }

    @Override
    public void zacalTah(Hrac komu) {
        if (jsFunkce.hasMember("zacalTah")) {
            jsFunkce.getMember("zacalTah").execute(hra, komu);
        } else {
            HerniPravidla.super.zacalTah(komu);
        }
    }

    @Override
    public void skoncilTah(Hrac komu) {
        if (jsFunkce.hasMember("skoncilTah")) {
            jsFunkce.getMember("skoncilTah").execute(hra, komu);
        } else {
            HerniPravidla.super.skoncilTah(komu);
        }
    }

    @Override
    public boolean muzeSpalit(Karta co) {
        if (jsFunkce.hasMember("muzeSpalit")) {
            Value vysledek = jsFunkce.getMember("muzeSpalit").execute(hra, co);
            if (vysledek.isBoolean()) {
                return vysledek.asBoolean();
            }
        }
        return HerniPravidla.super.muzeSpalit(co);
    }

    @Override
    public UIPrvek[] getViditelnePrvky() {
        if (jsFunkce.hasMember("getViditelnePrvky")) {
            // Tohle může být v budoucnu trochu složitější na konverzi z JS pole 
            // do Java UIPrvek[], proto tu zkusíme rovnou použít asHostObject()
            Value vysledek = jsFunkce.getMember("getViditelnePrvky").execute(hra);
            if (vysledek.hasArrayElements()) {
                return vysledek.as(UIPrvek[].class);
            }
        }
        return HerniPravidla.super.getViditelnePrvky();
    }

    @Override
    public boolean muzeZahrat(Karta co, Hrac kdo) {
        if (jsFunkce.hasMember("muzeZahrat")) {
            Value vysledek = jsFunkce.getMember("muzeZahrat").execute(hra, co, kdo);
            if (vysledek.isBoolean()) {
                return vysledek.asBoolean();
            }
        }
        return HerniPravidla.super.muzeZahrat(co, kdo);
    }

    @Override
    public String getVychoziZadniObrazek() {
        if (jsFunkce.hasMember("getVychoziZadniObrazek")) {
            Value vysledek = jsFunkce.getMember("getVychoziZadniObrazek").execute(hra);
            if (vysledek.isString()) {
                return vysledek.asString();
            }
        }
        return HerniPravidla.super.getVychoziZadniObrazek();
    }

    @Override
    public boolean muzeVylozit(Hrac kdo, VylozitelnaKarta co) {
        if (jsFunkce.hasMember("muzeVylozit")) {
            Value vysledek = jsFunkce.getMember("muzeVylozit").execute(hra, kdo, co);
            if (vysledek.isBoolean()) {
                return vysledek.asBoolean();
            }
        }
        return HerniPravidla.super.muzeVylozit(kdo, co);
    }

    @Override
    public void spustitPrvniTah(SpravceTahu spravceTahu) {
        if (jsFunkce.hasMember("spustitPrvniTah")) {
            jsFunkce.getMember("spustitPrvniTah").execute(hra, spravceTahu);
        } else {
            HerniPravidla.super.spustitPrvniTah(spravceTahu);
        }
    }

    @Override
    public void uiButtonClicked(Hrac hrac, int uiId) {
        if (jsFunkce.hasMember("uiButtonClicked")) {
            jsFunkce.getMember("uiButtonClicked").execute(hra, hrac, uiId);
        } else {
            HerniPravidla.super.uiButtonClicked(hrac, uiId);
        }
    }

    // --- Úklid paměti ---
    public void znicPravidla() {
        // TODO: zavolat
        if (this.graalContext != null) {
            this.graalContext.close();
        }
    }
}
