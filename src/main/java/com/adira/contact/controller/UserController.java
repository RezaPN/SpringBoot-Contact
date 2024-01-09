package com.adira.contact.controller;

import com.adira.contact.dto.ResponseBody.UserDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.User;
import com.adira.contact.service.UserService;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
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

    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateUser(@RequestBody User user)
            throws BadRequestException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong((String) authentication.getPrincipal());
        String updatedUserMessage = userService.updateUser(userId, user);

        if (updatedUserMessage != null) {
            ApiResponse<String> apiResponse = new ApiResponse<>(HttpStatus.CREATED.value(), updatedUserMessage, "API",
                    null);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", "API", null));
        }
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
