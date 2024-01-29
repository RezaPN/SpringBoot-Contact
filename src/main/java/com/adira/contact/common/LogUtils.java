package com.adira.contact.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.MDC;

public class LogUtils {

    private final Logger logger;

    public LogUtils(Logger logger) {
        this.logger = logger;
    }

    public void logInfoWithTraceId(String message) {
        logWithTraceId("INFO", message);
    }

    private void logWithTraceId(String level, String message) {
        String traceId = MDC.get("traceId");

        switch (level) {
            case "DEBUG":
                logger.debug("{} {} - {} TraceId: {}", getCurrentTimestamp(), level, message, traceId);
                break;
            case "WARN":
                logger.warn("{} {} - {} TraceId: {}", getCurrentTimestamp(), level, message, traceId);
                break;
            case "INFO":
                logger.info("{} {} - {} TraceId: {}", getCurrentTimestamp(), level, message, traceId);
                break;
            default:
                logger.info("{} {} - {} TraceId: {}", getCurrentTimestamp(), level, message, traceId);
        }
    }

    private String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
        return LocalDateTime.now().format(formatter);
    }

    public void logDebugWithTraceId(String message) {
        logWithTraceId("DEBUG", message);
    }

    public void logWarnWithTraceId(String message) {
        logWithTraceId("WARN", message);
    }

    public void logErrorWithTraceId(String message, Throwable throwable) {
        logWithTraceId("ERROR", message, throwable);
    }

    private void logWithTraceId(String level, String message, Throwable throwable) {
        String traceId = MDC.get("traceId");
        logger.error("{} {} - {} , TraceId: {}", getCurrentTimestamp(), level, message, traceId, throwable);
    }
}
