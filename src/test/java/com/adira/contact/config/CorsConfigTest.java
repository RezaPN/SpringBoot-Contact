package com.adira.contact.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    @Test
    void corsConfigurationSource_shouldReturnExpectedConfiguration() {
        CorsConfig config = new CorsConfig();
        CorsConfigurationSource source = config.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/"); // Create a mock request
        CorsConfiguration configuration = source.getCorsConfiguration(request); // Pass the request

        assertThat(configuration.getAllowedOrigins()).containsExactly("http://localhost:8080");
        assertThat(configuration.getAllowedMethods()).containsExactly("*");
        assertThat(configuration.getAllowedHeaders()).containsExactly("*");
        assertThat(configuration.getAllowCredentials()).isTrue();
    }
}
