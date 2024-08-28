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
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MyPageService {
    private final CategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;
    private final UserManager userManager;

    // 모든 티켓 정보를 가져오는 메서드
    public List<MyPQResponseDto> getMyPQs() {
        return getMyPQsByFilter((userId, title) ->
                ticketRepository.findTicketsByUserIdWithBackgroundImageOrderByCreatedAtDesc(userId), null);
    }

    // 제목으로 필터링된 티켓 정보를 가져오는 메서드
    public List<MyPQResponseDto> getMyPQsByTitle(String title) {
        return getMyPQsByFilter(ticketRepository::findTicketsByUserIdAndTitleContainingOrderByCreatedAtDesc, title);
    }

    // 태그로 필터링된 티켓 정보를 가져오는 메서드
    public List<MyPQResponseDto> getMyPQsByTag(String tag) {
        return getMyPQsByFilter(ticketRepository::findTicketsByUserIdAndTagNameOrderByCreatedAtDesc, tag);
    }

    // 공통 로직을 처리하는 메서드
    private List<MyPQResponseDto> getMyPQsByFilter(BiFunction<Long, String, List<Ticket>> filterFunction, String title) {
        Long userId = userManager.currentUser()
                .map(CustomUserDetails::getId)
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        List<Ticket> tickets = filterFunction.apply(userId, title);

        List<Category> categories = categoryRepository.findAll();
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        return tickets.stream()
                .map(ticket -> {
                    String categoryName = categoryMap.get(ticket.getBackgroundImage().getCategoryId());
                    LocalDateTime createdAt = ticket.getCreatedAt();
                    String formattedDate = createdAt.format(formatter);

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
