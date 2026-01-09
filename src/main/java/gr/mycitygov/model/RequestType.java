package gr.mycitygov.model;

import gr.mycitygov.enums.RequestCategory;
import jakarta.persistence.*;

@Entity
@Table(name = "request_type")
public class RequestType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestCategory category;

    @Column(name = "sla_days", nullable = false)
    private int slaDays;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "required_attachments", nullable = false)
    private Integer requiredAttachments = 0;


    /* =======================
       Getters & Setters
       ======================= */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestCategory getCategory() {
        return category;
    }

    public void setCategory(RequestCategory category) {
        this.category = category;
    }

    public int getSlaDays() {
        return slaDays;
    }

    public void setSlaDays(int slaDays) {
        this.slaDays = slaDays;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Integer getRequiredAttachments() {
        return requiredAttachments; }

    public void setRequiredAttachments(Integer requiredAttachments) {
        this.requiredAttachments = requiredAttachments; }

}
