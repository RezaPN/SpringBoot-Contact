package com.adira.contact.service;

import java.util.Optional;

import com.adira.contact.pojo.RefreshToken;

public interface RefreshTokenService {

    Optional<RefreshToken> getRefreshTokenByToken(String token);

    Optional<RefreshToken> getRefreshTokenByUserId(Long userId);

    RefreshToken createRefreshToken(RefreshToken refreshToken);

    void deleteRefreshToken(Long id);
}
