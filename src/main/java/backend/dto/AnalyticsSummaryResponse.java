package backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Analytics summary containing counts of various entities")
public class AnalyticsSummaryResponse {

    @Schema(description = "Total number of announcements", example = "10")
    private long totalAnnouncements;

    @Schema(description = "Total number of complaints", example = "150")
    private long totalComplaints;

    public AnalyticsSummaryResponse() {
    }

    public AnalyticsSummaryResponse(long totalAnnouncements, long totalComplaints) {
        this.totalAnnouncements = totalAnnouncements;
        this.totalComplaints = totalComplaints;
    }

    public long getTotalAnnouncements() {
        return totalAnnouncements;
    }

    public void setTotalAnnouncements(long totalAnnouncements) {
        this.totalAnnouncements = totalAnnouncements;
    }

    public long getTotalComplaints() {
        return totalComplaints;
    }

    public void setTotalComplaints(long totalComplaints) {
        this.totalComplaints = totalComplaints;
    }
}
