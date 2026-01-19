package gr.mycitygov.model;

import gr.mycitygov.enums.RequestNoteType;
import jakarta.persistence.*;

@Entity
@Table(name = "request_note")
public class RequestNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private Long requestId;

    @Column(name = "employee_user_id", nullable = false)
    private Long employeeUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "note_type", nullable = false, length = 20)
    private RequestNoteType noteType;

    @Column(nullable = false, length = 1000)
    private String text;

    // ===== getters/setters =====

    public Long getId() {
        return id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getEmployeeUserId() {
        return employeeUserId;
    }

    public void setEmployeeUserId(Long employeeUserId) {
        this.employeeUserId = employeeUserId;
    }

    public RequestNoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(RequestNoteType noteType) {
        this.noteType = noteType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
