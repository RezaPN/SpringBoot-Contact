package com.adira.contact.security.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.adira.contact.entity.User;
import com.adira.contact.security.AuthenticatedUserDetails;
import com.adira.contact.service.UserService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private final UserService userServiceImpl;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName(); // Get the email from the Authentication object
        User user = userServiceImpl.getUserByEmail(email).orElse(null);

        if (user == null || !bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("You provided an incorrect email or password.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("userId", user.getId());
        additionalClaims.put("email", user.getEmail());

        AuthenticatedUserDetails userDetails = new AuthenticatedUserDetails(user.getId(), user.getEmail(), authorities);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                user.getPassword(),
                authorities);
    }
}
