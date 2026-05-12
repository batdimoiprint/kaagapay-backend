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

    private int getSeverityScore(Complaint complaint) {
        return complaint.getSeverityScore() != null ? complaint.getSeverityScore() : 0;
    }
}
