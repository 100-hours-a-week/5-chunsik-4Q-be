package org.chunsik.pq.mypage.controller;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.mypage.dto.MyPQResponseDto;
import org.chunsik.pq.mypage.service.MyPageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/myPQ")
public class MyPageController {

    private final MyPageService myPageService;

    // 내가 만든 PQ
    @GetMapping
    public ResponseEntity<List<MyPQResponseDto>> getMyPQ() {
        List<MyPQResponseDto> MyPQ = myPageService.getMyPQs();
        return ResponseEntity.ok(MyPQ);
    }

    // 제목으로 필터링
    @GetMapping("/title")
    public ResponseEntity<List<MyPQResponseDto>> searchMyPQByTitle(@RequestParam String title) {
        List<MyPQResponseDto> MyPQ = myPageService.getMyPQsByTitle(title);
        return ResponseEntity.ok(MyPQ);
    }

    // 태그로 필터링
    @GetMapping("/tag")
    public ResponseEntity<List<MyPQResponseDto>> searchMyPQByTag(@RequestParam String tag) {
        List<MyPQResponseDto> MyPQ = myPageService.getMyPQsByTag(tag);
        return ResponseEntity.ok(MyPQ);
    }


    // IllegalStateException을 처리하는 핸들러
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        // 예외 메시지와 함께 400 Bad Request 응답을 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // 일반적인 예외를 처리하는 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        // 예외 메시지와 함께 500 Internal Server Error 응답을 반환
        Sentry.captureException(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }
}
