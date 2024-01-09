package com.adira.contact.service;

import com.adira.contact.common.LogUtils;
import com.adira.contact.common.Utils;
import com.adira.contact.dto.ContactRequestDTO;
import com.adira.contact.dto.ContactUpdateDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.Contact;
import com.adira.contact.entity.User;
import com.adira.contact.exception.CustomNotFoundException;
import com.adira.contact.repository.ContactRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final UserService userService;

    public ContactServiceImpl(ContactRepository contactRepository, UserService userService) {
        this.contactRepository = contactRepository;
        this.userService = userService;
    }

    private final Utils utils = new Utils();

    @Override
    public List<Contact> getAllContacts() {
        Long userId = getUserIdFromAuthentication();
        return contactRepository.findByUserId(userId);
    }

    @Override
    public Contact getContactById(Long id) {
        Long userId = getUserIdFromAuthentication();
        User user = getUserById(userId);
        return contactRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CustomNotFoundException("Contact with id " + id + " not found"));
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong((String) authentication.getPrincipal());

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
    public void deleteContact(Long id) {
        LogUtils.logInfoWithTraceId("Checking does contact Exist" + id);
        if (!doesContactExistById(id)) {
            LogUtils.logErrorWithTraceId("Contact " + id + " doesn't exist",
                    new EmptyResultDataAccessException("Contact Not Found", 0, null));
            throw new EmptyResultDataAccessException("Contact Not Found", 0, null);
        }
        LogUtils.logInfoWithTraceId("Contact exist" + id);
        // Directly delete the contact by ID
        contactRepository.deleteById(id);

    }

    @Override
    public boolean doesContactExistById(Long id) {
        return contactRepository.existsById(id);
    }

    private User getUserFromJWT() {
        return getUserById(getUserIdFromAuthentication());
    }

    private Long getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong((String) authentication.getPrincipal());
    }

    private User getUserById(Long userId) {
        return userService.getUserById(userId)
                .orElseThrow(() -> new CustomNotFoundException("User with id " + userId + " not found"));
    }

    public ResponseEntity<ApiResponse<Contact>> updateContact(Long id, ContactUpdateDTO contactUpdate) {

        Optional<Contact> contactOptional = contactRepository.findById(id);

        if (!contactOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Contact Not Found", "API Contact Service", null));
        }

        Contact contact = contactOptional.get();

        User user = getUserFromJWT();

        if (contact.getUser().getId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "Contact not Belonging To User", "API Contact Service", null));
        }

        if (contactUpdate.getAccountNumber() != null) {
            contact.setAccountNumber(contactUpdate.getAccountNumber());
        }

        if (contactUpdate.getBankName() != null) {
            contact.setBankName(contactUpdate.getBankName());
        }

        if (contactUpdate.getContactName() != null) {
            contact.setContactName(contactUpdate.getContactName());
        }

        Contact updatedContact = contactRepository.save(contact);

        return updatedContact != null
                ? ResponseEntity
                        .ok(new ApiResponse<>(200, "Contact " + id + " Updated", "API Contact Service", updatedContact))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Failed", "API Contact Service", null));
    }

    @Override
    public ResponseEntity<ApiResponse<List<Contact>>> findBySearchCriteria(String bankName, String accountNumber,
            String contactName) {

        List<Contact> contacts = contactRepository.findByCriteria(getUserFromJWT().getId(), bankName, accountNumber,
                contactName);

        if (contacts.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Contact Not Found", "API Contact Service", null));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(200, "Contact Found", "API Contact Service", contacts));
    }
}
