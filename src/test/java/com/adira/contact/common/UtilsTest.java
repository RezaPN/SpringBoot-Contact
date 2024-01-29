package com.adira.contact.common;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.adira.contact.entity.ApiResponse;

public class UtilsTest {

    @Test
    void testValidatePassword_ValidPassword_ReturnsTrue() {
        // Arrange
        Utils utils = new Utils();
        String validPassword = "Abcd@1234";

        // Act
        boolean result = utils.validatePassword(validPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidatePassword_InvalidPassword_ReturnsFalse() {
        // Arrange
        Utils utils = new Utils();
        String invalidPassword = "weakpassword";

        // Act
        boolean result = utils.validatePassword(invalidPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void testHandleValidationErrors_ReturnsBadRequestResponseWithErrors() {
        // Arrange
        Utils utils = new Utils();
        BindingResult bindingResult = new BeanPropertyBindingResult("object", "object");
        FieldError error1 = new FieldError("object", "field1", "Error message 1");
        FieldError error2 = new FieldError("object", "field2", "Error message 2");
        bindingResult.addError(error1);
        bindingResult.addError(error2);

        // Act
        ResponseEntity<ApiResponse<?>> responseEntity = utils.handleValidationErrors(bindingResult);
        ApiResponse<?> apiResponse = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getBody().getStatusCode());
        assertEquals("Validation failed", apiResponse.getMessage());
        assertEquals("API", apiResponse.getSource());
        List<String> expectedErrors = Arrays.asList("Error message 1", "Error message 2");
        assertEquals(expectedErrors, apiResponse.getResult());

    }

    @Test
    void testHandleValidationErrors_EmptyErrors_ReturnsBadRequestResponseWithEmptyErrors() {
        // Arrange
        Utils utils = new Utils();
        BindingResult bindingResult = new BeanPropertyBindingResult("object", "object");

        // Act
        ResponseEntity<ApiResponse<?>> responseEntity = utils.handleValidationErrors(bindingResult);
        ApiResponse<?> apiResponse = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getBody().getStatusCode());
        assertEquals("Validation failed", apiResponse.getMessage());
        assertEquals("API", apiResponse.getSource());
    }

    @Test
    void testUsingRandomUUID_ReturnsValidString() {
        // Arrange
        Utils utils = new Utils();

        // Act
        String result = utils.usingRandomUUID();

        // Assert
        assertNotNull(result);
        assertDoesNotThrow(() -> UUID.fromString(result)); // Check if the result is a valid UUID
        assertFalse(result.contains("_")); // Check if underscores are removed
    }

    @Test
    void testNullOnIsNonEmptyString_returnFalse() {
        // Arrange
        Utils utils = new Utils();
        String nonEmptyString = "Hello";

        // Act
        boolean result = utils.isNonEmptyString(nonEmptyString);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsEmptyString_returnTrue() {
        // Arrange
        Utils utils = new Utils();
        String emptyString = "";

        // Act
        boolean result = utils.isNonEmptyString(emptyString);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsNonEmptyString_WithWhitespace_ReturnsTrue() {
        // Arrange
        Utils utils = new Utils();
        String whitespaceString = "    ";

        // Act
        boolean result = utils.isNonEmptyString(whitespaceString);

        // Assert
        assertTrue(result);
    }
}
