package gr.mycitygov.dto.request;

public class EmployeeDecisionDto {

    /**
     * true  => COMPLETED
     * false => REJECTED
     */
    private boolean approved;

    /**
     * Υποχρεωτική τεκμηρίωση (θα γίνει RequestNote DECISION).
     */
    private String reason;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
