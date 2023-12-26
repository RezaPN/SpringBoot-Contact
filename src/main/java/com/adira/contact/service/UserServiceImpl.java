package com.adira.contact.service;

import com.adira.contact.common.Constants;
import com.adira.contact.common.Utils;
import com.adira.contact.dto.UserDTO;
import com.adira.contact.dto.UserRequestDTO;
import com.adira.contact.entity.ApiResponse;
import com.adira.contact.entity.User;
import com.adira.contact.exception.CustomNotFoundException;
import com.adira.contact.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
   
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

    @Override
    public ResponseEntity<ApiResponse<?>> Register(UserRequestDTO userRequestDTO, BindingResult bindingResult) {

        Utils utils = new Utils();

        if (bindingResult.hasErrors()) {
            //there is good way to simplify this, check lecture 24 final project on exception handle (without using binding result)
            return utils.handleValidationErrors(bindingResult);
        }

        if (doesUserExistByEmail(userRequestDTO.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(),
                    Constants.USER_ALREADY_EXISTS, "API", null));
        }

        User user = new User(userRequestDTO.getEmail(), passwordEncoder.encode(userRequestDTO.getPassword()));

        User createdUser = createUser(user);

        if (createdUser != null) {
            UserDTO userDTO = new UserDTO(createdUser.getId(), createdUser.getEmail(), createdUser.isAdmin());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(HttpStatus.CREATED.value(), "User created", "API", userDTO));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.USER_CREATION_FAILED, "API",
                            null));
        }
    }
}
