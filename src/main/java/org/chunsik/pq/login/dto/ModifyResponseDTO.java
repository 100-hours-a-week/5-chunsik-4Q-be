package org.chunsik.pq.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ModifyResponseDTO {

    private final Long id;
    private final String email;
    private final String nickname;
}
