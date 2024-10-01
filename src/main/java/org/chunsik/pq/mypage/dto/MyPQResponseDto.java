package org.chunsik.pq.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyPQResponseDto {
    private Long id;
    private String ticketUrl;
    private String title;
    private String formattedDate;
    private String categoryName;
    private List<String> tags;
    private int likesCount;
}