package org.chunsik.pq.shortenurl.controller;


import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.shortenurl.dto.RequestConvertUrlDTO;
import org.chunsik.pq.shortenurl.dto.ResponseConvertUrlDTO;
import org.chunsik.pq.shortenurl.exception.ErrorCode;
import org.chunsik.pq.shortenurl.exception.ErrorResponse;
import org.chunsik.pq.shortenurl.exception.URLConvertReachTheLimitException;
import org.chunsik.pq.shortenurl.service.ShortenUrlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ShortenUrlController {

    private final ShortenUrlService shortenUrlService;

    @PostMapping("/short")
    public ResponseConvertUrlDTO urlConvert(@RequestBody @Valid RequestConvertUrlDTO requestConvertUrlDTO) {
        String url = requestConvertUrlDTO.getSrcUrl();
        return shortenUrlService.convertToShortUrl(url);
    }

    @GetMapping("/s/{dest_url}")
    public ResponseEntity<?> responseShortenUrl(@PathVariable String dest_url) throws URISyntaxException {
        String srcUrl = shortenUrlService.responseShortUrl(dest_url);
        URI sourceURL = new URI(srcUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(sourceURL);

        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleURLNotMatchFormatException() {
        return ResponseEntity.status(ErrorCode.FORMAT_NOT_MATCH.getStatus()).body(new ErrorResponse(ErrorCode.FORMAT_NOT_MATCH.getStatus(), ErrorCode.FORMAT_NOT_MATCH.getMessage()));
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<?> handleURLNotMatchSyntaxException() {
        return ResponseEntity.status(ErrorCode.SYNTAX_NOT_MATCH.getStatus()).body(new ErrorResponse(ErrorCode.SYNTAX_NOT_MATCH.getStatus(), ErrorCode.SYNTAX_NOT_MATCH.getMessage()));
    }

    @ExceptionHandler(URLConvertReachTheLimitException.class)
    public ResponseEntity<?> handleURLConvertReachTheLimitException(URLConvertReachTheLimitException ex) {
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(new ErrorResponse(ex.getErrorCode().getStatus(), ex.getErrorCode().getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        Sentry.captureException(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
