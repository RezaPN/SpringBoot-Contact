package com.adira.contact.controller;

import com.adira.contact.dto.UserDTO;
import com.adira.contact.pojo.ApiResponse;
import com.adira.contact.pojo.User;
import com.adira.contact.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDTO userDTO = convertToDTO(user); // Fungsi konversi dari User ke UserDTO
            ApiResponse<UserDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User found", "API", userDTO);
            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<UserDTO> apiResponse = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", "API",
                    null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors
            List<String> errors = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errors.add(error.getDefaultMessage());
            }
            ApiResponse<List<String>> apiResponse = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(),
                    "Validation failed",
                    "API", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }

        User createdUser = userService.createUser(user);

        if (createdUser != null) {
            ApiResponse<User> apiResponse = new ApiResponse<>(HttpStatus.CREATED.value(), "User created", "API",
                    createdUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } else {
            ApiResponse<User> apiResponse = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to create user", "API", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return updatedUser != null ? new ResponseEntity<>(updatedUser, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getEmail(), user.isAdmin());
    }
}
