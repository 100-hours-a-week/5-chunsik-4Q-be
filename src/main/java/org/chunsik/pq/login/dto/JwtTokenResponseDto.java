package org.chunsik.pq.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtTokenResponseDto {
    private final String accessToken;
}
