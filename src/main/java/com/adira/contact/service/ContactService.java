// ContactService.java
package com.adira.contact.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import com.adira.contact.dto.ContactRequestDTO;
import com.adira.contact.dto.ContactUpdateDTO;
import com.adira.contact.pojo.ApiResponse;
import com.adira.contact.pojo.Contact;

public interface ContactService {

    List<Contact> getAllContacts();

    Contact getContactById(Long id);

    List<Contact> getContactsByUserId(Long userId);

    ResponseEntity<ApiResponse<?>> createContact(ContactRequestDTO contact, BindingResult bindingResult);

    Contact updateContact(Long id, Contact contact);

    ResponseEntity<?> deleteContact( Long id);

    boolean doesContactExistById(Long id);

    ResponseEntity<ApiResponse<Contact>> updateContactAggregate(Long id, ContactUpdateDTO contactUpdate);
}
