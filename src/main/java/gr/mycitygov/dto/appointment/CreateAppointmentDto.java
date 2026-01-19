package gr.mycitygov.dto.appointment;

import java.time.LocalDateTime;

public class CreateAppointmentDto {
    private Long departmentId;
    private String citizenIdNumber;
    private LocalDateTime startAt; // ISO string: "2026-01-10T09:20"

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getCitizenIdNumber() { return citizenIdNumber; }
    public void setCitizenIdNumber(String citizenIdNumber) { this.citizenIdNumber = citizenIdNumber; }

    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
}
