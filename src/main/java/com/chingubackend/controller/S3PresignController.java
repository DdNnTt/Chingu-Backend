package com.chingubackend.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import com.amazonaws.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3PresignController {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @GetMapping("/presign")
    public ResponseEntity<?> getPresignedUrl(@RequestParam String fileName) {
        try {
            String key = "album/" + UUID.randomUUID() + "-" + fileName;

            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 5); // 5분
            GeneratePresignedUrlRequest request =
                    new GeneratePresignedUrlRequest(bucket, key)
                            .withMethod(HttpMethod.PUT)
                            .withExpiration(expiration);

            URL presignedUrl = amazonS3.generatePresignedUrl(request);

            return ResponseEntity.ok(Map.of(
                    "uploadUrl", presignedUrl.toString(),
                    "fileUrl", amazonS3.getUrl(bucket, key).toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("presigned URL 생성 실패");
        }
    }
}
