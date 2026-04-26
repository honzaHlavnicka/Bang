/*
 * Voice Chat Manager - Spravuje peer-to-peer komunikaci přes PeerJS
 */
package cz.honza.bang.net;

import cz.honza.bang.HracImp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spravuje voice chat pro jednu hru.
 * Udržuje si seznam všech hráčů a jejich PeerJS ID.
 * Když se nový hráč připojí, posle všem ostatním jeho info.
 * Když se hráč odpojí, posle všem zprávu o odpojení.
 * 
 * @author honza
 */
public class VoiceChatManager {
    
    // Mapa hráč -> Jeho PeerJS ID
    private final Map<HracImp, String> hracyPeerIds = new ConcurrentHashMap<>();
    
    // Komunikátor, kterým posíláme zprávy
    private final KomunikatorHryImp komunikator;
    
    public VoiceChatManager(KomunikatorHryImp komunikator) {
        this.komunikator = komunikator;
    }
    
    /**
     * Zaregistruje nového hráče do voice chatu
     * Posle všem ostatním informaci o novém hráči
     * 
     * @param hrac Hráč, který se připojuje
     * @param peerId PeerJS ID hráče
     */
    public void pripojiHrace(HracImp hrac, String peerId) {
        hracyPeerIds.put(hrac, peerId);
        System.out.println("Voice Chat: Hráč " + hrac.getJmeno() + " se připojil s PeerID: " + peerId);
        
        // Posli všem ostatním: "Hey, je tu nový hráč s tímto PeerID"
        posliZpravuONovemHraci(hrac, peerId);
        
        // Pošli novému hráči seznam všech ostatních hráčů (aby se s nimi mohl spojit)
        posliSeznamOstatnichHracu(hrac);
    }
    
    /**
     * Odebere hráče z voice chatu
     * Posle všem ostatním informaci o odpojení
     * 
     * @param hrac Hráč, který se odpojuje
     */
    public void odpojHrace(HracImp hrac) {
        String peerId = hracyPeerIds.remove(hrac);
        if (peerId == null) {
            System.out.println("Voice Chat: Hráč " + hrac.getJmeno() + " nebyl v mapě peer IDs");
            return;
        }
        
        System.out.println("Voice Chat: Hráč " + hrac.getJmeno() + " se odpojil");
        
        // Posli všem ostatním: "Hráč se odpojil"
        posliZpravuOOdpojenimHraci(hrac);
    }
    
    /**
     * Pošle všem ostatním hráčům informaci o novém hráči
     * Zpráva: voicechat:novyHrac:<idHrace>:<peerId>
     */
    private void posliZpravuONovemHraci(HracImp hrac, String peerId) {
        String zprava = "voicechat:novyHrac:" + hrac.getId() + ":" + peerId;
        komunikator.posliVsem(zprava, hrac); // Posli všem KROMĚ tohoto hráče
    }
    
    /**
     * Pošle všem ostatním hráčům informaci o odpojení hráče
     * Zpráva: voicechat:odpojeni:<idHrace>
     */
    private void posliZpravuOOdpojenimHraci(HracImp hrac) {
        String zprava = "voicechat:odpojeni:" + hrac.getId();
        komunikator.posliVsem(zprava);
    }
    
    /**
     * Pošle novému hráči seznam všech ostatních hráčů
     * Aby se s nimi mohl ihned navázat hlasový kontakt
     * 
     * Zpráva: voicechat:seznamPeeru:userId1:peerId1|userId2:peerId2|...
     */
    private void posliSeznamOstatnichHracu(HracImp noveHrac) {
        StringBuilder sb = new StringBuilder("voicechat:seznamPeeru:");
        
        boolean prvni = true;
        for (Map.Entry<HracImp, String> entry : hracyPeerIds.entrySet()) {
            HracImp hrac = entry.getKey();
            String peerId = entry.getValue();
            
            // Přeskočíme sám nového hráče
            if (hrac.equals(noveHrac)) {
                continue;
            }
            
            if (!prvni) {
                sb.append("|");
            }
            sb.append(hrac.getId()).append(":").append(peerId);
            prvni = false;
        }
        
        komunikator.posli(noveHrac, sb.toString());
    }
    
    /**
     * Vrátí PeerJS ID hráče
     */
    public String getPeerId(HracImp hrac) {
        return hracyPeerIds.get(hrac);
    }
    
    /**
     * Kontroluje, jestli je hráč ve voice chatu
     */
    public boolean jePripojenHrac(HracImp hrac) {
        return hracyPeerIds.containsKey(hrac);
    }
    
    /**
     * Vrátí počet připojených hráčů ve voice chatu
     */
    public int getPocetPripojenychHracu() {
        return hracyPeerIds.size();
    }
    
    /**
     * Vymaže všechna data (např. když se hra skončí)
     */
    public void vycisti() {
        hracyPeerIds.clear();
    }
    
    /**
     * Vrátí kopii mapy peer IDs (pro debug)
     */
    public Map<String, String> getDebugInfo() {
        Map<String, String> debug = new HashMap<>();
        for (Map.Entry<HracImp, String> entry : hracyPeerIds.entrySet()) {
            debug.put(entry.getKey().getJmeno(), entry.getValue());
        }
        return debug;
    }
}
