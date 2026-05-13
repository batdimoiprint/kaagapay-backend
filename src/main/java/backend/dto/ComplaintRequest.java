package backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public class ComplaintRequest {
    @Schema(description = "Choose one supported complaint type (English or Tagalog). This is the primary input used for severity scoring.", example = "Fire Hazard / Open Burning or Panganib ng Apoy")
    private String complaintType;

    @Schema(description = "Date and time of incident (ISO-8601)", example = "2024-04-24T10:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateOfIncident;

    @Schema(description = "Detailed description of the issue", example = "The street light in front of house #123 is not working.")
    private String description;

    @Schema(description = "Location of the incident", example = "123 Main St, Barangay San Jose")
    private String location;

    @Schema(description = "Latitude of the incident location", example = "14.716432646016036")
    private Double lat;

    @Schema(description = "Longitude of the incident location", example = "121.030132611756")
    private Double lng;

    @Schema(description = "Whether an injury was reported", example = "true")
    private String hasInjury;

    @Schema(description = "Whether the incident needs an immediate response", example = "true")
    private String needsImmediateResponse;

    @Schema(description = "Whether property damage was reported", example = "false")
    private String hasPropertyDamage;

    @Schema(description = "Number of people affected by the incident", example = "3")
    private String affectedPeopleCount;

    public ComplaintRequest() {
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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getHasInjury() {
        return hasInjury;
    }

    public void setHasInjury(String hasInjury) {
        this.hasInjury = hasInjury;
    }

    public String getNeedsImmediateResponse() {
        return needsImmediateResponse;
    }

    public void setNeedsImmediateResponse(String needsImmediateResponse) {
        this.needsImmediateResponse = needsImmediateResponse;
    }

    public String getHasPropertyDamage() {
        return hasPropertyDamage;
    }

    public void setHasPropertyDamage(String hasPropertyDamage) {
        this.hasPropertyDamage = hasPropertyDamage;
    }

    public String getAffectedPeopleCount() {
        return affectedPeopleCount;
    }

    public void setAffectedPeopleCount(String affectedPeopleCount) {
        this.affectedPeopleCount = affectedPeopleCount;
    }
}
