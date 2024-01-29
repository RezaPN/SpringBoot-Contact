package com.adira.contact.controller;

import com.adira.contact.common.LogUtils;
import com.adira.contact.common.Utils;
import com.adira.contact.dto.RequestBody.ContactRequestDTO;
import com.adira.contact.dto.ResponseBody.ContactDTO;
import com.adira.contact.dto.ResponseBody.ContactUpdateDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.Contact;
import com.adira.contact.exception.CustomNotFoundException;
import com.adira.contact.service.ContactService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    Utils utils = new Utils();
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactController.class);
    LogUtils logUtils = new LogUtils(LOGGER);

    @Autowired
    private ContactService contactService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Contact>>> getAllContacts() {
        try {
            List<Contact> contacts = contactService.getAllContacts();
            ApiResponse<List<Contact>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Success",
                    "API Contact Service", contacts);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            logUtils.logErrorWithTraceId(e.getMessage(), e);
            ApiResponse<List<Contact>> errorResponse = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error", "An error occurred while processing the request", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<Contact>> getContactById(@PathVariable Long id) {
        try {
            Contact contact = contactService.getContactById(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "Success", "API Contact Service", contact));
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Not Found", "API Contact Service", null));
        } catch (Exception e) {
            // Handle other types of exceptions as needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Internal Server Error", "API Contact Service", null));
        }
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<ApiResponse<List<Contact>>> getContactsByUserId(@PathVariable Long userId) {
        try {
            List<Contact> contacts = contactService.getContactsByUserId(userId);

            if (contacts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "Not Found", "API Contact Service", null));
            }

            ApiResponse<List<Contact>> apiResponse = new ApiResponse<>(200, "Success", "API Contact Service", contacts);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Internal Server Error", "API Contact Service", null));
        }

    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createContact(@Valid @RequestBody ContactRequestDTO contactRequest,
            BindingResult bindingResult) {
        try {
            logUtils.logInfoWithTraceId("Validation on Binding Result Error");
            if (bindingResult.hasErrors()) {
                logUtils.logInfoWithTraceId("Binding Result has Error");
                return utils.handleValidationErrors(bindingResult);
            }

            Contact contactCreated = contactService.createContact(contactRequest, bindingResult);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(200, "Contact Successfully Created", "API Contact Service",
                            new ContactDTO(contactCreated.getAccountNumber(), contactCreated.getBankName(),
                                    contactCreated.getContactName())));
        } catch (CustomNotFoundException e) {
            logUtils.logErrorWithTraceId("Custom Not Found Exception", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), "API Contact Service", null));
        } catch (DataAccessException e) {
            logUtils.logErrorWithTraceId("Data Access Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, e.getMessage(), "API Contact Service", null));
        } catch (Exception e) {
            logUtils.logErrorWithTraceId("Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Internal Server Error", "API Contact Service", null));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<?>> updateContact(@PathVariable Long id,
            @Valid @RequestBody ContactUpdateDTO contactUpdate) {
        try {
            Contact contactUpdated = contactService.updateContact(id, contactUpdate);
            return contactUpdated != null
                    ? ResponseEntity
                            .ok(new ApiResponse<>(200, "Contact " + id + " Updated", "API Contact Service",
                                    contactUpdated))
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse<>(400, "Failed Update Contact", "API Contact Service", null));
        } catch (BadRequestException e) {
            // TODO Auto-generated catch block
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), "API Contact Service", null));
        }

    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id) {
        try {
            contactService.deleteContact(id);
            return ResponseEntity.ok().build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Contact Not Found", "API Contact Service",
                            null));
        } catch (Exception e) {
            logUtils.logErrorWithTraceId("Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                            "API Contact Service", null));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Contact>>> searchContacts(
            @RequestParam(name = "bankName", required = false) String bankName,
            @RequestParam(name = "accountNumber", required = false) String accountNumber,
            @RequestParam(name = "contactName", required = false) String contactName) {

        return contactService.findBySearchCriteria(bankName, accountNumber, contactName);
    }

}
