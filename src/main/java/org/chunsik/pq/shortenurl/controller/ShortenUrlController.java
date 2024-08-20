package org.chunsik.pq.shortenurl.controller;


import lombok.RequiredArgsConstructor;
import org.chunsik.pq.shortenurl.dto.RequestConvertUrlDTO;
import org.chunsik.pq.shortenurl.dto.ResponseConvertUrlDTO;
import org.chunsik.pq.shortenurl.service.ShortenUrlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ShortenUrlController {

    private final ShortenUrlService shortenUrlService;

    @PostMapping("/short")
    public ResponseConvertUrlDTO urlConvert(@RequestBody RequestConvertUrlDTO requestConvertUrlDTO) {
        return shortenUrlService.convertToShortUrl(requestConvertUrlDTO);
    }

    @GetMapping("/{dest_url}")
    public ResponseEntity<?> responseShortenUrl(@PathVariable String dest_url) throws URISyntaxException {
        String srcUrl = shortenUrlService.responseShortUrl(dest_url);
        URI redirectUri = new URI(srcUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);

        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
    }
}
