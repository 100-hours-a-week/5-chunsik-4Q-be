package org.chunsik.pq.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TokenClaimsDto {
    private final String email;
    private final Long userId;
}
