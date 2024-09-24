package org.chunsik.pq.s3.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chunsik.pq.generate.model.BackgroundImage;
import org.chunsik.pq.shortenurl.model.ShortenURL;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "Ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "url_id", nullable = false)
    private ShortenURL url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_id", nullable = false)
    private BackgroundImage backgroundImage;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    public Ticket(Long userId, ShortenURL url, BackgroundImage backgroundImage, String title, String imagePath) {
        this.userId = userId;
        this.url = url;
        this.backgroundImage = backgroundImage;
        this.title = title;
        this.imagePath = imagePath;
        this.createdAt = LocalDateTime.now();
    }

    public void updateTicket(String imagePath) {
        this.imagePath = imagePath;
        this.createdAt = LocalDateTime.now();
    }
}
