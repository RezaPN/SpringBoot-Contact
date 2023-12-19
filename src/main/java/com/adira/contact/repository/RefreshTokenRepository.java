package com.adira.contact.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adira.contact.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(Long userId);
}
