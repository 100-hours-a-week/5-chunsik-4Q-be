package org.chunsik.pq.login.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinDto {

    @NotNull
    @NotBlank
    private String nickname;

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$")
    private String email;

    @NotNull
    @NotBlank
    private String password;

    // 기본 생성자
    public JoinDto() {
    }

    public JoinDto(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
}
