package com.adira.contact.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LogUtilsTest {

    private Logger loggerMock;

    @BeforeEach
    void setUp() {
        // Mock the Logger class statically
        try (MockedStatic<LoggerFactory> loggerFactoryMock = Mockito.mockStatic(LoggerFactory.class)) {
            loggerMock = mock(Logger.class);
            loggerFactoryMock.when(() -> LoggerFactory.getLogger(LogUtils.class)).thenReturn(loggerMock);
        }
    }

    // The test method logInfoWithTraceId uses Mockito to mock the MDC and
    // LoggerFactory classes to control their behavior during the test.
    // It sets up the MDC.get("traceId") method to return a predefined traceId.
    // It sets up the LoggerFactory.getLogger(LogUtils.class) method to return a
    // mocked logger.
    // The method under test (LogUtils.logInfoWithTraceId(message)) is then called.
    // Finally, the test verifies that the info method of the logger was called with
    // the expected parameters.

    @Test
    void logInfoWithTraceId() {
        // Set up MDC mock to return a predefined traceId
        try (MockedStatic<MDC> mdcMock = Mockito.mockStatic(MDC.class)) {
            mdcMock.when(() -> MDC.get("traceId")).thenReturn("12345");

            // Create an instance of LogUtils with the mocked Logger
            LogUtils logUtils = new LogUtils(loggerMock);

            // Call the method under test
            logUtils.logInfoWithTraceId("Test message");

            // Verify that the logger.info method is invoked with the expected arguments
            verify(loggerMock).info(
                    eq("{} {} - {} TraceId: {}"),
                    anyString(),
                    eq("INFO"),
                    eq("Test message"),
                    eq("12345"));
        }
    }

    @Test
    void logDebugWithTraceId() {
        String message = "Test debug message";

        try (MockedStatic<MDC> mdcMock = Mockito.mockStatic(MDC.class)) {
            mdcMock.when(() -> MDC.get("traceId")).thenReturn("12345");

            // Create an instance of LogUtils with the mocked Logger
            LogUtils logUtils = new LogUtils(loggerMock);

            logUtils.logDebugWithTraceId(message);
            // Perubahan disini: Memverifikasi bahwa metode debug dipanggil dengan parameter
            // yang benar
            verify(loggerMock).debug(
                    eq("{} {} - {} TraceId: {}"),
                    anyString(),
                    eq("DEBUG"),
                    eq(message),
                    eq("12345"));
        }
    }

    @Test
    void logWarnWithTraceId() {
        String message = "Test warn message";

        // Call the method to be tested
        try (MockedStatic<MDC> mdcMock = Mockito.mockStatic(MDC.class)) {
            mdcMock.when(() -> MDC.get("traceId")).thenReturn("12345");
            LogUtils logUtils = new LogUtils(loggerMock);
            logUtils.logWarnWithTraceId(message);

            verify(loggerMock).warn(
                    eq("{} {} - {} TraceId: {}"),
                    anyString(),
                    eq("WARN"),
                    eq(message),
                    eq("12345"));
        }
    }

    @Test
    void logErrorWithTraceId() {
        String message = "Test error message";

        Throwable throwable = new RuntimeException("Test exception");

        try (MockedStatic<MDC> mdcMock = Mockito.mockStatic(MDC.class)) {
            mdcMock.when(() -> MDC.get("traceId")).thenReturn("12345");
            LogUtils logUtils = new LogUtils(loggerMock);
            logUtils.logErrorWithTraceId(message, throwable);

            verify(loggerMock).error(
                    eq("{} {} - {} , TraceId: {}"),
                    anyString(),
                    eq("ERROR"),
                    eq(message),
                    eq("12345"),
                    eq(throwable));
        }
    }
}
