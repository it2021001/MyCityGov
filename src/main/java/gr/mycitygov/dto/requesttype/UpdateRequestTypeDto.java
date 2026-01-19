package gr.mycitygov.dto.requesttype;

import gr.mycitygov.enums.RequestCategory;

public class UpdateRequestTypeDto {
    private String name;
    private String description;
    private Integer slaDays;
    private RequestCategory category;
    private Long departmentId;
    private Boolean active;
    private String requiredAttachments;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSlaDays() { return slaDays; }
    public void setSlaDays(Integer slaDays) { this.slaDays = slaDays; }

    public RequestCategory getCategory() { return category; }
    public void setCategory(RequestCategory category) { this.category = category; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getRequiredAttachments() { return requiredAttachments; }
    public void setRequiredAttachments(String requiredAttachments) { this.requiredAttachments = requiredAttachments; }
}
