package org.chunsik.pq.mypage.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.repository.CategoryRepository;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.chunsik.pq.mypage.dto.MyPQResponseDto;
import org.chunsik.pq.s3.model.Ticket;
import org.chunsik.pq.s3.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MyPageService {
    private final CategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;
    private final UserManager userManager;

    public List<MyPQResponseDto> getMyPQs() {
        // 현재 userId 가져오기
        Long userId = userManager.currentUser()
                .map(CustomUserDetails::getId)
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        // 티켓 ID를 생성일자 내림차순으로 가져오기
        List<Long> ticketIds = ticketRepository.findTicketIdsByUserIdOrderByCreatedAtDesc(userId);

        // 티켓 정보를 조회
        List<Ticket> tickets = ticketRepository.findByIdIn(ticketIds);

        // 티켓을 ID 순서에 맞게 다시 정렬
        List<MyPQResponseDto> result = ticketIds.stream()
                .map(ticketId -> tickets.stream()
                        .filter(ticket -> ticket.getId().equals(ticketId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Ticket not found: " + ticketId))
                )
                .map(ticket -> {
                    // BackgroundImage 엔티티에서 CategoryId를 조회하고, 이를 통해 CategoryName을 가져옴
                    String categoryName = categoryRepository.findNameById(ticket.getBackgroundImage().getCategoryId());

                    // 티켓의 생성일자에서 년, 월, 일 추출
                    LocalDateTime createdAt = ticket.getCreatedAt();
                    int year = createdAt.getYear();
                    int month = createdAt.getMonthValue();
                    int day = createdAt.getDayOfMonth();

                    // MyPQResponseDto로 변환
                    return new MyPQResponseDto(
                            ticket.getImagePath(),
                            ticket.getTitle(),
                            year,
                            month,
                            day,
                            categoryName
                    );
                })
                .collect(Collectors.toList());

        return result;
    }
}
