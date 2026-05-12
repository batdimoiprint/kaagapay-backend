package backend.service;

import backend.entity.Complaint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComplaintPriorityService {

    public List<Complaint> sortByHighestSeverity(List<Complaint> complaints) {
        List<Complaint> prioritizedComplaints = new ArrayList<>(complaints);

        for (int i = 0; i < prioritizedComplaints.size() - 1; i++) {
            for (int j = i + 1; j < prioritizedComplaints.size(); j++) {
                if (getSeverityScore(prioritizedComplaints.get(j)) > getSeverityScore(prioritizedComplaints.get(i))) {
                    Complaint temp = prioritizedComplaints.get(i);
                    prioritizedComplaints.set(i, prioritizedComplaints.get(j));
                    prioritizedComplaints.set(j, temp);
                }
            }
        }

        return prioritizedComplaints;
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
