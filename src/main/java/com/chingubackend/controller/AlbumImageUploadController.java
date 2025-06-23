package com.chingubackend.controller;

import com.chingubackend.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "앨범 이미지 업로드용 Presigned URL 발급"
    )
    @GetMapping("/{groupId}/upload-url")
    public ResponseEntity<Map<String, String>> getAlbumUploadUrl(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "jpg") String extension) {

        String ext = extension.startsWith(".") ? extension : "." + extension;
        String key = "albums/" + groupId + "/" + UUID.randomUUID() + ext;

        URL uploadUrl = s3Service.generatePreSignedUrl(key);
        String fileUrl = s3Service.getFileUrl(key);

        return ResponseEntity.ok(Map.of(
                "uploadUrl", uploadUrl.toString(),
                "fileUrl", fileUrl
        ));
    }
}