package org.chunsik.pq.mypage.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.model.Category;
import org.chunsik.pq.generate.repository.CategoryRepository;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.chunsik.pq.mypage.dto.MyPQResponseDto;
import org.chunsik.pq.s3.model.Ticket;
import org.chunsik.pq.s3.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MyPageService {
    private final CategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;
    private final UserManager userManager;

    public List<MyPQResponseDto> getMyPQs() {
        // 현재 로그인한 사용자의 userId를 가져옴
        Long userId = userManager.currentUser()
                .map(CustomUserDetails::getId)
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        // 해당 사용자의 티켓 정보를 최신순 조회
        List<Ticket> tickets = ticketRepository.findTicketsByUserIdWithBackgroundImageOrderByCreatedAtDesc(userId);

        // 모든 카테고리 정보를 조회하여 categoryMap에 ID와 이름을 매핑
        List<Category> categories = categoryRepository.findAll();
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 날짜 형식을 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        // 티켓 정보를 MyPQResponseDto 리스트로 변환하여 반환
        return tickets.stream()
                .map(ticket -> {
                    // CategoryName을 조회
                    String categoryName = categoryMap.get(ticket.getBackgroundImage().getCategoryId());

                    // 티켓의 생성일자를 yyyy/MM/dd 형식으로 포맷팅
                    LocalDateTime createdAt = ticket.getCreatedAt();
                    String formattedDate = createdAt.format(formatter);

                    // MyPQResponseDto 객체를 생성하여 반환
                    return new MyPQResponseDto(
                            ticket.getImagePath(),
                            ticket.getTitle(),
                            formattedDate,
                            categoryName
                    );
                })
                .collect(Collectors.toList());
    }
}
