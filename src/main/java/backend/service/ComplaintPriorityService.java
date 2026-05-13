package backend.service;

import backend.entity.Complaint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComplaintPriorityService {

    /**
     * Status priority: PENDING (highest) → ON_GOING → COMPLETED → WITHDRAWN (lowest).
     * Within the same status group, sort by severityScore descending.
     */
    public List<Complaint> sortByHighestSeverity(List<Complaint> complaints) {
        List<Complaint> sorted = new ArrayList<>(complaints);
        sorted.sort((a, b) -> {
            int statusCmp = getStatusPriority(a.getStatus()) - getStatusPriority(b.getStatus());
            if (statusCmp != 0) {
                return statusCmp; // lower priority number = higher in list
            }
            // Same status group → highest severity first
            return getSeverityScore(b) - getSeverityScore(a);
        });
        return sorted;
    }

    private int getStatusPriority(String status) {
        if (status == null) return 99;
        return switch (status.toUpperCase()) {
            case "PENDING"   -> 0;
            case "ON_GOING"  -> 1;
            case "COMPLETED" -> 2;
            case "WITHDRAWN" -> 3;
            default          -> 99;
        };
    }

    public List<Complaint> sortByNewestFirst(List<Complaint> complaints) {
        List<Complaint> prioritizedComplaints = new ArrayList<>(complaints);
        prioritizedComplaints.sort((left, right) -> {
            var leftDateOfIncident = left.getDateOfIncident();
            var rightDateOfIncident = right.getDateOfIncident();

            if (leftDateOfIncident == null && rightDateOfIncident == null) {
                return compareIdsDescending(left, right);
            }
            if (leftDateOfIncident == null) {
                return 1;
            }
            if (rightDateOfIncident == null) {
                return -1;
            }

            int incidentComparison = rightDateOfIncident.compareTo(leftDateOfIncident);
            if (incidentComparison != 0) {
                return incidentComparison;
            }

            return compareIdsDescending(left, right);
        });

        return prioritizedComplaints;
    }

    private int compareIdsDescending(Complaint left, Complaint right) {
        if (left.getId() == null && right.getId() == null) {
            return 0;
        }
        if (left.getId() == null) {
            return 1;
        }
        if (right.getId() == null) {
            return -1;
        }
        return right.getId().compareTo(left.getId());
    }

    private int getSeverityScore(Complaint complaint) {
        return complaint.getSeverityScore() != null ? complaint.getSeverityScore() : 0;
    }
}
