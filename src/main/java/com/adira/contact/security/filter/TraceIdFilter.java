package com.adira.contact.security.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.adira.contact.common.LogUtils;
import com.adira.contact.controller.ContactController;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceIdFilter.class);
    LogUtils logUtils = new LogUtils(LOGGER);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

  

        try {
            String traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);

            logUtils.logInfoWithTraceId("START - " + request.getMethod() + " " + request.getRequestURI());

            // Continue with the filter chain
            filterChain.doFilter(request, response);

            logUtils.logInfoWithTraceId("END - " + request.getMethod() + " " + request.getRequestURI() + ", Status: "
                    + response.getStatus());

        } finally {
            // Clean up MDC after processing the request
            MDC.remove("traceId");
        }
    }

}
