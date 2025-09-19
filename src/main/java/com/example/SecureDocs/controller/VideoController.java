package com.example.SecureDocs.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/stream")
public class VideoController {

    private static final long CHUNK_SIZE = 1024 * 1024; // 1MB chunk size

    @Value("${video.path}")
    private String videoPath;

    @GetMapping("/videos/{fileName}")
    public ResponseEntity<ResourceRegion> streamVideo(
            @PathVariable String fileName,
            @RequestHeader HttpHeaders headers) throws IOException {

        Path filePath = Paths.get(videoPath).resolve(fileName);
        Resource videoResource = new UrlResource(filePath.toUri());

        long contentLength = videoResource.contentLength();
        Optional<ResourceRegion> region = headers.getRange()
                .stream()
                .map(range -> {
                    long start = range.getRangeStart(contentLength);
                    long end = range.getRangeEnd(contentLength);
                    long length = Math.min(CHUNK_SIZE, end - start + 1);
                    return new ResourceRegion(videoResource, start, length);
                })
                .findFirst();

        ResourceRegion resourceRegion = region.orElseGet(() -> {
            long length = Math.min(CHUNK_SIZE, contentLength);
            return new ResourceRegion(videoResource, 0, length);
        });

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resourceRegion);
    }
}