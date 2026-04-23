package backend.controller;

import backend.dto.ComplaintRequest;
import backend.dto.ComplaintStatusUpdate;
import backend.entity.Complaint;
import backend.entity.User;
import backend.repository.ComplaintRepository;
import backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @Operation(summary = "Create a new complaint")
    public ResponseEntity<?> createComplaint(@ModelAttribute ComplaintRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
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

        return ResponseEntity.ok(complaintRepository.save(complaint));
    }

    @GetMapping
    @Operation(summary = "Get all complaints")
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get complaints by user ID")
    public List<Complaint> getComplaintsByUser(@PathVariable Long userId) {
        return complaintRepository.findByUserId(userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single complaint by ID")
    public ResponseEntity<?> getComplaintById(@PathVariable Long id) {
        return complaintRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update complaint status and remarks")
    public ResponseEntity<?> updateComplaintStatus(@PathVariable Long id, @ModelAttribute ComplaintStatusUpdate update) {
        Optional<Complaint> complaintOpt = complaintRepository.findById(id);
        if (complaintOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Complaint complaint = complaintOpt.get();
        if (update.getStatus() != null) {
            complaint.setStatus(update.getStatus());
        }
        if (update.getRemarks() != null) {
            complaint.setRemarks(update.getRemarks());
        }

        return ResponseEntity.ok(complaintRepository.save(complaint));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a complaint")
    public ResponseEntity<?> deleteComplaint(@PathVariable Long id) {
        if (!complaintRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        complaintRepository.deleteById(id);
        return ResponseEntity.ok("Complaint deleted successfully");
    }
}
