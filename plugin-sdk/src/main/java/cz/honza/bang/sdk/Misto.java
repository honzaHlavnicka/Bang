package cz.honza.bang.sdk;

/**
 * Místo, které se ukazuje hráčům. Můlže to být UI prvek, nebo Hráč.
 * Tento interface by se neměl impolementovat v pluginu, jelikož by klient
 * pravděpodobně neznal identifikator.
 * 
 * Rozdělování identifikátorů funguje následovně:
 * <table><tbody>
 * <tr><td> Hráč# </td> <td>Hráč, kde # je jeho id</td></tr>
 * <tr><td> Hráč#vyložené </td> <td>vyložené karty hráče, kde # je jeho id</td></tr>
 * <tr><td> ODHAZOVACI_BALICEK </td> <td>Identifikátor prvku, braný z UIPrvek</td></tr>
 * </tbody></table>
 * @author honza
 */
public interface Misto {
    String getIdentifikatorMista();
}
