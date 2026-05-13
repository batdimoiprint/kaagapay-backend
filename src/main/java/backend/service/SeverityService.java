package backend.service;

import backend.dto.ComplaintRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class SeverityService {

    public SeverityResult calculateSeverity(ComplaintRequest request, boolean hasMediaEvidence) {
        int score = 0;
        String complaintType = buildSearchableComplaintType(request);

        score = addComplaintTypeScore(score, complaintType, 50,
                "Fire Hazard / Open Burning",
                "Electrical Hazard",
                "Animal Bite / Attack",
                "Missing Person (local report)",
                "Harassment / Threats");

        if (Boolean.TRUE.equals(request.getHasInjury())) {
            score += 35;
        }

        score = addComplaintTypeScore(score, complaintType, 30,
                "Domestic Conflict",
                "Theft / Petty Crime",
                "Suspicious Activity",
                "Public Health Concern",
                "Infrastructure Damage",
                "Tree Obstruction / Fallen Tree");

        if (Boolean.TRUE.equals(request.getNeedsImmediateResponse())) {
            score += 25;
        }
        if (request.getAffectedPeopleCount() != null && request.getAffectedPeopleCount() > 1) {
            score += 20;
        }
        if (Boolean.TRUE.equals(request.getHasPropertyDamage())) {
            score += 15;
        }

        score = addComplaintTypeScore(score, complaintType, 15,
                "Vandalism / Property Damage",
                "Trespassing",
                "Water Leakage / Pipe Issue",
                "Drainage / Flooding",
                "Clogged Canal / Sewer",
                "Road Damage / Potholes",
                "Broken Streetlight",
                "Illegal Construction",
                "Building Code Violation",
                "Pollution (air water noise)",
                "Abandoned Vehicle",
                "Illegal Vendor / Sidewalk Obstruction");

        score = addComplaintTypeScore(score, complaintType, 5,
                "Neighborhood Dispute",
                "Noise Complaint",
                "Public Disturbance",
                "Illegal Parking / Obstruction",
                "Waste / Garbage Issue",
                "Sanitation Problem",
                "Animal Concern",
                "Stray Animals",
                "Noise from Business",
                "Curfew Violation",
                "Ordinance Violation",
                "Parking Dispute",
                "Lost and Found",
                "Other");

        if (hasMediaEvidence) {
            score += 5;
        }

        score += calculateAgePoints(request.getDateOfIncident());

        return new SeverityResult(score, resolveLabel(score));
    }

    private String buildSearchableComplaintType(ComplaintRequest request) {
        String complaintType = request.getComplaintType() != null ? request.getComplaintType() : "";
        return normalizeText(complaintType);
    }

    private int addComplaintTypeScore(int currentScore, String normalizedComplaintType, int points, String... complaintTypes) {
        for (String complaintType : complaintTypes) {
            if (normalizedComplaintType.equals(normalizeText(complaintType))) {
                return currentScore + points;
            }
        }
        return currentScore;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }

    private int calculateAgePoints(LocalDateTime dateOfIncident) {
        if (dateOfIncident == null || dateOfIncident.isAfter(LocalDateTime.now())) {
            return 0;
        }

        long daysOld = Duration.between(dateOfIncident, LocalDateTime.now()).toDays();
        if (daysOld >= 7) {
            return 15;
        }
        if (daysOld >= 3) {
            return 10;
        }
        if (daysOld >= 1) {
            return 5;
        }
        return 0;
    }

    private String resolveLabel(int score) {
        if (score >= 80) {
            return "CRITICAL";
        }
        if (score >= 50) {
            return "HIGH";
        }
        if (score >= 25) {
            return "MODERATE";
        }
        return "LOW";
    }

    public static class SeverityResult {
        private final int score;
        private final String label;

        public SeverityResult(int score, String label) {
            this.score = score;
            this.label = label;
        }

        public int getScore() {
            return score;
        }

        public String getLabel() {
            return label;
        }
    }
}
