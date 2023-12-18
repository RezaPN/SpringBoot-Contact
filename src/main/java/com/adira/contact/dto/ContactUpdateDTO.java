package com.adira.contact.dto;

public class ContactUpdateDTO {

    private String accountNumber;
    private String bankName;
    private String contactName;

    // Constructors, getters, and setters

    // Constructors
    public ContactUpdateDTO() {
    }

    public ContactUpdateDTO(String accountNumber, String bankName, String contactName) {
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.contactName = contactName;
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
}
