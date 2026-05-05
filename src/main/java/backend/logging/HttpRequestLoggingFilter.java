package backend.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter that logs HTTP request and response details including:
 * - Request method, URI, query parameters
 * - Request headers
 * - Request body (for non-multipart requests)
 * - Response status code
 * - Response headers
 * - Response body
 * - Request processing time
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);
    private static final int MAX_PAYLOAD_LENGTH = 10000;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // Wrap request and response to cache content for logging
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Log request details
        logRequest(requestWrapper);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            // Log response details
            logResponse(responseWrapper, duration);
            
            // Copy cached response content back to the actual response
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        StringBuilder requestLog = new StringBuilder();
        requestLog.append("\n========== HTTP REQUEST ==========\n");
        requestLog.append(String.format("%s %s", request.getMethod(), request.getRequestURI()));
        
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestLog.append("?").append(queryString);
        }
        requestLog.append("\n");

        // Log headers
        Map<String, String> headers = getRequestHeaders(request);
        if (!headers.isEmpty()) {
            requestLog.append("Headers:\n");
            headers.forEach((key, value) -> {
                // Mask sensitive headers
                if (key.equalsIgnoreCase("Authorization") || key.equalsIgnoreCase("Cookie")) {
                    requestLog.append(String.format("  %s: [REDACTED]\n", key));
                } else {
                    requestLog.append(String.format("  %s: %s\n", key, value));
                }
            });
        }

        // Log request body (if present and not multipart)
        String contentType = request.getContentType();
        if (contentType != null && !contentType.startsWith("multipart/")) {
            String payload = getRequestPayload(request);
            if (payload != null && !payload.isEmpty()) {
                requestLog.append("Body:\n");
                requestLog.append(payload).append("\n");
            }
        } else if (contentType != null && contentType.startsWith("multipart/")) {
            requestLog.append("Body: [MULTIPART DATA]\n");
        }

        requestLog.append("==================================");
        log.info(requestLog.toString());
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        StringBuilder responseLog = new StringBuilder();
        responseLog.append("\n========== HTTP RESPONSE ==========\n");
        responseLog.append(String.format("Status: %d\n", response.getStatus()));
        responseLog.append(String.format("Duration: %d ms\n", duration));

        // Log response headers
        Map<String, String> headers = getResponseHeaders(response);
        if (!headers.isEmpty()) {
            responseLog.append("Headers:\n");
            headers.forEach((key, value) -> 
                responseLog.append(String.format("  %s: %s\n", key, value))
            );
        }

        // Log response body
        String payload = getResponsePayload(response);
        if (payload != null && !payload.isEmpty()) {
            responseLog.append("Body:\n");
            responseLog.append(payload).append("\n");
        }

        responseLog.append("===================================");
        log.info(responseLog.toString());
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                headers.put(headerName, headerValue);
            }
        }
        
        return headers;
    }

    private Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        
        for (String headerName : response.getHeaderNames()) {
            String headerValue = response.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        
        return headers;
    }

    private String getRequestPayload(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                int length = Math.min(buf.length, MAX_PAYLOAD_LENGTH);
                String payload = new String(buf, 0, length, request.getCharacterEncoding());
                if (buf.length > MAX_PAYLOAD_LENGTH) {
                    payload += "\n... [TRUNCATED]";
                }
                return payload;
            } catch (UnsupportedEncodingException e) {
                return "[UNABLE TO DECODE]";
            }
        }
        return null;
    }

    private String getResponsePayload(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                int length = Math.min(buf.length, MAX_PAYLOAD_LENGTH);
                String payload = new String(buf, 0, length, response.getCharacterEncoding());
                if (buf.length > MAX_PAYLOAD_LENGTH) {
                    payload += "\n... [TRUNCATED]";
                }
                return payload;
            } catch (UnsupportedEncodingException e) {
                return "[UNABLE TO DECODE]";
            }
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip logging for static resources and actuator endpoints
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || 
               path.startsWith("/static") ||
               path.startsWith("/webjars") ||
               path.endsWith(".css") ||
               path.endsWith(".js") ||
               path.endsWith(".ico");
    }
}
