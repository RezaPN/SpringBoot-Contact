package com.adira.contact.service;

import com.adira.contact.common.Utils;
import com.adira.contact.dto.ContactRequestDTO;
import com.adira.contact.dto.ContactUpdateDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.Contact;
import com.adira.contact.entity.User;
import com.adira.contact.exception.CustomNotFoundException;
import com.adira.contact.repository.ContactRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserService userService;

    private Utils utils = new Utils();

    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public Contact getContactById(Long id) {
        return contactRepository.findById(id).orElse(null);
    }

    @Override
    public List<Contact> getContactsByUserId(Long userId) {
        return contactRepository.findByUserId(userId);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> createContact(ContactRequestDTO contactRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return utils.handleValidationErrors(bindingResult);
        }

        Long userId = contactRequest.getUserId();

        Optional<User> user = userService.getUserById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "User not found", "API Contact Service", null));
        }

        Contact contact = new Contact(contactRequest.getAccountNumber(), contactRequest.getBankName(),
                contactRequest.getContactName(), user.get());

        Contact contactCreated = contactRepository.save(contact);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Contact Created", "API Contact Service", contactCreated));
    }

    @Override
    public Contact updateContact(Long id, Contact contact) {
        Optional<Contact> existingContactOptional = contactRepository.findById(id);

        if (existingContactOptional.isPresent()) {
            Contact existingContact = existingContactOptional.get();
            existingContact.setAccountNumber(contact.getAccountNumber());
            existingContact.setBankName(contact.getBankName());
            existingContact.setContactName(contact.getContactName());
            existingContact.setUser(contact.getUser());

            return contactRepository.save(existingContact);
        } else {
            throw new CustomNotFoundException("Contact with id " + id + " not found");
        }
    }

    @Override
    public ResponseEntity<?> deleteContact(Long id) {

        if (!doesContactExistById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Contact Not Found", "API Contact Service", null));
        }

        try {
            contactRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Handle any exceptions or errors during deletion
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Failed to delete contact", "API Contact Service", null));
        }
    }

    @Override
    public boolean doesContactExistById(Long id) {
        return contactRepository.existsById(id);
    }

    public ResponseEntity<ApiResponse<Contact>> updateContactAggregate(Long id, ContactUpdateDTO contactUpdate) {
        Optional<User> user = userService.getUserById(id);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, "User not found", "API Contact Service", null));
        }

        Contact contact = getContactById(id);

        if (contactUpdate.getAccountNumber() != null) {
            contact.setAccountNumber(contactUpdate.getAccountNumber());
        }

        if (contactUpdate.getBankName() != null) {
            contact.setBankName(contactUpdate.getBankName());
        }

        if (contactUpdate.getContactName() != null) {
            contact.setContactName(contactUpdate.getContactName());
        }

        Contact updatedContact = updateContact(id, contact);
        return updatedContact != null
                ? ResponseEntity
                        .ok(new ApiResponse<>(200, "Contact " + id + " Updated", "API Contact Service", updatedContact))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Failed", "API Contact Service", null));
    }
}
