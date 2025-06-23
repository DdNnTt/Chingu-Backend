package com.chingubackend.controller;

import com.chingubackend.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumImageUploadController {

    private final S3Service s3Service;

    @GetMapping("/upload-url")
    public ResponseEntity<Map<String, String>> getAlbumUploadUrl(
            @RequestParam(defaultValue = "jpg") String extension,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        String ext = extension.startsWith(".") ? extension : "." + extension;
        String key = "albums/" + userId + "/" + UUID.randomUUID() + ext;

        URL uploadUrl = s3Service.generatePreSignedUrl(key);
        String fileUrl = s3Service.getFileUrl(key);

        return ResponseEntity.ok(Map.of(
                "uploadUrl", uploadUrl.toString(),
                "fileUrl", fileUrl
        ));
    }
}