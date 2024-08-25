package org.chunsik.pq.login.dto;

import lombok.Getter;

@Getter
public class MeResponseDto {
    private final Integer id;
    private final String email;
    private final String nickname;

    public MeResponseDto(Integer id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}
