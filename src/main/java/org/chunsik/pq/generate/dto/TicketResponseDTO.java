package org.chunsik.pq.generate.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class TicketResponseDTO {
    private final String ticketUrl;
    private final String title;
    private final String shortenUrl;
}