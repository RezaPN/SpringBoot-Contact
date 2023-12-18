package com.adira.contact.service;

import java.util.Optional;

import com.adira.contact.pojo.User;

import io.jsonwebtoken.Claims;

public interface AuthenticationService {

    Optional<User> authenticateUser(String email, String password);

    User registerUser(String email, String password, boolean admin);

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    Claims parseToken(String token);
}
