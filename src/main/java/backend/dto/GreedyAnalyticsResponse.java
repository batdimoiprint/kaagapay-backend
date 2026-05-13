package backend.dto;

public class GreedyAnalyticsResponse {
    private String monthlyTotal;
    private String semiAnnualTotal;
    private String annualTotal;
    private String topIncidentType;
    private String topIncidentTypeCount;
    private String peakMonth;
    private String peakMonthCount;

    public GreedyAnalyticsResponse() {}

    public GreedyAnalyticsResponse(String monthlyTotal, String semiAnnualTotal, String annualTotal,
                                   String topIncidentType, String topIncidentTypeCount,
                                   String peakMonth, String peakMonthCount) {
        this.monthlyTotal = monthlyTotal;
        this.semiAnnualTotal = semiAnnualTotal;
        this.annualTotal = annualTotal;
        this.topIncidentType = topIncidentType;
        this.topIncidentTypeCount = topIncidentTypeCount;
        this.peakMonth = peakMonth;
        this.peakMonthCount = peakMonthCount;
    }

    public String getMonthlyTotal() { return monthlyTotal; }
    public void setMonthlyTotal(String monthlyTotal) { this.monthlyTotal = monthlyTotal; }

    public String getSemiAnnualTotal() { return semiAnnualTotal; }
    public void setSemiAnnualTotal(String semiAnnualTotal) { this.semiAnnualTotal = semiAnnualTotal; }

    public String getAnnualTotal() { return annualTotal; }
    public void setAnnualTotal(String annualTotal) { this.annualTotal = annualTotal; }

    public String getTopIncidentType() { return topIncidentType; }
    public void setTopIncidentType(String topIncidentType) { this.topIncidentType = topIncidentType; }

    public String getTopIncidentTypeCount() { return topIncidentTypeCount; }
    public void setTopIncidentTypeCount(String topIncidentTypeCount) { this.topIncidentTypeCount = topIncidentTypeCount; }

    public String getPeakMonth() { return peakMonth; }
    public void setPeakMonth(String peakMonth) { this.peakMonth = peakMonth; }

    public String getPeakMonthCount() { return peakMonthCount; }
    public void setPeakMonthCount(String peakMonthCount) { this.peakMonthCount = peakMonthCount; }
}
