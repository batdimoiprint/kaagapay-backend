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

    @Schema(description = "Incident category used for severity scoring", example = "Fire")
    private String incidentType;

    @Schema(description = "Whether an injury was reported", example = "true")
    private Boolean hasInjury;

    @Schema(description = "Whether the incident needs an immediate response", example = "true")
    private Boolean needsImmediateResponse;

    @Schema(description = "Whether property damage was reported", example = "false")
    private Boolean hasPropertyDamage;

    @Schema(description = "Number of people affected by the incident", example = "3")
    private Integer affectedPeopleCount;

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

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public Boolean getHasInjury() {
        return hasInjury;
    }

    public void setHasInjury(Boolean hasInjury) {
        this.hasInjury = hasInjury;
    }

    public Boolean getNeedsImmediateResponse() {
        return needsImmediateResponse;
    }

    public void setNeedsImmediateResponse(Boolean needsImmediateResponse) {
        this.needsImmediateResponse = needsImmediateResponse;
    }

    public Boolean getHasPropertyDamage() {
        return hasPropertyDamage;
    }

    public void setHasPropertyDamage(Boolean hasPropertyDamage) {
        this.hasPropertyDamage = hasPropertyDamage;
    }

    public Integer getAffectedPeopleCount() {
        return affectedPeopleCount;
    }

    public void setAffectedPeopleCount(Integer affectedPeopleCount) {
        this.affectedPeopleCount = affectedPeopleCount;
    }
}
