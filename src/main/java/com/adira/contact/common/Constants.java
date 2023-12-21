package com.adira.contact.common;

public class Constants {

    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("ErrorMessages class should not be instantiated.");
    }

    public static final String USER_ALREADY_EXISTS = "User with this email already exists";
    public static final String USER_CREATION_FAILED = "Failed to create user";

}
