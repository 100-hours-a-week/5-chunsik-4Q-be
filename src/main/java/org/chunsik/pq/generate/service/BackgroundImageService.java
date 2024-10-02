package org.chunsik.pq.generate.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.gallery.dto.BackgroundImageDTO;
import org.chunsik.pq.gallery.model.GallerySort;
import org.chunsik.pq.generate.repository.BackgroundImageRepository;
import org.chunsik.pq.generate.repository.BackgroundImageRepositoryImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BackgroundImageService {

    private final BackgroundImageRepository backgroundImageRepository;
    private final BackgroundImageRepositoryImpl backgroundImageRepositoryimpl;
    public Page<BackgroundImageDTO> getBackgroundImages(String tagName, String categoryName, GallerySort sort, Pageable pageable) {
        return backgroundImageRepository.findByTagAndCategory(tagName, categoryName, sort, pageable);
    }

    public List<BackgroundImageDTO> getLikedBackgroundImages() {
        return backgroundImageRepositoryimpl.findLikedBackgroundImagesWithoutPagination();
    }
}
