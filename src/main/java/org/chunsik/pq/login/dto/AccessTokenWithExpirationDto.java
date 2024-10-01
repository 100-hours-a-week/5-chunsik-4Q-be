package org.chunsik.pq.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AccessTokenWithExpirationDto {
    private final String accessToken;
    private final Long expiration;
}
