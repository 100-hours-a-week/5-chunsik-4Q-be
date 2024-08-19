package org.chunsik.pq.image.controller;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.image.dto.ImageResponseDto;
import org.chunsik.pq.image.service.ImageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/images")
public class PopularImageController {

    private final ImageService service;

    @GetMapping("/popular")
    public List<ImageResponseDto> getTopPhotoBackgrounds() {
        return service.getTopPhotoBackgrounds().stream()
                .map(data -> new ImageResponseDto((String) data[0]))
                .collect(Collectors.toList());
    }
}
