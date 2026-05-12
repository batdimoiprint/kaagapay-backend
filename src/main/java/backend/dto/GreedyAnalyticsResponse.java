package backend.dto;

import java.util.List;
import java.util.Map;

public class GreedyAnalyticsResponse {
    private long monthlyTotal;
    private long semiAnnualTotal;
    private long annualTotal;
    private List<Map.Entry<String, Long>> topIncidentTypes;
    private PeakPeriod peakPeriod;

    public GreedyAnalyticsResponse() {}

    public GreedyAnalyticsResponse(long monthlyTotal, long semiAnnualTotal, long annualTotal, 
                                   List<Map.Entry<String, Long>> topIncidentTypes, PeakPeriod peakPeriod) {
        this.monthlyTotal = monthlyTotal;
        this.semiAnnualTotal = semiAnnualTotal;
        this.annualTotal = annualTotal;
        this.topIncidentTypes = topIncidentTypes;
        this.peakPeriod = peakPeriod;
    }

    // Getters and Setters
    public long getMonthlyTotal() { return monthlyTotal; }
    public void setMonthlyTotal(long monthlyTotal) { this.monthlyTotal = monthlyTotal; }

    public long getSemiAnnualTotal() { return semiAnnualTotal; }
    public void setSemiAnnualTotal(long semiAnnualTotal) { this.semiAnnualTotal = semiAnnualTotal; }

    public long getAnnualTotal() { return annualTotal; }
    public void setAnnualTotal(long annualTotal) { this.annualTotal = annualTotal; }

    public List<Map.Entry<String, Long>> getTopIncidentTypes() { return topIncidentTypes; }
    public void setTopIncidentTypes(List<Map.Entry<String, Long>> topIncidentTypes) { this.topIncidentTypes = topIncidentTypes; }

    public PeakPeriod getPeakPeriod() { return peakPeriod; }
    public void setPeakPeriod(PeakPeriod peakPeriod) { this.peakPeriod = peakPeriod; }

    public static class PeakPeriod {
        private String month;
        private long count;

        public PeakPeriod(String month, long count) {
            this.month = month;
            this.count = count;
        }

        public String getMonth() { return month; }
        public long getCount() { return count; }
    }
}
