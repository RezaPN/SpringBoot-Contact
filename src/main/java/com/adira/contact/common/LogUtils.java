package com.adira.contact.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogUtils {

    private static final Logger logger = LoggerFactory.getLogger(LogUtils.class);

    public static void logInfoWithTraceId(String message) {
        logWithTraceId("INFO", message);
    }

    public static void logDebugWithTraceId(String message) {
        logWithTraceId("DEBUG", message);
    }

    public static void logWarnWithTraceId(String message) {
        logWithTraceId("WARN", message);
    }

    public static void logErrorWithTraceId(String message, Throwable throwable) {
        logWithTraceId("ERROR", message, throwable);
    }

    private static void logWithTraceId(String level, String message) {
        String traceId = MDC.get("traceId");
        logger.info("{} {} - {} TraceId: {}", getCurrentTimestamp(), level, message, traceId);
    }

    private static void logWithTraceId(String level, String message, Throwable throwable) {
        String traceId = MDC.get("traceId");
        logger.error("{} {} - {} , TraceId: {}", getCurrentTimestamp(), level, message, traceId, throwable);
    }

    private static String getCurrentTimestamp() {
        // You can customize the timestamp format as needed
        // Here, we use the default format provided by SLF4J
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

        // Get the current date and format it
        String formattedDate = dateFormat.format(new Date());

        return formattedDate;
    }
}
