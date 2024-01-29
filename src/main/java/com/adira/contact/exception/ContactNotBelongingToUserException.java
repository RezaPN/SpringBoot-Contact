package com.adira.contact.exception;

public class ContactNotBelongingToUserException extends RuntimeException {

    public ContactNotBelongingToUserException() {
        super("Contact does not belong to the current user");
    }

    public ContactNotBelongingToUserException(String message) {
        super(message);
    }

    public ContactNotBelongingToUserException(String message, Throwable cause) {
        super(message, cause);
    }
}