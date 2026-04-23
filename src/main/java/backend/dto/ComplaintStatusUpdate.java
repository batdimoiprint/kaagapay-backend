package backend.dto;

public class ComplaintStatusUpdate {
    private String status;
    private String remarks;

    public ComplaintStatusUpdate() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
