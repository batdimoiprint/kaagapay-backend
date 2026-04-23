package backend.dto;

import java.time.LocalDateTime;

public class ComplaintRequest {
    private Long userId;
    private String complaintType;
    private LocalDateTime dateOfIncident;
    private String description;
    private String location;

    public ComplaintRequest() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public LocalDateTime getDateOfIncident() {
        return dateOfIncident;
    }

    public void setDateOfIncident(LocalDateTime dateOfIncident) {
        this.dateOfIncident = dateOfIncident;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
