package backend.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central place for creating SLF4J loggers and accessing request-scoped MDC values.
 *
 * Uses Spring Boot's native logging stack (SLF4J + Logback via spring-boot-starter-*).
 */
public final class LogUtil {
    private LogUtil() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static String requestId() {
        return MdcUtil.get(MdcUtil.REQUEST_ID_KEY);
    }
}

