package com.adira.contact.service;

import com.adira.contact.common.Utils;
import com.adira.contact.exception.CustomNotFoundException;
import com.adira.contact.pojo.User;
import com.adira.contact.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public String updateUser(Long id, User user) throws BadRequestException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("User with ID " + id + " not found"));

        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (doesUserExistByEmail(user.getEmail())) {
                throw new CustomNotFoundException("Email " + user.getEmail() + " already exists!");
            }
            existingUser.setEmail(user.getEmail());
        }

        if (user.getPassword() != null) {
            Utils utils = new Utils();
            if (!utils.validatePassword(user.getPassword())) {
                throw new BadRequestException(
                        "Password must contain at least 8 characters, including one uppercase letter, one lowercase letter, one digit, and one special character");
            } else {
                existingUser.setPassword(user.getPassword());
            }
        }

        userRepository.save(existingUser);

        return "User with ID " + id + " updated successfully";
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new CustomNotFoundException("User with ID " + id + " not found");
        }

    }

    @Override
    public boolean doesUserExistByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
