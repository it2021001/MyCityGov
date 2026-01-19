package gr.mycitygov.model;

import gr.mycitygov.enums.RequestStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name= "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "protocol_number", nullable = false, unique = true, length = 30)
    private String protocolNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "request_type_id", nullable = false)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.SUBMITTED;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "due_at", nullable = false)
    private LocalDateTime dueAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    /* Extra columns (ανάλογα με category) */
    @Column(name = "location_text", length = 255)
    private String locationText;   // PROBLEM_REPORT

    @Column(length = 255)
    private String address;        // APPLICATION

    @Column(length = 255)
    private String purpose;        // APPLICATION

    @Column(length = 9)
    private String afm;        // APPLICATION

    @Column(length = 11)
    private String amka;        // APPLICATION


    @Column(name = "citizen_id_number", length = 20)
    private String citizenIdNumber;        // APPLICATION

    @Column(name="citizen_user_id", nullable=false)
    private Long citizenUserId;

    @Column(name = "assigned_employee_user_id")
    private Long assignedEmployeeUserId;


    /* =======================
       Getters & Setters
       ======================= */

    public Long getId() {
        return id;
    }
    public void setId(Long id) {this.id = id;}

    public String getProtocolNumber() {
        return protocolNumber;
    }
    public void setProtocolNumber(String protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    public RequestType getRequestType() {
        return requestType;
    }
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public RequestStatus getStatus() {
        return status;
    }
    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }
    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }
    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public String getLocationText() {
        return locationText;
    }
    public void setLocationText(String locationText) {
        this.locationText = locationText;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPurpose() {
        return purpose;
    }
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getAfm() {
        return afm;
    }
    public void setAfm(String afm) {
        this.afm = afm;
    }

    public String getAmka() {
        return amka;
    }
    public void setAmka(String amka) {
        this.amka = amka;
    }

    public String getCitizenIdNumber() {
        return citizenIdNumber;
    }
    public void setCitizenIdNumber(String citizenIdNumber) {
        this.citizenIdNumber = citizenIdNumber;
    }

    public Long getCitizenUserId() {return citizenUserId;}
    public void setCitizenUserId(Long citizenUserId) {this.citizenUserId = citizenUserId;}

    public Long getAssignedEmployeeUserId() {return assignedEmployeeUserId;}
    public void setAssignedEmployeeUserId(Long assignedEmployeeUserId) {this.assignedEmployeeUserId = assignedEmployeeUserId;}
}