package org.chunsik.pq.mypage.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.model.Category;
import org.chunsik.pq.generate.repository.CategoryRepository;
import org.chunsik.pq.generate.repository.TagRepository;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.chunsik.pq.mypage.dto.MyPQResponseDto;
import org.chunsik.pq.s3.model.Ticket;
import org.chunsik.pq.s3.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MyPageService {
    private final CategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;
    private final TagRepository tagRepository;
    private final UserManager userManager;

    public List<MyPQResponseDto> getMyPQs() {
        // 현재 로그인한 사용자의 userId를 가져옴
        Long userId = userManager.currentUser()
                .map(CustomUserDetails::getId)
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        // 모든 카테고리 정보를 조회하여 categoryMap에 ID와 이름을 매핑
        List<Category> categories = categoryRepository.findAll();
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 모든 태그 정보를 조회하여 태그를 photoBackgroundId와 매핑
        List<Object[]> tagResults = tagRepository.findAllTagsWithBackgroundIds();
        Map<Long, List<String>> tagMap = tagResults.stream()
                .collect(Collectors.groupingBy(
                        result -> (Long) result[1], // photoBackgroundId
                        Collectors.mapping(result -> (String) result[0], Collectors.toList()) // tag name
                ));

        // 해당 사용자의 티켓 정보 및 좋아요 개수를 조회
        List<Object[]> ticketResults = ticketRepository.findTicketsWithLikesByUserIdOrderByCreatedAtDesc(userId);

        // 날짜 형식을 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        // 티켓 정보를 MyPQResponseDto 리스트로 변환하여 반환
        return ticketResults.stream()
                .map(result -> {
                    Ticket ticket = (Ticket) result[0];
                    Long likesCount = (Long) result[2];

                    // CategoryName을 조회
                    String categoryName = categoryMap.get(ticket.getBackgroundImage().getCategoryId());

                    // 해당 티켓의 배경 이미지에 연결된 태그 조회
                    List<String> tags = tagMap.getOrDefault(ticket.getBackgroundImage().getId(), List.of());

                    // 티켓의 생성일자를 YYYY/MM/DD 형식으로 포맷팅
                    LocalDateTime createdAt = ticket.getCreatedAt();
                    String formattedDate = createdAt.format(formatter);

                    // MyPQResponseDto 객체를 생성하여 반환
                    return new MyPQResponseDto(
                            ticket.getImagePath(),
                            ticket.getTitle(),
                            formattedDate,
                            categoryName,
                            tags,
                            likesCount.intValue()
                    );
                })
                .collect(Collectors.toList());
    }
}
