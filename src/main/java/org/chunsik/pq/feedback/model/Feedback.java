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

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "star_rate", nullable = false)
    private Float starRate;

    @Column(name = "comment", length = 200)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "ease", nullable = false)
    private Integer ease;

    @Column(name = "design", nullable = false)
    private Integer design;

    @Column(name = "performance", nullable = false)
    private Integer performance;

    @Column(name = "feature", nullable = false)
    private Boolean feature;

    @Column(name = "recommendation", nullable = false)
    private Boolean recommendation;

    @Column(name = "reuse", nullable = false)
    private Boolean reuse;

    @Column(name = "age_group", nullable = false)
    private Integer ageGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    public enum Gender {
        MALE, FEMALE
    }

    public Feedback(Integer userId, Float starRate, String comment, Timestamp createdAt, Integer ease, Integer design,
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
