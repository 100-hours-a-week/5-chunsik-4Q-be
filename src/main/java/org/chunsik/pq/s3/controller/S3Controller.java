package org.chunsik.pq.s3.controller;

import org.chunsik.pq.s3.dto.S3UploadDTO;
import org.chunsik.pq.s3.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/s3/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("userId") Long userId,
                                             @RequestParam("categoryId") String categoryId) throws IOException {
        String key = file.getOriginalFilename();

        // 로컬 임시 파일로 변환
        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);

        List<String> tags = new ArrayList<>();
        // S3UploadDTO 객체 생성
        S3UploadDTO s3UploadDTO = new S3UploadDTO(tempFile, key, userId, categoryId,tags);

        // 파일 업로드
        String uploadedUrl = s3Service.uploadFile(s3UploadDTO);

        return ResponseEntity.ok("File uploaded successfully: " + uploadedUrl);
    }

    @GetMapping("/s3/download")
    public ResponseEntity<String> downloadFile(@RequestParam("fileName") String fileName) {
        // 사용자의 홈 디렉토리 경로를 가져옴
        String home = System.getProperty("user.home");
        // 다운로드 폴더 경로 생성
        String downloadPath = home + "/Downloads/" + fileName;

        // 파일 다운로드
        s3Service.downloadFile(fileName, downloadPath);

        return ResponseEntity.ok("File downloaded successfully: " + fileName);
    }
}