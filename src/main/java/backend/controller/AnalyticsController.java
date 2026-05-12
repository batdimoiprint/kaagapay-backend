package backend.controller;

import backend.dto.AnalyticsSummaryResponse;
import backend.repository.AnnouncementRepository;
import backend.repository.ComplaintRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@Tag(name = "Analytics", description = "Endpoints for retrieving system analytics and statistics")
public class AnalyticsController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private backend.service.GreedyAnalyticsService greedyAnalyticsService;

    @Operation(summary = "Get total counts for announcements and complaints")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved counts")
    @GetMapping("/summary")
    public ResponseEntity<backend.dto.AnalyticsSummaryResponse> getAnalyticsSummary() {
        long totalAnnouncements = announcementRepository.count();
        long totalComplaints = complaintRepository.count();

        backend.dto.AnalyticsSummaryResponse summary = new backend.dto.AnalyticsSummaryResponse(totalAnnouncements, totalComplaints);
        
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Get analytics report generated using Greedy Algorithm", 
               description = "Efficiently processes incident data into monthly, semi-annual, and annual summaries.")
    @ApiResponse(responseCode = "200", description = "Successfully generated greedy analytics report")
    @GetMapping("/greedy-report")
    public ResponseEntity<backend.dto.GreedyAnalyticsResponse> getGreedyReport() {
        return ResponseEntity.ok(greedyAnalyticsService.generateGreedyAnalytics());
    }
}
