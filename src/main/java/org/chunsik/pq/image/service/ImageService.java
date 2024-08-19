package org.chunsik.pq.image.service;

import lombok.RequiredArgsConstructor;

import org.chunsik.pq.image.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository repository;

    public List<Object[]> getTopPhotoBackgrounds() {
        return repository.findTopPhotoBackgrounds();
    }
}