package org.chunsik.pq.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPQResponseDto {
    private String ticketUrl;
    private String title;
    private int year;
    private int month;
    private int day;
    private String categoryName;
}
