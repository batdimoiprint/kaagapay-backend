package backend.service;

import backend.dto.ComplaintRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SeverityService {

    public SeverityResult calculateSeverity(ComplaintRequest request, boolean hasMediaEvidence) {
        int score = 0;

        String normalizedType = normalizeText(
                request.getComplaintType() != null ? request.getComplaintType() : "");
        String normalizedDesc = normalizeText(
                request.getDescription() != null ? request.getDescription() : "");

        // --- 1. Score from complaint-type + description keyword matching ---
        score += calculateKeywordScore(normalizedType, normalizedDesc);

        // --- 2. Contextual flags (fields are Strings from MIT App Inventor) ---
        if (parseBoolean(request.getHasInjury())) {
            score += 35;
        }
        if (parseBoolean(request.getNeedsImmediateResponse())) {
            score += 25;
        }
        if (parseInteger(request.getAffectedPeopleCount()) > 1) {
            score += 20;
        }
        if (parseBoolean(request.getHasPropertyDamage())) {
            score += 15;
        }

        // --- 3. Media evidence ---
        if (hasMediaEvidence) {
            score += 5;
        }

        // --- 4. Incident age ---
        score += calculateAgePoints(request.getDateOfIncident());

        return new SeverityResult(score, resolveLabel(score));
    }

    /**
     * Determines the base severity score by matching the complaint type dropdown
     * value AND scanning the free-text description for known keywords.
     *
     * The highest-tier match wins (we do NOT stack tier scores).
     */
    private int calculateKeywordScore(String normalizedType, String normalizedDesc) {
        int bestScore = 0;

        for (Map.Entry<Integer, List<List<String>>> entry : ComplaintKeywords.TIERS.entrySet()) {
            int tierPoints = entry.getKey();
            if (tierPoints <= bestScore) {
                continue; // already have a higher match
            }

            for (List<String> keywordList : entry.getValue()) {
                if (matchesAny(normalizedType, normalizedDesc, keywordList)) {
                    bestScore = tierPoints;
                    break; // found a match in this tier, no need to check more lists at same tier
                }
            }
        }

        return bestScore;
    }

    /**
     * Returns true if the normalised complaint type exactly equals any keyword
     * (after normalisation), OR the normalised description contains any keyword.
     */
    private boolean matchesAny(String normalizedType, String normalizedDesc,
                               List<String> keywords) {
        for (String keyword : keywords) {
            String normalizedKeyword = normalizeText(keyword);
            if (normalizedKeyword.isEmpty()) {
                continue;
            }
            // Exact match on the dropdown type
            if (normalizedType.equals(normalizedKeyword)) {
                return true;
            }
            // Substring match on the description
            if (!normalizedDesc.isEmpty() && normalizedDesc.contains(normalizedKeyword)) {
                return true;
            }
        }
        return false;
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

    private boolean parseBoolean(String value) {
        return "true".equalsIgnoreCase(value);
    }

    private int parseInteger(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
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
