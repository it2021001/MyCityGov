package gr.mycitygov.dto.request;

public class UpdateRequestStatusDto {
    private String status;   // π.χ. IN_PROGRESS / COMPLETED / REJECTED
    private String comment;  // προαιρετικό σχόλιο υπαλλήλου

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
