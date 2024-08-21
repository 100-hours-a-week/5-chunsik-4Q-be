package org.chunsik.pq.shortenurl.dto;


import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RequestConvertUrlDTO {

    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "올바른 URL이 아닙니다")
    private String srcUrl;
}
