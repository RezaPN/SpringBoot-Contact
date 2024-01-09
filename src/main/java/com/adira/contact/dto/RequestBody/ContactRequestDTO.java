package com.adira.contact.dto.RequestBody;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ContactRequestDTO {

    @NotNull(message = "Account Number should not be Null")
    @NotEmpty(message = "Account Number should not be Empty")
    private String accountNumber;
    @NotNull(message = "Bank Name should not be Null")
    @NotEmpty(message = "Bank Name should not be Empty")
    private String bankName;
    @NotNull(message = "Contact Name should not be Null")
    @NotEmpty(message = "Contact Name should not be Empty")
    private String contactName;

    // Constructors, getters, and setters

    // Constructors
    public ContactRequestDTO() {
    }

    public ContactRequestDTO(String accountNumber, String bankName, String contactName) {
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
