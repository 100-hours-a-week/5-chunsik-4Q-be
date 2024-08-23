package org.chunsik.pq.generate.dto;

public class CreateImageResponseDto {
    private String message;
    private Long id;

    public CreateImageResponseDto(String message, Long id) {
        this.message = message;
        this.id = id;
    }
}
