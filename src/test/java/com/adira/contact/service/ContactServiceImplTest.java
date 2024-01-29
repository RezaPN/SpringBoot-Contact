package com.adira.contact.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;

import com.adira.contact.common.Utils;
import com.adira.contact.dto.RequestBody.ContactRequestDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.Contact;
import com.adira.contact.entity.User;
import com.adira.contact.exception.CustomNotFoundException;
import com.adira.contact.repository.ContactRepository;
import com.adira.contact.repository.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContactServiceImplTest {

    private ContactRepository contactRepository;
    private UserService userService;
    private ContactServiceImpl contactService;
    private BindingResult bindingResult;
    private Utils utils;
    private Authentication authentication;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        contactRepository = mock(ContactRepository.class);
        bindingResult = mock(BindingResult.class);
        utils = mock(Utils.class);
        userRepository = mock(UserRepository.class);
        userService = mock(UserServiceImpl.class); // Mock UserService
        contactService = new ContactServiceImpl(contactRepository, userService);
        authentication = mock(Authentication.class);
    }

    @Test
    void getAllContacts() {
        // Arrange
        Long userId = 1L;
        List<Contact> expectedContacts = Arrays.asList(new Contact(), new Contact());

        when(authentication.getPrincipal()).thenReturn(userId.toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(contactRepository.findByUserId(userId)).thenReturn(expectedContacts);

        // Act
        List<Contact> actualContacts = contactService.getAllContacts();

        // Assert
        assertEquals(expectedContacts, actualContacts);
        verify(contactRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testGetContactById_ContactFound() {
        // Arrange
        Long contactId = 1L;
        Long userId = 123L;
        Contact mockedContact = new Contact();
        mockedContact.setId(contactId);
        User mockedUser = new User("test@example.com", "password");
        mockedUser.setId(userId);

        when(authentication.getPrincipal()).thenReturn(userId.toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(contactRepository.findByIdAndUser(contactId, mockedUser)).thenReturn(Optional.of(mockedContact));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(userService.getUserById(userId)).thenReturn(Optional.of(mockedUser));

        // Act
        Contact result = contactService.getContactById(contactId);

        // Assert
        assertNotNull(result);
        assertEquals(contactId, result.getId());

        // Verify that repository and service methods were called
        verify(contactRepository, times(1)).findByIdAndUser(contactId, mockedUser);

        // Use the mocked userService for verification, not the actual implementation
        verify((UserServiceImpl) userService, times(1)).getUserById(userId);
    }

    @Test
    void testGetContactById_ContactNotFound() {
        // Arrange
        Long contactId = 1L;
        Long userId = 123L;

        when(authentication.getPrincipal()).thenReturn(userId.toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock user retrieval
        User mockedUser = new User("test@example.com", "password");
        mockedUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(userService.getUserById(userId)).thenReturn(Optional.of(mockedUser));

        // Mock contact retrieval with an empty Optional
        when(contactRepository.findByIdAndUser(contactId, mockedUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> contactService.getContactById(contactId));

        // Verify that repository and service methods were called
        verify(contactRepository, times(1)).findByIdAndUser(contactId, mockedUser);
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testGetContactsByUserId() {
        Long userId = 123L;
        List<Contact> expectedContacts = Arrays.asList(new Contact(), new Contact());

        when(contactRepository.findByUserId(userId)).thenReturn(expectedContacts);

        // Act
        List<Contact> actualContacts = contactService.getContactsByUserId(userId);

        assertEquals(expectedContacts, actualContacts);
        verify(contactRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testCreateContact() {
        Long userId = 123L;
        Long idContact = 1L;
        // Mock data
        ContactRequestDTO contactRequest = new ContactRequestDTO("123456", "Test Bank", "John Doe");
        when(authentication.getPrincipal()).thenReturn(userId.toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getPrincipal()).thenReturn(userId.toString());
        when(userService.getUserById(userId)).thenReturn(Optional.of(new User("test@example.com", "password")));

        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> {
            Contact savedContact = invocation.getArgument(0);
            savedContact.setId(idContact); // Mocking the ID for simplicity
            return savedContact;
        });

        // Call the method
        Contact contact = contactService.createContact(contactRequest, bindingResult);



        // Assertions
        assertNotNull(contact);
        assertEquals(idContact, contact.getId());
        verify(userService, times(1)).getUserById(userId);
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    // @Test
    // void testCreateContact_ValidationFailed() {
    //     Long userId = 123L;
    //     ContactRequestDTO contactRequest = new ContactRequestDTO("123456", "", "John Doe");
    //     when(authentication.getPrincipal()).thenReturn(userId.toString());
    //     when(bindingResult.hasErrors()).thenReturn(true);
    //     SecurityContextHolder.getContext().setAuthentication(authentication);
    //     // Mock behavior when validation fails
    //     when(utils.handleValidationErrors(bindingResult)).thenReturn(ResponseEntity.badRequest()
    //             .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", "API",
    //                     Collections.singletonList("Bank Name should not be Empty"))));

    //     // Call the method
    //     contactService.createContact(contactRequest, bindingResult);

    //     // // Assertions
    //     // assertNotNull(contactCreated);
    //     // assertEquals(HttpStatus.BAD_REQUEST, contactCreated);

    //     // ApiResponse<?> apiResponse = responseEntity.getBody();
    //     // assertNotNull(apiResponse);
    //     // assertEquals(400, apiResponse.getStatusCode());
    //     // assertEquals("Validation failed", apiResponse.getMessage());
    //     // assertEquals("API", apiResponse.getSource());

    //     // Verify that userService and contactRepository methods were not called
    //     verifyNoInteractions(userService);
    //     verifyNoInteractions(contactRepository);
    // }

    // @Test
    // void testCreateContact_UserNotPresent() {
    //     Long userId = 123L;
    //     ContactRequestDTO contactRequest = new ContactRequestDTO("123456", "BCA", "John Doe");
    //     when(authentication.getPrincipal()).thenReturn(userId.toString());
    //     when(bindingResult.hasErrors()).thenReturn(false);
    //     SecurityContextHolder.getContext().setAuthentication(authentication);

    //     // Call the method
    //     ResponseEntity<ApiResponse<?>> responseEntity = contactService.createContact(contactRequest, bindingResult);

    //     // Assertions
    //     assertNotNull(responseEntity);
    //     assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

    //     ApiResponse<?> apiResponse = responseEntity.getBody();
    //     assertNotNull(apiResponse);
    //     assertEquals(404, apiResponse.getStatusCode());
    //     assertEquals("User not found", apiResponse.getMessage());
    //     assertEquals("API Contact Service", apiResponse.getSource());

    //     verify(userService, times(1)).getUserById(userId);
    //     verifyNoInteractions(contactRepository);
    // }
}
