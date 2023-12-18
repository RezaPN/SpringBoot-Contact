package com.adira.contact.dto;

import jakarta.validation.constraints.NotNull;

public class ContactRequestDTO {

    @NotNull(message = "Account Number should not be Empty")
    private String accountNumber;
    @NotNull(message = "Bank Name should not be Empty")
    private String bankName;
    @NotNull(message = "Contact Name should not be Empty")
    private String contactName;
    @NotNull(message = "User ID should not be empty")
    private Long userId;

    // Constructors, getters, and setters

    // Constructors
    public ContactRequestDTO() {
    }

    public ContactRequestDTO(String accountNumber, String bankName, String contactName, Long userId) {
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.contactName = contactName;
        this.userId = userId;
    }

    // Getters and setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
