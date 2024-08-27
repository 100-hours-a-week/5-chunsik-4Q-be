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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MyPageService {
    private final CategoryRepository categoryRepository;  // 카테고리 정보를 가져오는 리포지토리
    private final TicketRepository ticketRepository;  // 티켓 정보를 가져오는 리포지토리
    private final UserManager userManager;  // 현재 사용자 정보를 관리하는 매니저

    private Map<Long, String> categoryMap;  // 카테고리 ID와 이름을 매핑하는 맵

    public List<MyPQResponseDto> getMyPQs() {
        // 현재 로그인한 사용자의 userId를 가져옴
        Long userId = userManager.currentUser()
                .map(CustomUserDetails::getId)
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        // 해당 사용자의 티켓 정보를 생성일자 기준 내림차순으로 조회
        List<Ticket> tickets = ticketRepository.findTicketsByUserIdWithBackgroundImageOrderByCreatedAtDesc(userId);

        // 모든 카테고리 정보를 조회하여 categoryMap에 ID와 이름을 매핑
        List<Category> categories = categoryRepository.findAll();
        categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 티켓 정보를 MyPQResponseDto 리스트로 변환하여 반환
        return tickets.stream()
                .map(ticket -> {
                    // BackgroundImage 엔티티에서 CategoryId를 사용해 CategoryName을 조회
                    String categoryName = categoryMap.get(ticket.getBackgroundImage().getCategoryId());

                    // 티켓의 생성일자에서 년, 월, 일을 추출
                    LocalDateTime createdAt = ticket.getCreatedAt();
                    int year = createdAt.getYear();
                    int month = createdAt.getMonthValue();
                    int day = createdAt.getDayOfMonth();

                    // MyPQResponseDto 객체를 생성하여 반환
                    return new MyPQResponseDto(
                            ticket.getImagePath(),  // 이미지 경로
                            ticket.getTitle(),  // 티켓 제목
                            year,  // 생성일의 년도
                            month,  // 생성일의 월
                            day,  // 생성일의 일
                            categoryName  // 카테고리 이름
                    );
                })
                .collect(Collectors.toList());
    }
}
