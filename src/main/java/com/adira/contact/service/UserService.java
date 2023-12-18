package com.adira.contact.service;

import java.util.Optional;

import com.adira.contact.pojo.User;

public interface UserService {

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}
