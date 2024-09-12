package org.chunsik.pq.generate.controller;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.dto.*;
import org.chunsik.pq.generate.exception.UnauthorizedException;
import org.chunsik.pq.generate.service.GenerateService;
import org.chunsik.pq.generate.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class GenerateController {
    private final GenerateService generateService;
    private final TagService tagService;

    @PostMapping("/image")
    public GenerateResponseDTO generateImage(@RequestBody GenerateImageDTO generateImageDTO) throws IOException {
        return generateService.generateImage(generateImageDTO);
    }

    @GetMapping("/image/relate/{id}")
    public List<RelateImageDTO> getRelateImages(@PathVariable Long id) {
        return generateService.getRelateImage(id);
    }

    @PostMapping("/ticket")
    public CreateImageResponseDto createImage(@ModelAttribute GenerateApiRequestDTO dto) throws IOException {
        return generateService.createImage(dto);
    }

    @GetMapping("/ticket/{id}")
    public TicketResponseDTO getTicket(@PathVariable Long id) {
        return generateService.findTicketById(id);
    }

    @GetMapping("/users/tags/last-used")
    public ResponseEntity<List<String>> getLastUsedTags() {
        List<String> lastUsedTags = tagService.getLastUsedTagsForCurrentUser();
        return ResponseEntity.ok(lastUsedTags);
    }

    @GetMapping("/tag")
    public TagResponseDTO getTags() {
        return generateService.currentTags();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}