package org.chunsik.pq.generate.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static org.chunsik.pq.gallery.model.QUserLike.userLike;
import static org.chunsik.pq.generate.model.QBackgroundImage.backgroundImage;
import static org.chunsik.pq.generate.model.QCategory.category;
import static org.chunsik.pq.generate.model.QTag.tag;
import static org.chunsik.pq.generate.model.QTagBackgroundImage.tagBackgroundImage;
import static org.chunsik.pq.model.QUser.user;

@RequiredArgsConstructor
@Repository
public class BackgroundImageRepositoryImpl implements BackgroundImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Map<String, Object>> findByTagAndCategory(String tagName, String categoryName, String sort, Pageable pageable) {
        // GROUP_CONCAT 결과를 사용하여 태그들을 하나의 문자열로 결합
        StringExpression tagsConcat = stringTemplate("group_concat(DISTINCT {0})", tag.name);

        // QueryDSL을 사용해 데이터베이스 쿼리 실행
        List<Tuple> results = queryFactory
                .select(
                        backgroundImage.id,
                        backgroundImage.url,
                        backgroundImage.createdAt,
                        category.name,
                        tagsConcat.as("tags"),
                        user.nickname,
                        userLike.countDistinct()
                )
                .from(backgroundImage)
                .leftJoin(category).on(backgroundImage.categoryId.eq(category.id))
                .leftJoin(tagBackgroundImage).on(tagBackgroundImage.photoBackgroundId.eq(backgroundImage.id))
                .leftJoin(tag).on(tag.id.eq(tagBackgroundImage.tagId))  // 태그와의 JOIN 명확히 설정
                .leftJoin(user).on(user.id.eq(backgroundImage.userId))
                .leftJoin(userLike).on(userLike.photoBackgroundId.eq(backgroundImage.id))
                .where(
                        tagNameEq(tagName),
                        categoryNameEq(categoryName)
                )
                .groupBy(backgroundImage.id, category.name, user.nickname)
                .orderBy(sort.equalsIgnoreCase("popular") ? userLike.countDistinct().desc() : backgroundImage.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 조건에 맞는 전체 레코드 수 계산
        long total = queryFactory
                .selectFrom(backgroundImage)
                .leftJoin(category).on(backgroundImage.categoryId.eq(category.id))
                .leftJoin(tagBackgroundImage).on(tagBackgroundImage.photoBackgroundId.eq(backgroundImage.id))
                .leftJoin(tag).on(tag.id.eq(tagBackgroundImage.tagId))
                .leftJoin(user).on(user.id.eq(backgroundImage.userId))
                .leftJoin(userLike).on(userLike.photoBackgroundId.eq(backgroundImage.id))
                .where(
                        tagNameEq(tagName),
                        categoryNameEq(categoryName)
                )
                .fetchCount();

        // Tuple을 Map<String, Object>로 변환
        List<Map<String, Object>> mappedResults = results.stream().map(tuple -> {
            Map<String, Object> map = new HashMap<>();
            map.put("imageId", tuple.get(backgroundImage.id));
            map.put("url", tuple.get(backgroundImage.url));
            map.put("createdAt", tuple.get(backgroundImage.createdAt));
            map.put("categoryName", tuple.get(category.name));

            String tagsString = tuple.get(4, String.class);  // 4번째 필드(태그)를 가져옴
            List<String> tagsList = tagsString != null ? Arrays.asList(tagsString.split(",")) : new ArrayList<>();
            map.put("tags", tagsList);

            map.put("userName", tuple.get(user.nickname));
            map.put("likeCount", tuple.get(userLike.countDistinct()));
            return map;
        }).collect(Collectors.toList());

        // 변환된 결과를 페이지 객체로 반환
        return new PageImpl<>(mappedResults, pageable, total);
    }

    // 태그 이름에 따른 필터링 조건을 반환
    private BooleanExpression tagNameEq(String tagName) {
        return tagName != null ? tag.name.eq(tagName) : null;
    }

    // 카테고리 이름에 따른 필터링 조건을 반환
    private BooleanExpression categoryNameEq(String categoryName) {
        return categoryName != null ? category.name.eq(categoryName) : null;
    }

}
