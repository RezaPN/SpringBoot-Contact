// ContactService.java
package com.adira.contact.service;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.adira.contact.dto.RequestBody.ContactRequestDTO;
import com.adira.contact.dto.ResponseBody.ContactUpdateDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.Contact;

public interface ContactService {

    List<Contact> getAllContacts();

    Contact getContactById(Long id);

    List<Contact> getContactsByUserId(Long userId);

    Contact createContact(ContactRequestDTO contact, BindingResult bindingResult);

    void deleteContact( Long id);

    boolean doesContactExistById(Long id);

    Contact updateContact(Long id, ContactUpdateDTO contactUpdate) throws BadRequestException;

    ResponseEntity<ApiResponse<List<Contact>>> findBySearchCriteria(String bankName, String accountNumber, String contactName);
}
