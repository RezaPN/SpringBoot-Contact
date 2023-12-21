package com.adira.contact.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adira.contact.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsById(Long id);
}
