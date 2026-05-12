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
        String searchableIncident = buildSearchableIncident(request);

        if (containsAny(searchableIncident, "fire hazard", "electrical hazard", "animal bite", "attack", "missing person", "harassment", "threats", "life-threatening", "critical emergency")) {
            score += 50;
        }
        if (Boolean.TRUE.equals(request.getHasInjury())) {
            score += 35;
        }
        if (containsAny(searchableIncident, "domestic conflict", "theft", "suspicious activity", "public health", "infrastructure damage", "fallen tree", "violence", "medical emergency")) {
            score += 30;
        }
        if (Boolean.TRUE.equals(request.getNeedsImmediateResponse())) {
            score += 25;
        }
        if (request.getAffectedPeopleCount() != null && request.getAffectedPeopleCount() > 1) {
            score += 20;
        }
        if (Boolean.TRUE.equals(request.getHasPropertyDamage())) {
            score += 15;
        }
        if (containsAny(searchableIncident, "flooding", "pipe issue", "road damage", "pothole", "drainage", "clogged canal", "vandalism", "trespassing", "pollution", "public safety", "hazard", "danger", "unsafe")) {
            score += 15;
        }
        if (containsAny(searchableIncident, "noise", "garbage", "sanitation", "parking", "stray", "animal concern", "dispute", "disturbance", "curfew", "ordinance", "abandoned", "vendor", "construction", "building code")) {
            score += 5;
        }
        if (hasMediaEvidence) {
            score += 5;
        }

        score += calculateAgePoints(request.getDateOfIncident());

        return new SeverityResult(score, resolveLabel(score));
    }

    private String buildSearchableIncident(ComplaintRequest request) {
        String complaintType = request.getComplaintType() != null ? request.getComplaintType() : "";
        String description = request.getDescription() != null ? request.getDescription() : "";
        return (complaintType + " " + description).toLowerCase(Locale.ROOT);
    }

    private boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
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
