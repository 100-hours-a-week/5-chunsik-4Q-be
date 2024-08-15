package org.chunsik.pq.email.model;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "email_confirm")
public class EmailConfirm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String secretCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isSend = false;

    private LocalDateTime sendedAt;

    @Column(nullable = false)
    private Boolean confirmation = false;

    private LocalDateTime confirmedAt;

}
