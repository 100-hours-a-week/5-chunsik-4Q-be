package org.chunsik.pq.gallery.controller;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.gallery.dto.BackgroundImageDTO;
import org.chunsik.pq.gallery.exception.InvalidGallerySortException;
import org.chunsik.pq.gallery.model.GallerySort;
import org.chunsik.pq.gallery.service.GalleryService;
import org.chunsik.pq.generate.service.BackgroundImageService;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/gallery")
public class GalleryController {

    private final GalleryService galleryService;
    private final BackgroundImageService backgroundImageService;

    // 갤러리 이미지 요청
    @GetMapping
    public Page<BackgroundImageDTO> getBackgroundImages(
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String categoryName,
            @RequestParam(defaultValue = "LATEST") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size) {

        Pageable pageable = PageRequest.of(page, size);
        GallerySort gallerySort = GallerySort.fromValue(sort);
        return backgroundImageService.getBackgroundImages(tagName, categoryName, gallerySort, pageable);
    }

    // 좋아요 추가 요청
    @PostMapping("/{imageId}/like")
    public ResponseEntity<Map<String, String>> addLike(@PathVariable Long imageId) {
        galleryService.addLike(imageId);
        return ResponseEntity.ok(Map.of(
                "message", "Like added successfully"
        ));
    }

    // 좋아요 제거 요청
    @DeleteMapping("/{imageId}/like")
    public ResponseEntity<Map<String, String>> removeLike(@PathVariable Long imageId) {
        galleryService.removeLike(imageId);
        return ResponseEntity.ok(Map.of(
                "message", "Like removed successfully"
        ));
    }

    // DataIntegrityViolationException 예외 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Like already exists");
    }

    // MissingServletRequestParameterException 예외 처리
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException e) {
        String name = e.getParameterName();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "message", name + " parameter is missing"
        ));
    }

    // InvalidGallerySortException 예외 처리
    @ExceptionHandler(InvalidGallerySortException.class)
    public ResponseEntity<String> handleInvalidGallerySort(InvalidGallerySortException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

}
