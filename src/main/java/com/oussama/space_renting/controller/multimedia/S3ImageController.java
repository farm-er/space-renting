package com.oussama.space_renting.controller.multimedia;

import com.oussama.space_renting.service.SupabaseImageStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/s3/images")
public class S3ImageController {



    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File is empty"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File must be an image"));
            }

            // Validate file size (max 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File size must be less than 10MB"));
            }

            String imageUrl = supabaseImageStorage.uploadImage(file);

            return ResponseEntity.ok(Map.of(
                    "url", imageUrl,
                    "message", "Image uploaded successfully",
                    "fileName", imageUrl.substring(imageUrl.lastIndexOf("/") + 1),
                    "size", file.getSize(),
                    "contentType", contentType
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }

    @GetMapping("/url/{id}")
    public ResponseEntity<Map<String, String>> getImageUrl(@PathVariable UUID id) {
        try {

            String url = supabaseImageStorage.getPublicUrl(id.toString());
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get image URL"));
        }
    }


    @Autowired
    private SupabaseImageStorage supabaseImageStorage;

}


//    @DeleteMapping("/{fileName}")
//    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable String fileName) {
//        try {
//            if (!supabaseS3Service.imageExists(fileName)) {
//                return ResponseEntity.notFound()
//                        .body(Map.of("error", "Image not found"));
//            }
//
//            supabaseS3Service.deleteImage(fileName);
//            return ResponseEntity.ok(Map.of("message", "Image deleted successfully"));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Failed to delete image: " + e.getMessage()));
//        }
//    }
