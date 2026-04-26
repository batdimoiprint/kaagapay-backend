package backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class RemarkRequest {
    @Schema(description = "Description of the remark to add", example = "The field visit is scheduled for tomorrow.")
    private String remarkDescription;

    public RemarkRequest() {}

    public String getRemarkDescription() {
        return remarkDescription;
    }

    public void setRemarkDescription(String remarkDescription) {
        this.remarkDescription = remarkDescription;
    }
}
