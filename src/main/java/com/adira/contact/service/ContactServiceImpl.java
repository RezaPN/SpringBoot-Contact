package com.adira.contact.service;

import com.adira.contact.common.LogUtils;
import com.adira.contact.common.Utils;
import com.adira.contact.dto.RequestBody.ContactRequestDTO;
import com.adira.contact.dto.ResponseBody.ContactDTO;
import com.adira.contact.dto.ResponseBody.ContactUpdateDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.Contact;
import com.adira.contact.entity.User;
import com.adira.contact.exception.ContactNotBelongingToUserException;
import com.adira.contact.exception.CustomNotFoundException;
import com.adira.contact.repository.ContactRepository;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactServiceImpl.class);
    LogUtils logUtils = new LogUtils(LOGGER);

    @Override
    public List<Contact> getAllContacts() {
        try {
            logUtils.logInfoWithTraceId("Getting User ID");
            Long userId = getUserIdFromAuthentication();
            logUtils.logInfoWithTraceId("Successfully retrieved User ID: " + userId);

            logUtils.logInfoWithTraceId("Fetching all contacts for User ID from the repository");
            List<Contact> listContact = contactRepository.findByUserId(userId);
            logUtils.logInfoWithTraceId("Successfully fetched contacts for User ID");

            return listContact;
        } catch (Exception e) {
            logUtils.logErrorWithTraceId("Error while fetching contacts", e);
            throw new RuntimeException("Error while fetching contacts", e);
        }
    }

    @Override
    public Contact getContactById(Long id) {
        try {
            logUtils.logInfoWithTraceId("Getting User ID & user");
            Long userId = getUserIdFromAuthentication();
            User user = getUserById(userId);
            logUtils.logInfoWithTraceId("Successfully retrieved User ID & User: " + userId);

            logUtils.logInfoWithTraceId("Fetching contacts from id belonging to user");
            Contact contact = contactRepository.findByIdAndUser(id, user)
                    .orElseThrow(() -> new CustomNotFoundException("Contact with id " + id + " not found"));

                    logUtils.logInfoWithTraceId("Successfully retrieved contact by ID: " + id);
            return contact;
        } catch (CustomNotFoundException e) {
            throw e; // rethrow to controller
        } catch (Exception e) {
            logUtils.logErrorWithTraceId("Error while fetching contacts", e);
            throw new RuntimeException("Error while fetching contacts", e);
        }
    }

    @Override
    public List<Contact> getContactsByUserId(Long userId) {
        return contactRepository.findByUserId(userId);
    }

    @Override
    public Contact createContact(ContactRequestDTO contactRequest, BindingResult bindingResult) {
        try {

            logUtils.logInfoWithTraceId("Get authentication and user id");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong((String) authentication.getPrincipal());

            logUtils.logInfoWithTraceId("checking if user exist");
            Optional<User> user = userService.getUserById(userId);
            if (!user.isPresent()) {
                logUtils.logInfoWithTraceId("user with  this userId is not found");
                throw new CustomNotFoundException("User Not Found");
            }

            logUtils.logInfoWithTraceId("create new contact");
            Contact contact = new Contact(contactRequest.getAccountNumber(), contactRequest.getBankName(),
                    contactRequest.getContactName(), user.get());
            Contact contactCreated = contactRepository.save(contact);
            logUtils.logInfoWithTraceId("contact created");
            // Return success response
            return contactCreated;
        } catch (DataAccessException e) {
            logUtils.logErrorWithTraceId("Get Data Access Exception", e);
            throw e;
        } catch (Exception e) {
            // Handle other general exceptions
            logUtils.logErrorWithTraceId("Get Exception", e);
            throw new RuntimeException("Unexpected error create contact", e);
        }
    }

    @Override
    public void deleteContact(Long id) {
        try {
            logUtils.logInfoWithTraceId("Checking does contact Exist" + id);

            if (!doesContactExistById(id)) {
                logUtils.logErrorWithTraceId("Contact " + id + " doesn't exist",
                        new CustomNotFoundException("Contact Not Found"));
                throw new CustomNotFoundException("Contact Not Found");
            }

            logUtils.logInfoWithTraceId("Contact exist" + id);
            // Directly delete the contact by ID
            contactRepository.deleteById(id);

        } catch (CustomNotFoundException e) {
            logUtils.logErrorWithTraceId("Error deleting contact: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            logUtils.logErrorWithTraceId("Unexpected error deleting contact: " + e.getMessage(), e);
            throw new RuntimeException("Unexpected error deleting contact", e);
        }
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

    public Contact updateContact(Long id, ContactUpdateDTO contactUpdate)
            throws BadRequestException {

        Optional<Contact> contactOptional = contactRepository.findById(id);

        if (!contactOptional.isPresent()) {
            throw new CustomNotFoundException("Contact Not Found");
        }

        Contact contact = contactOptional.get();

        User user = getUserFromJWT();

        if (contact.getUser().getId() != user.getId()) {
            throw new ContactNotBelongingToUserException("Contact not Belonging To User");
        }

        if (utils.isNonEmptyString(contactUpdate.getAccountNumber())
                || utils.isNonEmptyString(contactUpdate.getBankName())
                || utils.isNonEmptyString(contactUpdate.getContactName())) {
            throw new BadRequestException("Fields should not be empty or contain only whitespaces");
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

        return contactRepository.save(contact);

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
