package gr.mycitygov.dto.request;

import java.time.LocalDateTime;

public class RequestViewDto {

    private Long id;

    private Long requestTypeId;
    private String requestTypeName;

    private Long departmentId;
    private String departmentName;

    private String status; // π.χ. SUBMITTED / IN_PROGRESS / COMPLETED / REJECTED

    private String description;
    private String locationText;

    private String address;
    private String purpose;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRequestTypeId() { return requestTypeId; }
    public void setRequestTypeId(Long requestTypeId) { this.requestTypeId = requestTypeId; }

    public String getRequestTypeName() { return requestTypeName; }
    public void setRequestTypeName(String requestTypeName) { this.requestTypeName = requestTypeName; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocationText() { return locationText; }
    public void setLocationText(String locationText) { this.locationText = locationText; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
