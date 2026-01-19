package gr.mycitygov.dto.appointment;

import java.util.List;

public class AvailabilityViewDto {
    private Long departmentId;
    private String date;
    private List<String> slots;

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<String> getSlots() { return slots; }
    public void setSlots(List<String> slots) { this.slots = slots; }
}
