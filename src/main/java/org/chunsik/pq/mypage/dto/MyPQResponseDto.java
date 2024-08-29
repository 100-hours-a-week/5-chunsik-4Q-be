package org.chunsik.pq.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPQResponseDto {
    private Long ticketId;
    private String ticketUrl;
    private String title;
    private String formattedDate;
    private String categoryName;
    private String backgroundImageUrl;
}
