package com.adira.contact.controller;

import com.adira.contact.common.Utils;
import com.adira.contact.dto.ContactRequestDTO;
import com.adira.contact.dto.ContactUpdateDTO;
import com.adira.contact.pojo.ApiResponse;
import com.adira.contact.pojo.Contact;
import com.adira.contact.pojo.User;
import com.adira.contact.service.ContactService;
import com.adira.contact.service.UserService;

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

    @Autowired
    private UserService userService;

    private Utils utils = new Utils();

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

        if (bindingResult.hasErrors()) {
            return utils.handleValidationErrors(bindingResult);
        }

        Long userId = contactRequest.getUserId();

        Optional<User> user = userService.getUserById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, "User not found", "API Contact Service", null));
        }

        Contact contact = new Contact(contactRequest.getAccountNumber(), contactRequest.getBankName(),
                contactRequest.getContactName(), user.get());

        Contact contactCreated = contactService.createContact(contact);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Contact Created", "API Contact Service", contactCreated));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<Contact>> updateContact(@PathVariable Long id,
            @RequestBody ContactUpdateDTO contactUpdate) {

        Optional<User> user = userService.getUserById(id);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, "User not found", "API Contact Service", null));
        }

        Contact contact = contactService.getContactById(id);

        if (contactUpdate.getAccountNumber() != null) {
            contact.setAccountNumber(contactUpdate.getAccountNumber());
        }

        if (contactUpdate.getBankName() != null) {
            contact.setBankName(contactUpdate.getBankName());
        }

        if (contactUpdate.getContactName() != null) {
            contact.setContactName(contactUpdate.getContactName());
        }

        Contact updatedContact = contactService.updateContact(id, contact);
        return updatedContact != null
                ? ResponseEntity
                        .ok(new ApiResponse<>(200, "Contact " + id + " Updated", "API Contact Service", updatedContact))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Failed", "API Contact Service", null));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id) {
        if (!contactService.doesContactExistById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Contact Not Found", "API Contact Service", null));
        }
    
        boolean deleted = contactService.deleteContact(id);
    
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Failed to delete contact", "API Contact Service", null));
        }
    }
    
}
