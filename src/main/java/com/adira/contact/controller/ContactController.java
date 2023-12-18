package com.adira.contact.controller;

import com.adira.contact.dto.ContactRequestDTO;
import com.adira.contact.dto.ContactUpdateDTO;
import com.adira.contact.pojo.ApiResponse;
import com.adira.contact.pojo.Contact;
import com.adira.contact.service.ContactService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Contact>>> getAllContacts() {
        List<Contact> contacts = contactService.getAllContacts();
        ApiResponse<List<Contact>> apiResponse = new ApiResponse<>(200, "Success", "API Contact Service", contacts);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<Contact>> getContactById(@PathVariable Long id) {
        return Optional.ofNullable(contactService.getContactById(id))
                .map(contact -> ResponseEntity.ok(new ApiResponse<>(200, "Success", "API Contact Service", contact)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "Not Found", "API Contact Service", null)));
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<ApiResponse<List<Contact>>> getContactsByUserId(@PathVariable Long userId) {
        List<Contact> contacts = contactService.getContactsByUserId(userId);

        if (contacts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Not Found", "API Contact Service", null));
        }

        ApiResponse<List<Contact>> apiResponse = new ApiResponse<>(200, "Success", "API Contact Service", contacts);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createContact(@Valid @RequestBody ContactRequestDTO contactRequest,
            BindingResult bindingResult) {

        return contactService.createContact(contactRequest, bindingResult);
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<Contact>> updateContact(@PathVariable Long id,
            @RequestBody ContactUpdateDTO contactUpdate) {
        return contactService.updateContactAggregate(id, contactUpdate);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id) {
        return contactService.deleteContact(id);
    }

}
