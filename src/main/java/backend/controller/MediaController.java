package backend.controller;

import backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@Tag(name = "Media Uploads", description = "Endpoints for uploading images and videos to Cloudinary")
public class MediaController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Operation(summary = "Upload an image", description = "Endpoint to upload an image from App Inventor or Swagger")
    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadImage(
            @Parameter(description = "Image file to upload", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        try {
            String url = cloudinaryService.uploadImage(file.getBytes());
            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(summary = "Upload a video", description = "Endpoint to upload a video from App Inventor or Swagger")
    @PostMapping(value = "/upload/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadVideo(
            @Parameter(description = "Video file to upload", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        try {
            String url = cloudinaryService.uploadVideo(file.getBytes());
            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(summary = "Get a media URL", description = "Endpoint to retrieve a Cloudinary media URL by its public ID")
    @GetMapping("/{publicId}")
    public ResponseEntity<Map<String, String>> getMediaUrl(
            @Parameter(description = "The public ID of the media asset") @PathVariable String publicId,
            @Parameter(description = "The resource type (image or video)") @RequestParam(defaultValue = "image") String resourceType) {
        String url = cloudinaryService.getMediaUrl(publicId, resourceType);
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        return ResponseEntity.ok(response);
    }
}
