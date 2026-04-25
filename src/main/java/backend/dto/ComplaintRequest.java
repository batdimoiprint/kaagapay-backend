package backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public class ComplaintRequest {
    @Schema(description = "Type of complaint", example = "Street Light Repair")
    private String complaintType;
    
    @Schema(description = "Date and time of incident (ISO-8601)", example = "2024-04-24T10:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateOfIncident;
    
    @Schema(description = "Detailed description of the issue", example = "The street light in front of house #123 is not working.")
    private String description;

    @Schema(description = "Location of the incident", example = "123 Main St, Barangay San Jose")
    private String location;

    public ComplaintRequest() {}

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
