package org.chunsik.pq.shortenurl.repository;

import org.chunsik.pq.shortenurl.model.ShortenURL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortenUrlRepository extends JpaRepository<ShortenURL, Long> {
    ShortenURL findByDestURL(String destURL);
}
