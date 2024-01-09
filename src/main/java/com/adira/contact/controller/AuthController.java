package com.adira.contact.controller;

import com.adira.contact.dto.RequestBody.UserRequestDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@Valid @RequestBody UserRequestDTO user,
            BindingResult bindingResult) {
        return userService.Register(user, bindingResult);
    }

}
