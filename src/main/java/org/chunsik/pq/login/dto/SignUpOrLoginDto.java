package org.chunsik.pq.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignUpOrLoginDto {
    private final String nickname;
    private final String email;
}
