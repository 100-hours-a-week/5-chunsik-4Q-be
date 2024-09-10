package org.chunsik.pq.generate.manager;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.dto.TagResponseDTO;
import org.chunsik.pq.generate.model.Tag;
import org.chunsik.pq.generate.repository.TagBackgroundImageRepository;
import org.chunsik.pq.generate.repository.TagRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HotTagManager {
    private final TagRepository tagRepository;
    private final TagBackgroundImageRepository tagBackgroundImageRepository;

    private TagResponseDTO tagResponseDTO;


    @PostConstruct
    public void initializeTags() {
        updateTags();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void updateTags() {
        List<String> hotTags = new ArrayList<>();

        LocalDateTime week = LocalDateTime.now().minusWeeks(1);
        List<Long> topTagId = tagBackgroundImageRepository.findTopTagId(week);
        List<Tag> tagList = tagRepository.findAll();

        List<Long> top10Tag = topTagId.size() > 10 ? topTagId.subList(0, 10) : topTagId;


        for (Tag tag : tagList) {
            if (top10Tag.contains(tag.getId())) {
                hotTags.add(tag.getName());
            }
        }

        tagResponseDTO = new TagResponseDTO(hotTags);
    }

    public TagResponseDTO getCurrentTags() {
        return tagResponseDTO;
    }
}
