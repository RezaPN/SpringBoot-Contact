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
import com.adira.contact.dto.RequestBody.UserRequestDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.security.AuthenticatedUserDetails;
import com.adira.contact.security.SecurityConstants;
import com.adira.contact.security.jwt.RSAKeyReader;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            UserRequestDTO user = new ObjectMapper().readValue(request.getInputStream(), UserRequestDTO.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),
                    user.getPassword());
            return authenticationManager.authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        ApiResponse<String> apiResponse = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Authentication Failed",
                "Authentication", failed.getMessage());

        sendResponse(response, HttpStatus.UNAUTHORIZED, apiResponse);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authResult) throws IOException, ServletException {

        AuthenticatedUserDetails userDetails = (AuthenticatedUserDetails) authResult.getPrincipal();
        String userId = userDetails.getUserId().toString();
        String email = userDetails.getEmail();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        String[] authorityArray = authorities.stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);

        PrivateKey privateKey = null;
        String accessToken = null;
        String refreshToken = null;
        String idToken = UUID.randomUUID().toString();

        try {
            privateKey = RSAKeyReader.getPrivateKeyFromFile("private_key.pem");
            accessToken = RSAKeyReader.createTokenRS256(userId, email, authorityArray,
                    SecurityConstants.ACCESS_TOKEN_EXPIRATION, privateKey);

            refreshToken = RSAKeyReader.createTokenRS256(userId, email, authorityArray,
                    SecurityConstants.REFRESH_TOKEN_EXPIRATION, privateKey, idToken);
        } catch (Exception e) {
            ApiResponse<AuthToken> apiResponse = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Authentication Failed", "Authentication", null);
            sendResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, apiResponse);
            return;
        }

        if (privateKey != null && accessToken != null && refreshToken != null) {
            AuthToken authToken = new AuthToken(accessToken, refreshToken);
            ApiResponse<AuthToken> apiResponse = new ApiResponse<>(HttpStatus.OK.value(),
                    "Authentication successful", "Authentication", authToken);
            sendResponse(response, HttpStatus.OK, apiResponse);
        }
    }

    private void sendResponse(HttpServletResponse response, HttpStatus httpStatus, ApiResponse<?> apiResponse)
            throws IOException {
        String jsonResponse = new ObjectMapper().writeValueAsString(apiResponse);
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
