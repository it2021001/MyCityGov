package gr.mycitygov.dto.department;

import java.time.LocalTime;

public class DepartmentScheduleDto {

    private Long departmentId;
    private String dayOfWeek;   // MONDAY, TUESDAY, ...
    private LocalTime startTime; // "09:00"
    private LocalTime endTime;   // "15:00"

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
