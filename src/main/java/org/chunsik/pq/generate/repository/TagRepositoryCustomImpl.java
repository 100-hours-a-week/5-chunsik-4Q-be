package org.chunsik.pq.generate.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.chunsik.pq.generate.model.QBackgroundImage.backgroundImage;
import static org.chunsik.pq.generate.model.QTag.tag;
import static org.chunsik.pq.generate.model.QTagBackgroundImage.tagBackgroundImage;

@Repository
@RequiredArgsConstructor
public class TagRepositoryCustomImpl implements TagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findTagNamesByLastUsedBackgroundImage(Long userId) {
        // 마지막으로 생성한 이미지 ID 조회
        Long lastUsedBackgroundImageId = queryFactory
                .select(backgroundImage.id)
                .from(backgroundImage)
                .where(backgroundImage.userId.eq(userId))
                .orderBy(backgroundImage.createdAt.desc())
                .limit(1)
                .fetchOne();

        // 생성한 이미지가 없으면 빈 리스트 반환
        if (lastUsedBackgroundImageId == null) {
            return List.of();
        }

        // 태그 이름 리스트 반환
        return queryFactory
                .select(tag.name)
                .from(tag)
                .join(tagBackgroundImage).on(tagBackgroundImage.tagId.eq(tag.id))
                .where(tagBackgroundImage.photoBackgroundId.eq(lastUsedBackgroundImageId))
                .fetch();
    }
}
