package org.chunsik.pq.generate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
public class GenerateApiRequestDTO {
    private MultipartFile ticketImage;
    private Long shortenUrlId;
    private String title;
    private Long backgroundImageId;
}
