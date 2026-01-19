package gr.mycitygov.dto.appointment;

import gr.mycitygov.enums.AppointmentStatus;

import java.time.LocalDateTime;

public class AppointmentViewDto {
    private Long id;
    private Long departmentId;
    private String departmentName;
    private String citizenIdNumber;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private AppointmentStatus status;
    private LocalDateTime createdAt;



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() {return departmentName;}
    public void setDepartmentName(String departmentName) {this.departmentName = departmentName;}


    public String getCitizenIdNumber() { return citizenIdNumber; }
    public void setCitizenIdNumber(String citizenIdNumber) { this.citizenIdNumber = citizenIdNumber; }

    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }

    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}

