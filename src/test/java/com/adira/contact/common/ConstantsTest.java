package com.adira.contact.common;



import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ConstantsTest {

    @Test
    public void testUserAlreadyExistsMessage() {
        assertEquals("User with this email already exists", Constants.USER_ALREADY_EXISTS);
    }

    @Test
    public void testUserCreationFailedMessage() {
        assertEquals("Failed to create user", Constants.USER_CREATION_FAILED);
    }
}
