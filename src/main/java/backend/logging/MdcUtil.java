package backend.logging;

import org.slf4j.MDC;

/**
 * Simple MDC helper so request-scoped identifiers show up consistently in logs.
 */
public final class MdcUtil {
    private MdcUtil() {
    }

    public static final String REQUEST_ID_KEY = "requestId";

    public static void put(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        MDC.put(key, value);
    }

    public static String get(String key) {
        return MDC.get(key);
    }

    public static void remove(String key) {
        if (key == null) {
            return;
        }
        MDC.remove(key);
    }
}

