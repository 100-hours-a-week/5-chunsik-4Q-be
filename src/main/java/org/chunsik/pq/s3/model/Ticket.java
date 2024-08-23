package org.chunsik.pq.s3.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chunsik.pq.generate.model.BackgroundImage;
import org.chunsik.pq.model.User;
import org.chunsik.pq.shortenurl.model.ShortenURL;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "Ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    private ShortenURL url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_id", nullable = false)
    private BackgroundImage backgroundImage;

    @Column(name = "title")
    private String title;

    @Column(name = "image_path")
    private String imagePath;

    public Ticket(Integer userId, ShortenURL url, BackgroundImage backgroundImage, String title, String imagePath) {
        this.userId = userId;
        this.url = url;
        this.backgroundImage = backgroundImage;
        this.title = title;
        this.imagePath = imagePath;
    }
}

