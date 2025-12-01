/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template

Toto je domácí verze souborů z programování.
 */
package cz.honza.bang.sdk;

/**
 *
 * @author honza
 */
public abstract class Karta {

    protected Hra hra;
    protected Balicek<Karta> balicek;
    static private int nextId = 0;
    private final int id;

    public Karta(Hra hra, Balicek<Karta> balicek) {
        this.hra = hra;
        this.balicek = balicek;
        id = nextId;
        nextId++;
    }

    public final int getId() {
        return id;
    }

    /**
     * Vrací informace o kratě ve formátu json
     *
     * @return json ve formátu:
     * novaKarta:{"jmeno":jmeno,"obrazek":obrazek,"id":id}
     */
    @Deprecated
    public String toJSONold() {
        StringBuilder sb = new StringBuilder("novaKarta:{\"jmeno\":\"");
        sb.append(this.getJmeno());
        sb.append("\",\"obrazek\":\"");
        sb.append(this.getObrazek());
        sb.append("\",\"id\":");
        sb.append(id);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Vrací informace o kratě ve formátu json
     *
     * @return json ve formátu: {"jmeno":jmeno,"obrazek":obrazek,"id":id}
     */
    public String toJSON() {
        StringBuilder sb = new StringBuilder("{\"jmeno\":\"");
        sb.append(this.getJmeno());
        sb.append("\",\"obrazek\":\"");
        sb.append(this.getObrazek());
        sb.append("\",\"id\":");
        sb.append(id);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Akce, které by se měli provést před spálením.
     */
    public void predSpalenim() {
        //Obecně karta nic dělat nemusí
    }

    public abstract String getObrazek();

    public abstract String getJmeno();

    public String getZadniObrazek() {
        return hra.getHerniPravidla().getVychoziZadniObrazek();
    }
}
