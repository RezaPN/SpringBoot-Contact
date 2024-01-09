package com.adira.contact.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import com.adira.contact.common.Constants;
import com.adira.contact.common.Utils;
import com.adira.contact.dto.RequestBody.UserRequestDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.User;
import com.adira.contact.exception.CustomNotFoundException;
import com.adira.contact.repository.UserRepository;

class UserServiceImplTest {

    private UserServiceImpl userService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private BindingResult bindingResult;
    private Utils utils;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        bindingResult = mock(BindingResult.class);
        utils = mock(Utils.class);

        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        UserRequestDTO userRequestDTO = new UserRequestDTO("test@example.com", "password123");
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // Act
        ResponseEntity<ApiResponse<?>> result = userService.Register(userRequestDTO, bindingResult);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.CREATED.value(), result.getBody().getStatusCode());
        assertEquals("User created", result.getBody().getMessage());
    }

    @Test
    void testRegisterUser_UserAlreadyExists() {
        // Arrange
        UserRequestDTO userRequestDTO = new UserRequestDTO("existing@example.com", "password123");
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(true);

        // Act
        ResponseEntity<ApiResponse<?>> result = userService.Register(userRequestDTO, bindingResult);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getBody().getStatusCode());
        assertEquals("User with this email already exists", result.getBody().getMessage());
    }

    @Test
    void testRegisterUser_ValidationErrors() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);
        ResponseEntity<ApiResponse<?>> expectedResponse = ResponseEntity.badRequest().body(
                new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", "API",
                        Arrays.asList(new String[] {})));

        when(utils.handleValidationErrors(bindingResult)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<?>> result = userService.Register(new UserRequestDTO(), bindingResult);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(expectedResponse.getBody().getResult(), result.getBody().getResult());
        assertEquals(expectedResponse.getBody().getMessage(), result.getBody().getMessage());
    }

    @Test
    void testRegisterUser_UserCreationFailed() {
        UserRequestDTO userRequestDTO = new UserRequestDTO("test@example.com", "password123");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.doesUserExistByEmail(userRequestDTO.getEmail())).thenReturn(false);
        when(userService.createUser(any(User.class))).thenReturn(null); // Simulate user creation failure

        // Act
        ResponseEntity<ApiResponse<?>> result = userService.Register(userRequestDTO, bindingResult);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getBody().getStatusCode());
        assertEquals(Constants.USER_CREATION_FAILED, result.getBody().getMessage());
    }

    @Test
    void testDeleteUser_UserExists() {
        // Arrange

        Long userId = (long) 11;

        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> userService.deleteUser(userId));

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Arrange

        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        // Act and Assert
        CustomNotFoundException exception = assertThrows(CustomNotFoundException.class,
                () -> userService.deleteUser(userId));
        assertEquals("User with ID " + userId + " not found", exception.getMessage());

        // Ensure deleteById is never called in this case
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void testUpdateUser() {
        // Mock data
        Long userId = 1L;
        User existingUser = new User("existing@example.com", "Arisato432011@");
        User updatedUser = new User("updated@example.com", "Arisato432011@");

        // Stub userRepository behavior
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(existingUser);

        // Call the update method
        try {
            String result = userService.updateUser(userId, updatedUser);

            // Assertions
            verify(userRepository, times(1)).findById(userId);
            verify(userRepository, times(1)).save(existingUser);
            assertEquals("User Updated Successfully", result);

        } catch (BadRequestException | CustomNotFoundException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void testUpdateUserNotFound() {
        // Mock data
        Long userId = 1L;
        User updatedUser = new User("existing@example.com", "Arisato432011@");

        // Stub userRepository behavior
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Call the update method and expect CustomNotFoundException
        assertThrows(CustomNotFoundException.class, () -> userService.updateUser(userId, updatedUser));

        // Assertions
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUserWithInvalidPassword() {
        // Mock data
        Long userId = 1L;
        User existingUser = new User("existing@example.com", "Arisato432011@");
        User updatedUser = new User("updated@example.com", "falsepassword");

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));

        // Call the update method and expect BadRequestException
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.updateUser(userId, updatedUser));

        verify(userRepository, never()).save(any());
        assertEquals(
                "Password must contain at least 8 characters, including one uppercase letter, one lowercase letter, one digit, and one special character",
                exception.getMessage());
    }

    @Test
    void testGetUserById() {
        // Mock data
        Long userId = 1L;
        User expectedUser = new User("test@example.com", "Test123!");

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Call the getUserById method
        Optional<User> result = userService.getUserById(userId);

        // Assertions
        verify(userRepository, times(1)).findById(userId);
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
    }

    @Test
    void testGetUserByIdNotFound() {
        // Mock data
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Call the getUserById method
        Optional<User> result = userService.getUserById(userId);

        // Assertions
        verify(userRepository, times(1)).findById(userId);
        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserByEmail() {
        // Mock data
        String email = "test@example.com";
        User expectedUser = new User(email, "Test123!");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        // Call the getUserByEmail method
        Optional<User> result = userService.getUserByEmail(email);

        // Assertions
        verify(userRepository, times(1)).findByEmail(email);
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
    }

    @Test
    void testGetUserByEmailNotFound() {
        // Mock data
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Call the getUserByEmail method
        Optional<User> result = userService.getUserByEmail(email);

        // Assertions
        verify(userRepository, times(1)).findByEmail(email);
        assertFalse(result.isPresent());
    }
}
