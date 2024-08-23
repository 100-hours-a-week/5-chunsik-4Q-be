package org.chunsik.pq.generate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "Category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "eng_name", nullable = false)
    private String engName;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    public Category(String name, Integer sortOrder, String engName, Timestamp createdAt) {
        this.name = name;
        this.sortOrder = sortOrder;
        this.engName = engName;
        this.createdAt = createdAt;
    }
}
