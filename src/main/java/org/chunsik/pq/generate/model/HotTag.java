package org.chunsik.pq.generate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "hot_tag")
public class HotTag {
    @Id
    @Column
    private String hotTag;

    public HotTag(String hotTag) {
        this.hotTag = hotTag;
    }
}
