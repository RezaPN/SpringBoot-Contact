package com.adira.contact.dto.ResponseBody;

public class ContactDTO {
    private String account_number;
    private String bank_name;
    private String contact_name;


    public ContactDTO() {
    }


    public ContactDTO(String account_number, String bank_name, String contact_name) {
        this.account_number = account_number;
        this.bank_name = bank_name;
        this.contact_name = contact_name;
    }


    public String getAccount_number() {
        return this.account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getBank_name() {
        return this.bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getContact_name() {
        return this.contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

}
