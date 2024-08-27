package org.chunsik.pq.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.mypage.dto.MyPQResponseDto;
import org.chunsik.pq.mypage.service.MyPageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/myPQ")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    public ResponseEntity<List<MyPQResponseDto>> getMyPQ() {
        List<MyPQResponseDto> MyPQ = myPageService.getMyPQs();
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }
}
