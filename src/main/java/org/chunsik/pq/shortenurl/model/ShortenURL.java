package org.chunsik.pq.shortenurl.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "Shorten_URL")
public class ShortenURL {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "src_url")
    private String srcURL;

    @Column(name = "dest_url")
    private String destURL;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    public ShortenURL(String srcURL, String destURL, LocalDateTime createdAt) {
        this.srcURL = srcURL;
        this.destURL = destURL;
        this.createdAt = createdAt;
    }
}
