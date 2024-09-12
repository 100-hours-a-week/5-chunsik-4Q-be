package org.chunsik.pq.generate.manager;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.chunsik.pq.generate.dto.TagResponseDTO;
import org.chunsik.pq.generate.model.HotTag;
import org.chunsik.pq.generate.model.Tag;
import org.chunsik.pq.generate.repository.HotTagRepository;
import org.chunsik.pq.generate.repository.TagBackgroundImageRepository;
import org.chunsik.pq.generate.repository.TagRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.chunsik.pq.generate.util.constant.HotTagConstant.*;

@Component
@RequiredArgsConstructor
public class HotTagManager {
    private final TagRepository tagRepository;
    private final HotTagRepository hotTagRepository;
    private final TagBackgroundImageRepository tagBackgroundImageRepository;

    @PostConstruct
    public void initializeTags() {
        if (hotTagRepository.findAll().isEmpty()) {
            updateTags();
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @SchedulerLock(name = "hot-tag-lock")
    public void updateTags() {

        List<HotTag> hotTags = new ArrayList<>();

        LocalDateTime week = LocalDateTime.now().minusWeeks(1);
        List<Long> topTagId = tagBackgroundImageRepository.findTopTagId(week);
        List<Tag> tagList = tagRepository.findAll();
        if (topTagId.isEmpty()) {
            hotTags = List.of(new HotTag(NEON), new HotTag(BLUE), new HotTag(GREEN), new HotTag(SUMMER));

            hotTagRepository.deleteAll();
            hotTagRepository.saveAll(hotTags);
            return;
        }

        List<Long> top10Tag = topTagId.size() > 10 ? topTagId.subList(0, 10) : topTagId;


        for (Tag tag : tagList) {
            if (top10Tag.contains(tag.getId())) {
                hotTags.add(new HotTag(tag.getName()));
            }
        }

        hotTagRepository.deleteAll();
        hotTagRepository.saveAll(hotTags);
    }

    public TagResponseDTO getCurrentTags() {
        List<HotTag> responseTags = hotTagRepository.findAll();
        List<String> hotTags = new ArrayList<>();

        for (HotTag responseTag : responseTags) {
            hotTags.add(responseTag.getHotTag());
        }

        return new TagResponseDTO(hotTags);
    }
}
