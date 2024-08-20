package org.chunsik.pq.shortenurl.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public ShortenURL(String srcURL, String destURL) {
        this.srcURL = srcURL;
        this.destURL = destURL;
    }
}
