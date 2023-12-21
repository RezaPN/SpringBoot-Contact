package com.adira.contact.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.adira.contact.security.filter.AuthenticationFilter;
import com.adira.contact.security.filter.ExceptionHandlerFilter;
import com.adira.contact.security.filter.JWTAuthorizationFilter;
import com.adira.contact.security.manager.CustomAuthenticationManager;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration extends WebSecurityConfiguration {

        private final CustomAuthenticationManager customAuthenticationManager;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager);
                authenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");

                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                                                .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**")
                                                .hasAnyAuthority("ROLE_ADMIN")
                                                .anyRequest().authenticated())
                                .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
                                .addFilter(authenticationFilter)
                                .addFilterAfter(new JWTAuthorizationFilter(), AuthenticationFilter.class)
                                .sessionManagement(
                                                sessionManagement -> sessionManagement.sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS));

                return http.build();

        }
}
