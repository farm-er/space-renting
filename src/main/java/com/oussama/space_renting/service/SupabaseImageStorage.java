package com.oussama.space_renting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

public class SupabaseImageStorage {


    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = generateFileName(file.getName());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return getPublicUrl(fileName);
    }

    public String getPublicUrl(String fileName) {
        String baseUrl = endpoint.replace("/storage/v1/s3", "");
        return String.format("%s/storage/v1/object/public/%s/%s", baseUrl, bucketName, fileName);
    }

    private String generateFileName(String originalFilename) {
        if (originalFilename == null) {
            return UUID.randomUUID().toString() + ".jpg";
        }

        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        return UUID.randomUUID().toString() + extension;
    }

    @Autowired
    private S3Client s3Client;

    @Value("${supabase.storage.bucket}")
    private String bucketName;

    @Value("${supabase.storage.endpoint}")
    private String endpoint;
}
