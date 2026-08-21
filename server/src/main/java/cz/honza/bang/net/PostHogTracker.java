package cz.honza.bang.net;

import com.posthog.server.PostHog;
import com.posthog.server.PostHogConfig;
import com.posthog.server.PostHogInterface;
import com.posthog.server.PostHogCaptureOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostHogTracker {
    private static final Logger logger = LoggerFactory.getLogger(PostHogTracker.class);
    
    private static PostHogInterface posthog;
    private static final String DEFAULT_TOKEN = "phc_AcGQ8b8Kg6GpuhFauLiqKpN3wjD8msdjiHKbMQ6YDpmu";
    private static final String DEFAULT_HOST = "https://szn.bang.honzaa.cz";
    
    public static synchronized void init() {
        if (posthog != null) return;
        
        String token = System.getenv("POSTHOG_TOKEN");
        if (token == null || token.isEmpty()) {
            token = DEFAULT_TOKEN;
        }
        
        String host = System.getenv("POSTHOG_HOST");
        if (host == null || host.isEmpty()) {
            host = DEFAULT_HOST;
        }
        
        try {
            PostHogConfig config = PostHogConfig.builder(token)
                    .host(host)
                    .build();
            posthog = PostHog.with(config);
            logger.info("PostHog tracker úspěšně inicializován s hostem: {}", host);
            // Zaznamenání spuštění serveru
            posthog.capture("server", "server_started");
        } catch (Exception e) {
            logger.error("Chyba při inicializaci PostHog trackeru: {}", e.getMessage(), e);
        }
    }
    
    public static void trackError(String component, String message, Throwable throwable) {
        if (posthog == null) return;
        
        try {
            PostHogCaptureOptions.Builder builder = PostHogCaptureOptions.builder();
            builder.property("component", component);
            builder.property("message", message);
            
            if (throwable != null) {
                // PostHog Error Tracking očekává pole $exception_list
                List<Map<String, Object>> exceptionList = new ArrayList<>();
                Map<String, Object> exceptionObj = new HashMap<>();
                exceptionObj.put("type", throwable.getClass().getName());
                exceptionObj.put("value", throwable.getMessage() != null ? throwable.getMessage() : "");
                
                // Sestavení stacktrace snímků (od nejstaršího po nejnovější vyvolávající)
                Map<String, Object> stacktrace = new HashMap<>();
                List<Map<String, Object>> frames = new ArrayList<>();
                
                StackTraceElement[] elements = throwable.getStackTrace();
                int count = 0;
                for (int i = elements.length - 1; i >= 0; i--) {
                    if (count++ > 50) { // Limit počtu snímků pro zachování rozumné velikosti payloadu
                        break;
                    }
                    StackTraceElement element = elements[i];
                    Map<String, Object> frame = new HashMap<>();
                    frame.put("filename", element.getFileName() != null ? element.getFileName() : "");
                    frame.put("function", element.getMethodName());
                    frame.put("module", element.getClassName());
                    frame.put("lineno", element.getLineNumber() >= 0 ? element.getLineNumber() : 0);
                    // Označení, zda chyba pochází z naší aplikace (pro lepší seskupování v UI)
                    frame.put("in_app", element.getClassName().startsWith("cz.honza.bang"));
                    frames.add(frame);
                }
                
                stacktrace.put("frames", frames);
                exceptionObj.put("stacktrace", stacktrace);
                exceptionList.add(exceptionObj);
                
                builder.property("$exception_list", exceptionList);
                builder.property("$exception_level", "error");
            }
            
            // Odeslání pod vyhrazeným názvem události $exception
            posthog.capture("server", "$exception", builder.build());
        } catch (Exception e) {
            logger.error("Selhalo odeslání chyby do PostHog: {}", e.getMessage());
        }
    }

    public static void trackEvent(String distinctId, String eventName, Map<String, Object> properties) {
        if (posthog == null || "ignore".equals(distinctId)) return;
        try {
            String id = (distinctId != null && !distinctId.isEmpty()) ? distinctId : "server";
            PostHogCaptureOptions.Builder builder = PostHogCaptureOptions.builder();
            if (properties != null) {
                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    builder.property(entry.getKey(), entry.getValue());
                }
            }
            // Přidáme příznak, že to přišlo ze serveru
            builder.property("source", "server");
            
            posthog.capture(id, eventName, builder.build());
        } catch (Exception e) {
            logger.error("Selhalo odeslání události do PostHog: {}", e.getMessage());
        }
    }
    
    public static synchronized void shutdown() {
        if (posthog != null) {
            try {
                posthog.close();
                posthog = null;
                logger.info("PostHog tracker ukončen.");
            } catch (Exception e) {
                logger.error("Chyba při ukončování PostHog trackeru: {}", e.getMessage());
            }
        }
    }
}
