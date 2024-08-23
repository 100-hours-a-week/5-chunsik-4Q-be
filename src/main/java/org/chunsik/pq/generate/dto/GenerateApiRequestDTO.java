package org.chunsik.pq.generate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class GenerateApiRequestDTO {
    private MultipartFile ticketImage;
    private String backgroundImageUrl;
    private Integer shortenUrlId;
    private String title;
    private List<String> tags;
    private String category;
}
