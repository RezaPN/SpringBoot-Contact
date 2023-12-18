// ContactService.java
package com.adira.contact.service;

import java.util.List;

import com.adira.contact.pojo.Contact;

public interface ContactService {

    List<Contact> getAllContacts();

    Contact getContactById(Long id);

    List<Contact> getContactsByUserId(Long userId);

    Contact createContact(Contact contact);

    Contact updateContact(Long id, Contact contact);

    void deleteContact(Long id);
}
