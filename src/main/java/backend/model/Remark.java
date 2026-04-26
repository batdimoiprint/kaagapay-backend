package backend.model;

import java.time.LocalDateTime;

public class Remark {
    private LocalDateTime remarkTime;
    private String remarkDescription;
    private Long userID;

    public Remark() {}

    public Remark(LocalDateTime remarkTime, String remarkDescription, Long userID) {
        this.remarkTime = remarkTime;
        this.remarkDescription = remarkDescription;
        this.userID = userID;
    }

    public LocalDateTime getRemarkTime() {
        return remarkTime;
    }

    public void setRemarkTime(LocalDateTime remarkTime) {
        this.remarkTime = remarkTime;
    }

    public String getRemarkDescription() {
        return remarkDescription;
    }

    public void setRemarkDescription(String remarkDescription) {
        this.remarkDescription = remarkDescription;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }
}
