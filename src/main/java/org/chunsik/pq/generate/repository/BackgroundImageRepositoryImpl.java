package org.chunsik.pq.generate.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.gallery.dto.BackgroundImageDTO;
import org.chunsik.pq.gallery.model.GallerySort;
import org.chunsik.pq.generate.dto.RelateImageDTO;
import org.chunsik.pq.generate.model.QBackgroundImage;
import org.chunsik.pq.generate.model.QTagBackgroundImage;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static org.chunsik.pq.gallery.model.QUserLike.userLike;
import static org.chunsik.pq.generate.model.QBackgroundImage.backgroundImage;
import static org.chunsik.pq.generate.model.QCategory.category;
import static org.chunsik.pq.generate.model.QTag.tag;
import static org.chunsik.pq.generate.model.QTagBackgroundImage.tagBackgroundImage;
import static org.chunsik.pq.login.model.QUser.user;

@RequiredArgsConstructor
@Repository
public class BackgroundImageRepositoryImpl implements BackgroundImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final UserManager userManager;

    @Override
    public Page<BackgroundImageDTO> findByTagAndCategory(String tagName, String categoryName, GallerySort sort, Pageable pageable) {
        // 현재 로그인된 사용자 ID 가져오기
        Optional<CustomUserDetails> currentUser = userManager.currentUser();
        Long currentUserId = currentUser.map(CustomUserDetails::getId).orElse(null);

        // GROUP_CONCAT 결과를 사용하여 태그들을 하나의 문자열로 결합
        StringExpression tagsConcat = stringTemplate("group_concat(DISTINCT {0})", tag.name);

        // 태그 및 카테고리로 이미지를 필터링하여 ID를 가져옴
        Set<Long> filteredImageIds = queryFactory
                .select(backgroundImage.id)
                .from(backgroundImage)
                .leftJoin(tagBackgroundImage).on(tagBackgroundImage.photoBackgroundId.eq(backgroundImage.id))
                .leftJoin(tag).on(tag.id.eq(tagBackgroundImage.tagId))
                .leftJoin(category).on(backgroundImage.categoryId.eq(category.id))
                .where(
                        tagNameEq(tagName),
                        categoryNameEq(categoryName)
                )
                .fetch()
                .stream()
                .collect(Collectors.toSet());  // 중복 제거를 위해 Set으로 변환

        long total = filteredImageIds.size();

        // 필터링된 이미지와 관련된 모든 태그를 다시 조회
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
                .leftJoin(tag).on(tag.id.eq(tagBackgroundImage.tagId))
                .leftJoin(user).on(user.id.eq(backgroundImage.userId))
                .leftJoin(userLike).on(userLike.photoBackgroundId.eq(backgroundImage.id))
                .where(
                        backgroundImage.id.in(filteredImageIds)
                )
                .groupBy(backgroundImage.id, category.name, user.nickname)
                .orderBy(sort == GallerySort.POPULAR ? userLike.countDistinct().desc() : backgroundImage.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Tuple을 BackgroundImageDTO로 변환
        List<BackgroundImageDTO> mappedResults = results.stream().map(tuple -> {
            String tagsString = tuple.get(4, String.class);  // 4번째 필드(태그)를 가져옴
            List<String> tagsList = tagsString != null ? List.of(tagsString.split(",")) : List.of();

            Long photoBackgroundId = tuple.get(backgroundImage.id);

            // 좋아요 여부 확인을 위한 쿼리 실행
            Boolean isLiked = currentUserId != null && queryFactory
                    .selectOne()
                    .from(userLike)
                    .where(
                            userLike.userId.eq(currentUserId)
                                    .and(userLike.photoBackgroundId.eq(photoBackgroundId))
                    )
                    .fetchFirst() != null;  // 레코드가 존재하면 true, 아니면 false

            return new BackgroundImageDTO(
                    tuple.get(backgroundImage.id),
                    tuple.get(backgroundImage.url),
                    tuple.get(backgroundImage.createdAt),
                    tuple.get(category.name),
                    tagsList,
                    tuple.get(user.nickname),
                    tuple.get(userLike.countDistinct()),
                    isLiked
            );
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

    public List<BackgroundImageDTO> findLikedBackgroundImagesWithoutPagination() {
        // 현재 로그인된 사용자 ID 가져오기
        Optional<CustomUserDetails> currentUser = userManager.currentUser();
        Long currentUserId = currentUser.map(CustomUserDetails::getId).orElse(null);

        // 사용자 ID가 없을 경우 빈 결과 반환
        if (currentUserId == null) {
            return List.of();  // 빈 리스트 반환
        }

        // GROUP_CONCAT 결과를 사용하여 태그들을 하나의 문자열로 결합
        StringExpression tagsConcat = stringTemplate("group_concat(DISTINCT {0})", tag.name);

        // 현재 사용자(userId)가 좋아요를 누른 배경 이미지 ID들을 가져옴
        Set<Long> likedImageIds = queryFactory
                .select(userLike.photoBackgroundId)
                .from(userLike)
                .where(userLike.userId.eq(currentUserId))
                .fetch()
                .stream()
                .collect(Collectors.toSet());

        // 좋아요를 누른 배경 이미지와 관련된 정보를 다시 조회
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
                .leftJoin(tag).on(tag.id.eq(tagBackgroundImage.tagId))
                .leftJoin(user).on(user.id.eq(backgroundImage.userId))
                .leftJoin(userLike).on(userLike.photoBackgroundId.eq(backgroundImage.id))
                .where(
                        backgroundImage.id.in(likedImageIds)
                )
                .groupBy(backgroundImage.id, category.name, user.nickname)
                .fetch();

        // Tuple을 BackgroundImageDTO로 변환
        return results.stream().map(tuple -> {
            String tagsString = tuple.get(4, String.class);  // 4번째 필드(태그)를 가져옴
            List<String> tagsList = tagsString != null ? List.of(tagsString.split(",")) : List.of();

            // 좋아요 여부는 이미 사용자 ID를 통해 필터링된 결과이므로 true로 설정
            Boolean isLiked = true;

            return new BackgroundImageDTO(
                    tuple.get(backgroundImage.id),
                    tuple.get(backgroundImage.url),
                    tuple.get(backgroundImage.createdAt),
                    tuple.get(category.name),
                    tagsList,
                    tuple.get(user.nickname),
                    tuple.get(userLike.countDistinct()),
                    isLiked  // 항상 좋아요가 true인 상태
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<RelateImageDTO> findRelateImgByTags(@Param("tagIds") List<Long> tagIds, Long backgroundImgId) {
        QBackgroundImage bi = new QBackgroundImage("bi");
        QTagBackgroundImage tbi = new QTagBackgroundImage("tbi");
        QTagBackgroundImage whole = new QTagBackgroundImage("whole");

        return queryFactory
                .select(Projections.constructor(RelateImageDTO.class, bi.id, bi.url))
                .from(bi)
                .join(tbi).on(bi.id.eq(tbi.photoBackgroundId).and(tbi.tagId.in(tagIds)))
                .join(whole).on(whole.photoBackgroundId.eq(bi.id))
                .where(bi.id.ne(backgroundImgId))
                .groupBy(bi.id)
                .orderBy(tbi.tagId.countDistinct().desc(), whole.tagId.countDistinct().asc())
                .limit(8)
                .fetch();
    }
}