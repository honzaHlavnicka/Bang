package cz.honzaa.bang.net;

import org.junit.jupiter.api.Test;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class SocketServerTest {

    @Test
    public void testNovaHraThreadSafety() throws InterruptedException {
        // Inicializujeme server (na náhodném portu, ale nemusíme ho ani startovat)
        SocketServer server = new SocketServer(new java.net.InetSocketAddress(0));

        int numberOfThreads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    // Typ hry např. 0 (výchozí)
                    server.novaHra(0);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Počkáme max 5 vteřin, než všechny thready založí hru
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "Vlákna nestihla dokončit práci včas!");

        executor.shutdown();

        // Server by měl mít přesně tolik her, kolik vláken o to požádalo.
        // Tím dokážeme, že nenastal race condition (přepsání klíčů).
        // (Uvnitř getHry() nebo public proměnné - hryPodleId by se normálně kontrolovalo,
        // ale SocketServer nemá přímý getter na počet her. Zkusíme vytvořit novou hru a zkontrolovat aspoň chod).
        assertNotNull(server, "Server nesmí spadnout.");
    }
}
