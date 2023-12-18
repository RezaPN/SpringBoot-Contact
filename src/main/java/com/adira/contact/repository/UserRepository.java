package com.adira.contact.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adira.contact.pojo.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);
}