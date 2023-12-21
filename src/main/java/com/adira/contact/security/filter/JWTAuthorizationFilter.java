package com.adira.contact.security.filter;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.adira.contact.security.SecurityConstants;
import com.adira.contact.security.jwt.RSAKeyReader;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith(SecurityConstants.BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.replace(SecurityConstants.BEARER, "");

            System.out.println("Get Public Key");
            PublicKey publicKey = RSAKeyReader.getPublicKeyFromFile("public_key.pem");
            System.out.println(publicKey);

            Claims user = Jwts.parser()
                    .verifyWith(publicKey)
                    .build().parseSignedClaims(token).getPayload();

            System.out.println("TEST");
            System.out.println(user);

            List<String> authorityStrings = user.get("authorities", List.class);

            List<GrantedAuthority> authorities = authorityStrings.stream()
                    .map(SimpleGrantedAuthority::new) // Mengubah string nama peran menjadi GrantedAuthority
                    .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getSubject(), null,
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (TokenExpiredException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired");
        } catch (JWTVerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }

    }
}
