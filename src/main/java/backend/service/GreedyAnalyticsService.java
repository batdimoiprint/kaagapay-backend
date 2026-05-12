package backend.service;

import backend.dto.GreedyAnalyticsResponse;
import backend.entity.Complaint;
import backend.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GreedyAnalyticsService {

    @Autowired
    private ComplaintRepository complaintRepository;

    /**
     * Generates analytics using a Greedy approach (single-pass efficient processing).
     * Time Complexity: O(n) where n is the number of complaints.
     */
    public GreedyAnalyticsResponse generateGreedyAnalytics() {
        List<Complaint> complaints = complaintRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        long monthlyTotal = 0;
        long semiAnnualTotal = 0;
        long annualTotal = 0;

        Map<String, Long> typeFrequency = new HashMap<>();
        Map<String, Long> monthlyCounts = new HashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // Greedy single-pass processing: Process each record and update all statistics immediately
        for (Complaint complaint : complaints) {
            LocalDateTime incidentDate = complaint.getDateOfIncident();
            if (incidentDate == null) continue;

            // 1. Time-based Bucketing (Greedy selection of relevant records)
            if (incidentDate.isAfter(now.minusMonths(1))) {
                monthlyTotal++;
            }
            if (incidentDate.isAfter(now.minusMonths(6))) {
                semiAnnualTotal++;
            }
            if (incidentDate.isAfter(now.minusYears(1))) {
                annualTotal++;
            }

            // 2. Frequency Tracking for Trends
            String type = complaint.getComplaintType();
            if (type != null) {
                typeFrequency.put(type, typeFrequency.getOrDefault(type, 0L) + 1);
            }

            // 3. Temporal Tracking for Peak Period
            String monthKey = incidentDate.format(monthFormatter);
            monthlyCounts.put(monthKey, monthlyCounts.getOrDefault(monthKey, 0L) + 1);
        }

        // Greedy Selection: Pick the top 5 incident types
        List<Map.Entry<String, Long>> topTypes = typeFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Greedy Selection: Pick the peak month (locally optimal choice for peak period)
        Map.Entry<String, Long> peakEntry = monthlyCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(new AbstractMap.SimpleEntry<>("N/A", 0L));

        GreedyAnalyticsResponse.PeakPeriod peakPeriod = new GreedyAnalyticsResponse.PeakPeriod(
                peakEntry.getKey(), peakEntry.getValue()
        );

        return new GreedyAnalyticsResponse(
                monthlyTotal,
                semiAnnualTotal,
                annualTotal,
                topTypes,
                peakPeriod
        );
    }
}
