package com.adira.contact.controller;

import com.adira.contact.common.LogUtils;
import com.adira.contact.common.Utils;
import com.adira.contact.dto.ContactRequestDTO;
import com.adira.contact.dto.ContactUpdateDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.Contact;
import com.adira.contact.service.ContactService;
import jakarta.validation.Valid;
import org.hibernate.NonUniqueResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.slf4j.MDC;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);
    Utils utils = new Utils();

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
            // Handle exception (logging, custom error response, etc.)
            ApiResponse<List<Contact>> errorResponse = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error", "An error occurred while processing the request", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
        return contactService.updateContact(id, contactUpdate);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id) {
        String traceId = utils.usingRandomUUID();
        MDC.put("traceId", traceId);
        LogUtils.logInfoWithTraceId("START: DELETE request received. URL: /api/contacts/" + id);
        contactService.deleteContact(id);
        LogUtils.logInfoWithTraceId("FINISH: Delete Success ID: " + id);
        MDC.remove("traceId");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Contact>>> searchContacts(
            @RequestParam(name = "bankName", required = false) String bankName,
            @RequestParam(name = "accountNumber", required = false) String accountNumber,
            @RequestParam(name = "contactName", required = false) String contactName) {

        return contactService.findBySearchCriteria(bankName, accountNumber, contactName);
    }

}
