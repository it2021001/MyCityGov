package gr.mycitygov.dto.appointment;

public class CancelAppointmentDto {
    private String reason; // προαιρετικό

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
