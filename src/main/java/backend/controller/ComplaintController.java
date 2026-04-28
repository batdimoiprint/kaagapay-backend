package backend.controller;

import backend.dto.ComplaintRequest;
import backend.dto.ComplaintStatusUpdate;
import backend.dto.RemarkRequest;
import backend.entity.Complaint;
import backend.entity.User;
import backend.model.Remark;
import backend.repository.ComplaintRepository;
import backend.repository.UserRepository;
import backend.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/complaints")
@Tag(name = "Complaints", description = "Endpoints for managing user complaints")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private backend.service.PushyService pushyService;

    @Autowired
    private backend.service.CloudinaryService cloudinaryService;

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String jwt = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        } else if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        if (jwt != null) {
            return jwtService.extractUserId(jwt);
        }
        return null;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new complaint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaint created successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request. User not found or invalid complaint data.")
    })
    public ResponseEntity<?> createComplaint(
            @ModelAttribute ComplaintRequest request,
            @Parameter(description = "Optional photo attachment", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @Parameter(description = "Optional video attachment", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
            @RequestParam(value = "video", required = false) MultipartFile video,
            HttpServletRequest httpRequest) {
        Long userId = extractUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Complaint complaint = new Complaint();
        complaint.setComplaintType(request.getComplaintType());
        complaint.setDateOfIncident(request.getDateOfIncident());
        complaint.setDescription(request.getDescription());
        complaint.setLocation(request.getLocation());
        complaint.setStatus("PENDING");
        complaint.setUser(userOpt.get());

        try {
            if (photo != null && !photo.isEmpty()) {
                complaint.getPictureUrl().add(cloudinaryService.uploadImage(photo.getBytes()));
            }
            if (video != null && !video.isEmpty()) {
                complaint.getVideoUrl().add(cloudinaryService.uploadVideo(video.getBytes()));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload media: " + e.getMessage());
        }

        return ResponseEntity.ok(complaintRepository.save(complaint));
    }

    @GetMapping
    @Operation(summary = "Get all complaints")
    @ApiResponse(responseCode = "200", description = "Returns a list of all complaints")
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    @GetMapping("-complaints")
    @Operation(summary = "Get complaints filed by current user")
    @ApiResponse(responseCode = "200", description = "Returns a list of complaints for the logged-in user")
    public ResponseEntity<?> getMyComplaints(HttpServletRequest httpRequest) {
        Long userId = extractUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
        }
        return ResponseEntity.ok(complaintRepository.findByUser_Id(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single complaint by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaint found and returned"),
        @ApiResponse(responseCode = "404", description = "Complaint not found for given ID")
    })
    public ResponseEntity<?> getComplaintById(@PathVariable Long id) {
        return complaintRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update complaint status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Complaint not found for given ID")
    })
    public ResponseEntity<?> updateComplaintStatus(@PathVariable Long id, @ModelAttribute ComplaintStatusUpdate update) {
        Optional<Complaint> complaintOpt = complaintRepository.findById(id);
        if (complaintOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Complaint complaint = complaintOpt.get();
        String oldStatus = complaint.getStatus();
        if (update.getStatus() != null) {
            complaint.setStatus(update.getStatus());
        }

        Complaint savedComplaint = complaintRepository.save(complaint);

        // Send push notification to the user who filed the complaint
        if (savedComplaint.getUser() != null && update.getStatus() != null && !update.getStatus().equals(oldStatus)) {
            String message = "Your complaint #" + id + " status has been updated to " + update.getStatus();
            pushyService.sendPushNotificationToUser(savedComplaint.getUser(), message);
        }

        return ResponseEntity.ok(savedComplaint);
    }

    @PatchMapping("/{id}/remarks")
    @Operation(summary = "Add a remark to a complaint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Remark added successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Complaint not found")
    })
    public ResponseEntity<?> addRemark(@PathVariable Long id, @ModelAttribute RemarkRequest request, HttpServletRequest httpRequest) {
        Long userId = extractUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
        }

        Optional<Complaint> complaintOpt = complaintRepository.findById(id);
        if (complaintOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Complaint complaint = complaintOpt.get();
        Remark remark = new Remark(LocalDateTime.now(), request.getRemarkDescription(), userId);
        complaint.getRemarks().add(remark);

        return ResponseEntity.ok(complaintRepository.save(complaint));
    }

    @PatchMapping(value = "/{id}/attachMedia", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Attach media (image or video) to a complaint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Media attached successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid media type"),
        @ApiResponse(responseCode = "404", description = "Complaint not found"),
        @ApiResponse(responseCode = "500", description = "Server error during media upload")
    })
    public ResponseEntity<?> attachMedia(@PathVariable Long id,
                                         @RequestParam("file") MultipartFile file,
                                         @RequestParam("mediaType") String mediaType) {
        Optional<Complaint> complaintOpt = complaintRepository.findById(id);
        if (complaintOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Complaint complaint = complaintOpt.get();
        try {
            String url;
            if ("image".equalsIgnoreCase(mediaType)) {
                url = cloudinaryService.uploadImage(file.getBytes());
                complaint.getPictureUrl().add(url);
            } else if ("video".equalsIgnoreCase(mediaType)) {
                url = cloudinaryService.uploadVideo(file.getBytes());
                complaint.getVideoUrl().add(url);
            } else {
                return ResponseEntity.badRequest().body("Invalid mediaType. Must be 'image' or 'video'.");
            }

            Complaint saved = complaintRepository.save(complaint);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading media: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a complaint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaint deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Complaint not found for given ID")
    })
    public ResponseEntity<?> deleteComplaint(@PathVariable Long id) {
        if (!complaintRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        complaintRepository.deleteById(id);
        return ResponseEntity.ok("Complaint deleted successfully");
    }
}
