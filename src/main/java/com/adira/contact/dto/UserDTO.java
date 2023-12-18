package com.adira.contact.dto;

public class UserDTO {
    private Long id;
    private String email;
    private boolean admin;

    // Constructors, getters, and setters

    public UserDTO(Long id, String email, boolean admin) {
        this.id = id;
        this.email = email;
        this.admin = admin;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return admin;
    }
}
