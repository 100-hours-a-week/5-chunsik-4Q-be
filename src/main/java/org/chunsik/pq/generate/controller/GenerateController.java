package org.chunsik.pq.generate.controller;

import io.sentry.Sentry;
import org.chunsik.pq.generate.dto.GenerateImageDTO;
import org.chunsik.pq.generate.dto.GenerateResponseDTO;
import org.chunsik.pq.generate.service.GenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class GenerateController {

    @Autowired
    private GenerateService generateService;

    @PostMapping("/generate")
    public GenerateResponseDTO generateImage(@RequestBody GenerateImageDTO generateImageDTO) throws IOException {
        return generateService.generateAndUploadImage(generateImageDTO);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        Sentry.captureException(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}