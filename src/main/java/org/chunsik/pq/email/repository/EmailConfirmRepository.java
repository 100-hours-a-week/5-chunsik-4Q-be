package org.chunsik.pq.email.repository;

import org.chunsik.pq.email.model.EmailConfirm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailConfirmRepository extends JpaRepository<EmailConfirm, Integer> {
    Optional<EmailConfirm> findByEmail(String email);
}
