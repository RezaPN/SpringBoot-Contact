package com.adira.contact.security;

public class SecurityConstants {
    public static final int REFRESH_TOKEN_EXPIRATION = 7200000; // 7200000 milliseconds = 7200 seconds = 2 hours.
    public static final int ACCESS_TOKEN_EXPIRATION = 600000; // 600000 milliseconds = 600 seconds = 10 minutes.
    public static final String BEARER = "Bearer "; // Authorization : "Bearer " + Token 
    public static final String AUTHORIZATION = "Authorization"; // "Authorization" : Bearer Token
    public static final String REGISTER_PATH = "/api/v1/auth/register"; // Public path that clients can use to register.
}