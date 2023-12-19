package com.adira.contact.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("account_number")
    @Column
    private String accountNumber;

    @JsonProperty("bank_name")
    @Column
    private String bankName;

    @JsonProperty("contact_name")
    @Column
  
    private String contactName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
    

    public Contact() {
    }

    public Contact(String accountNumber, String bankName, String contactName, User user) {
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.contactName = contactName;
        this.user = user;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getContactName() {
        return this.contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @PostPersist
    private void logInsert() {
        System.out.println("Inserted contact with id " + this.id);
    }

    @PostUpdate
    private void logUpdate() {
        System.out.println("Update contact with id " + this.id);
    }

    @PostRemove
    private void logRemove() {
        System.out.println("Remove contact with id " + this.id);
    }

}
