package org.chunsik.pq.feedback.dto;

import lombok.Getter;

@Getter
public class FeedbackDTO {
    private Integer starRate;
    private String comment;
    private Integer ease;
    private Integer design;
    private Integer performance;
    private Boolean feature;
    private Boolean recommendation;
    private Boolean reuse;
    private Integer ageGroup;
    private String gender;
}
