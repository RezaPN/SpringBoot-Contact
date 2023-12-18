package com.adira.contact.service;

import java.util.Optional;

import org.apache.coyote.BadRequestException;

import com.adira.contact.pojo.User;

public interface UserService {

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    User createUser(User user);

    String updateUser(Long id, User user) throws BadRequestException;

    void deleteUser(Long id);

    boolean doesUserExistByEmail (String email);
}
