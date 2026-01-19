package gr.mycitygov.dto.appointment;

import java.time.LocalDateTime;

public class RescheduleAppointmentDto {
    private LocalDateTime newStartAt;

    public LocalDateTime getNewStartAt() {
        return newStartAt;
    }

    public void setNewStartAt(LocalDateTime newStartAt) {
        this.newStartAt = newStartAt;
    }
}
