package org.chunsik.pq.gallery.controller;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.gallery.exception.UserLikeNotFoundException;
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
    @GetMapping("/images")
    public Page<Map<String, Object>> getBackgroundImages(
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String categoryName,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return backgroundImageService.getBackgroundImages(tagName, categoryName, sort, pageable);
    }

    // 좋아요 추가 요청
    @PostMapping("/like")
    public ResponseEntity<Map<String, String>> addLike(@RequestBody Map<String, Long> request) {
        Long photoBackgroundId = request.get("imageId");
        galleryService.addLike(photoBackgroundId);
        return ResponseEntity.ok(Map.of(
                "message", "Like added successfully"
        ));
    }

    // 좋아요 삭제 요청
    @DeleteMapping("/like")
    public ResponseEntity<Map<String, String>> removeLike(@RequestParam("imageId") Long imageId) {
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

    // LikeNotFoundException 예외 처리
    @ExceptionHandler(UserLikeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleLikeNotFoundException(UserLikeNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", e.getMessage()
        ));
    }

    // MissingServletRequestParameterException 예외 처리
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException e) {
        String name = e.getParameterName();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "message", name + " parameter is missing"
        ));
    }

    // 일반적인 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "Internal server error: " + e.getMessage()
        ));
    }
}
