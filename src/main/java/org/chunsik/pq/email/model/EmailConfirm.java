package org.chunsik.pq.email.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name="email_confirm")
@Entity
public class EmailConfirm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String secretCode;
    private LocalDateTime createdAt;
    @Builder.Default
    private Boolean isSend = false;
    private LocalDateTime sendedAt;
    @Builder.Default
    private Boolean confirmation = false;
    private LocalDateTime confirmedAt;
}
