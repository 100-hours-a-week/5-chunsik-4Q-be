package org.chunsik.pq.generate.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "Tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "sort_order")
    private String sortOrder;

    @Column(name = "eng_name")
    private String engName;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "category")
    private String category;
}
