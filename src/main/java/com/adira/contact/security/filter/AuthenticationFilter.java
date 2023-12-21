package com.adira.contact.security.filter;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Collection;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.adira.contact.dto.AuthToken;
import com.adira.contact.dto.UserRequestDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.security.AuthenticatedUserDetails;
import com.adira.contact.security.SecurityConstants;
import com.adira.contact.security.jwt.RSAKeyReader;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

        private AuthenticationManager authenticationManager;

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
                        throws AuthenticationException {
                try {
                        UserRequestDTO user = new ObjectMapper().readValue(request.getInputStream(),
                                        UserRequestDTO.class);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),
                                        user.getPassword());
                        return authenticationManager.authenticate(authentication);
                } catch (IOException e) {
                        throw new RuntimeException();
                }
        }

        @Override
        protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException failed) throws IOException, ServletException {

                ApiResponse<String> apiResponse = new ApiResponse<>(401, "Authentication Failed", "Authentication",
                                failed.getMessage());

                String jsonResponse = new ObjectMapper().writeValueAsString(apiResponse);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonResponse);
                response.getWriter().flush();
        }

        @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                        FilterChain chain, Authentication authResult) throws IOException, ServletException {

                AuthenticatedUserDetails userDetails = (AuthenticatedUserDetails) authResult.getPrincipal();
                String userId = userDetails.getUserId().toString();
                String email = userDetails.getEmail();

                Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
                String[] authorityArray = authorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .toArray(String[]::new);

                PrivateKey privateKey = null;
                String accessToken = null;
                String refreshToken = null;
                   String idToken = UUID.randomUUID().toString();

                try {
                        privateKey = RSAKeyReader.getPrivateKeyFromFile("private_key.pem");
                        accessToken = RSAKeyReader.createTokenRS256(userId, email, authorityArray,
                                        SecurityConstants.ACCESS_TOKEN_EXPIRATION, privateKey);

                        refreshToken = RSAKeyReader.createTokenRS256(userId, email, authorityArray, SecurityConstants.REFRESH_TOKEN_EXPIRATION, privateKey, idToken);
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        System.out.println("Failed...");

                        System.out.println(e);
                }
                AuthToken authToken = new AuthToken(accessToken, refreshToken);

                ApiResponse<AuthToken> apiResponse = new ApiResponse<>(HttpStatus.OK.value(),
                                "Authentication successful",
                                "Authentication", authToken);
                String jsonResponse = new ObjectMapper().writeValueAsString(apiResponse);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonResponse);
        }


}
