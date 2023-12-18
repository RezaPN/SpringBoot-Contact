// ContactController.java
package com.adira.contact.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.adira.contact.pojo.ApiResponse;
import com.adira.contact.pojo.Contact;

import java.util.List;

public interface ContactController {

    @GetMapping("/contacts")
    ResponseEntity<ApiResponse<List<Contact>>> getAllContacts();

    @GetMapping("/contacts/{id}")
    ResponseEntity<Contact> getContactById(@PathVariable Long id);

    @GetMapping("/contacts/user/{userId}")
    ResponseEntity<List<Contact>> getContactsByUserId(@PathVariable Long userId);

    @PostMapping("/contacts")
    ResponseEntity<Contact> createContact(@RequestBody Contact contact);

    @PutMapping("/contacts/{id}")
    ResponseEntity<Contact> updateContact(@PathVariable Long id, @RequestBody Contact contact);

    @DeleteMapping("/contacts/{id}")
    ResponseEntity<Void> deleteContact(@PathVariable Long id);
}
