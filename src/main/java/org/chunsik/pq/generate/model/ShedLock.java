package org.chunsik.pq.generate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "shedlock")
public class ShedLock {
    @Id
    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "lock_until", nullable = false, columnDefinition = "TIMESTAMP(3)")
    private Timestamp lockUntil;

    @Column(name = "locked_at", nullable = false, columnDefinition = "TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3)")
    private Timestamp lockedAt;

    @Column(name = "locked_by", length = 255, nullable = false)
    private String lockedBy;

}
