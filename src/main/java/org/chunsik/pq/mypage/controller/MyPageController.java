package org.chunsik.pq.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.mypage.dto.MyPQResponseDto;
import org.chunsik.pq.mypage.service.MyPageService;
import org.springframework.http.ResponseEntity;
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
}
