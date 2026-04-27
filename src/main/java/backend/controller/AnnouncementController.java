package backend.controller;

import backend.dto.AnnouncementRequest;
import backend.dto.AnnouncementStatusUpdate;
import backend.entity.Announcement;
import backend.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcement")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Announcements", description = "Endpoints for managing system announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    @Operation(summary = "Get all announcements")
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get announcement by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Announcement found"),
        @ApiResponse(responseCode = "404", description = "Announcement not found")
    })
    public ResponseEntity<Announcement> getAnnouncementById(@PathVariable Long id) {
        return announcementService.getAnnouncementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new announcement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Announcement created successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Announcement> createAnnouncement(@ModelAttribute AnnouncementRequest request) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Announcement created = announcementService.createAnnouncement(request, currentUsername);
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update announcement status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Announcement not found")
    })
    public ResponseEntity<Announcement> updateAnnouncementStatus(
            @PathVariable Long id, 
            @ModelAttribute AnnouncementStatusUpdate updateRequest) {
        
        return announcementService.updateStatus(id, updateRequest.getStatus())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an announcement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Announcement deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Announcement not found")
    })
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        boolean deleted = announcementService.deleteAnnouncement(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
