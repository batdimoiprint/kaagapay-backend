package backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ComplaintStatusUpdate {
    @Schema(description = "New status of the complaint", example = "IN_PROGRESS", allowableValues = {"PENDING", "IN_PROGRESS", "RESOLVED", "REJECTED"})
    private String status;

    public ComplaintStatusUpdate() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
