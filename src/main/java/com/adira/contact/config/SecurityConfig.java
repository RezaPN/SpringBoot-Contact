package com.adira.contact.config;
// package com.adira.contact.controller;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig extends WebSecurityConfiguration {

//   @Override
//   protected void configure(HttpSecurity http) throws Exception {
//     http
//       .authorizeHttpRequests()
//       .requestMatchers("/public/**").permitAll()
//       .anyRequest().authenticated()
//       .and()
//       .formLogin()
//       .loginPage("/login")
//       .permitAll()
//       .and()
//       .logout()
//       .permitAll();

//     // Disable Spring Security temporarily
//     http.disable();
//   }
// }
