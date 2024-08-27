//package org.chunsik.pq.gallery.controller;
//
//import lombok.RequiredArgsConstructor;
//
//import org.chunsik.pq.generate.model.BackgroundImage;
//import org.chunsik.pq.gallery.service.GalleryService;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/gallery")
//public class GalleryController {
//
//    private final GalleryService galleryService;
//
//    @GetMapping("/popular")
//    public ResponseEntity<List<Long>> getTopGeneratedImages() {
//        List<Long> topImages = galleryService.getTopGeneratedImages();
//        return ResponseEntity.ok(topImages);
//    }
//
//}
