package org.chunsik.pq.delete.service;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.chunsik.pq.s3.manager.S3Manager;
import org.chunsik.pq.s3.model.Ticket;
import org.chunsik.pq.s3.repository.TicketRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeleteService {
    private final S3Manager s3Manager;
    private final UserManager userManager;
    private final TicketRepository ticketRepository;

    @Transactional
    public void deleteById(Long id) {
        Long userId = findLoginUserIdOrNull();
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Ticket not found by Id: " + id));
        if (ticket.getUserId() == null) {
            throw new AccessDeniedException("Ticket is public property and cannot be deleted."); // 비회원이 만든 티켓은 삭제 허용하지 않음
        } else if (!ticket.getUserId().equals(userId)) {
            throw new AccessDeniedException("Authentication is necessary to delete a ticket."); // 요청 클라이언트 Id와 티켓 작성자 Id가 다르면 삭제 허용하지 않음
        }
        ticketRepository.deleteById(id); // 아래 코드에서 S3Exception이 발생하면 롤백
        s3Manager.deleteFile(ticket.getImagePath());
    }

    @Nullable
    private Long findLoginUserIdOrNull() {
        Optional<CustomUserDetails> currentUser = userManager.currentUser();
        return currentUser.map(CustomUserDetails::getId).orElse(null);
    }
}