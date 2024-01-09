package com.adira.contact.service;

import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.adira.contact.dto.RequestBody.UserRequestDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.User;

public interface UserService {

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    User createUser(User user);

    String updateUser(Long id, User user) throws BadRequestException;

    void deleteUser(Long id);

    boolean doesUserExistByEmail (String email);

    ResponseEntity<ApiResponse<?>> Register (UserRequestDTO user, BindingResult bindingResult);
}
