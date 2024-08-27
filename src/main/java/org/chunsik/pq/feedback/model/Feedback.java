package org.chunsik.pq.feedback.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "Feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "star_rate")
    private Integer starRate;

    @Column(name = "comment", length = 200)
    private String comment;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "ease")
    private Integer ease;

    @Column(name = "design")
    private Integer design;

    @Column(name = "performance")
    private Integer performance;

    @Column(name = "feature")
    private Boolean feature;

    @Column(name = "recommendation")
    private Boolean recommendation;

    @Column(name = "reuse")
    private Boolean reuse;

    @Column(name = "age_group")
    private Integer ageGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    public enum Gender {
        MALE, FEMALE
    }

    public Feedback(Long userId, Integer starRate, String comment, Timestamp createdAt, Integer ease, Integer design,
                    Integer performance, Boolean feature, Boolean recommendation, Boolean reuse, Integer ageGroup, Gender gender) {
        this.userId = userId;
        this.starRate = starRate;
        this.comment = comment;
        this.createdAt = createdAt;
        this.ease = ease;
        this.design = design;
        this.performance = performance;
        this.feature = feature;
        this.recommendation = recommendation;
        this.reuse = reuse;
        this.ageGroup = ageGroup;
        this.gender = gender;
    }
}
