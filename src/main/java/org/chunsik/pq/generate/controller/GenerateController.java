package org.chunsik.pq.generate.controller;

import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.dto.*;
import org.chunsik.pq.generate.model.RequestLimitResponse;
import org.chunsik.pq.generate.service.GenerateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class GenerateController {
    private final GenerateService generateService;

    @PostMapping("/image")
    public ResponseEntity<GenerateResponseDTO> generateImage(@CookieValue(value = "uuid", required = false) String uuid, HttpServletResponse response, @RequestBody GenerateImageDTO generateImageDTO) throws IOException {
        RequestLimitResponse requestResponse = generateService.canCreateImage(uuid, response);
        switch (requestResponse) {
            case CLIENT_LIMIT_REACHED:
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
            case SERVER_LIMIT_REACHED:
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
            default:
                GenerateResponseDTO generateResponseDTO = generateService.generateImage(generateImageDTO);
                return ResponseEntity.status(HttpStatus.OK).body(generateResponseDTO);
        }
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