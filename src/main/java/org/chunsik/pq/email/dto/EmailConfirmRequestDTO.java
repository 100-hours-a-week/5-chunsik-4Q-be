package org.chunsik.pq.email.dto;

import lombok.Data;

@Data
public class EmailConfirmRequestDTO {
    private String email;
    private String code;
}
