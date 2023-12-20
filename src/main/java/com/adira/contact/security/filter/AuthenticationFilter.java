package com.adira.contact.security.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.adira.contact.dto.AuthToken;
import com.adira.contact.dto.UserRequestDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.User;
import com.adira.contact.security.SecurityConstants;
import com.adira.contact.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
            UserRequestDTO user = new ObjectMapper().readValue(request.getInputStream(), UserRequestDTO.class);
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
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        // UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        // User user = userServiceImpl.getUserByEmail(userDetails.getUsername())
        // .orElseThrow(() -> new RuntimeException("User not found"));

        // boolean isAdmin = user.getAdmin();
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();

        String[] authorityArray = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);

        String token = JWT.create()
                .withSubject(authResult.getName())
                .withArrayClaim("authorities", authorityArray)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.ACCESS_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY));

        String idToken = UUID.randomUUID().toString();

        String refreshToken = JWT.create()
                .withSubject(authResult.getName())
                .withArrayClaim("authorities", authorityArray)
                .withClaim("idToken", idToken) // Use UUID for idToken
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.REFRESH_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY));

        AuthToken authToken = new AuthToken(token, refreshToken);

        ApiResponse<AuthToken> apiResponse = new ApiResponse<>(200, "Authentication successful", "Authentication",
                authToken);
        String jsonResponse = new ObjectMapper().writeValueAsString(apiResponse);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.addHeader(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);
        response.getWriter().write(jsonResponse);

    }

}
