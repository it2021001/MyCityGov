package gr.mycitygov.repository;

import gr.mycitygov.enums.RequestStatus;
import gr.mycitygov.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    // =========================
    // CITIZEN
    // =========================
    Optional<Request> findByProtocolNumber(String protocolNumber);

    List<Request> findByCitizenUserIdOrderByCreatedAtDesc(Long citizenUserId);

    Optional<Request> findByIdAndCitizenUserId(Long id, Long citizenUserId);

    // =========================
    // EMPLOYEE (department scope)
    // =========================

    /**
     * EMPLOYEE: Όλα τα αιτήματα του department (για "λίστα υπηρεσίας").
     */
    @Query("""
        SELECT r
        FROM Request r
        WHERE r.requestType.department.id = :departmentId
        ORDER BY r.createdAt DESC
    """)
    List<Request> findByDepartmentIdOrderByCreatedAtDesc(Long departmentId);

    /**
     * EMPLOYEE: Βρες συγκεκριμένο αίτημα ΜΟΝΟ αν ανήκει στο department.
     */
    @Query("""
        SELECT r
        FROM Request r
        WHERE r.id = :requestId
          AND r.requestType.department.id = :departmentId
    """)
    Optional<Request> findByIdAndDepartmentId(Long requestId, Long departmentId);

    /**
     * EMPLOYEE: PRIORITY TAKE-NEXT
     * Παίρνει τα "μη ανατεθειμένα" SUBMITTED αιτήματα της υπηρεσίας,
     * με προτεραιότητα:
     * 1) dueAt ASC (λήγει πιο σύντομα)
     * 2) createdAt ASC (πιο παλιό)
     */
    @Query("""
        SELECT r
        FROM Request r
        WHERE r.requestType.department.id = :departmentId
          AND r.assignedEmployeeUserId IS NULL
          AND r.status = :status
        ORDER BY r.dueAt ASC, r.createdAt ASC
    """)
    List<Request> findPriorityUnassignedByDepartment(Long departmentId, RequestStatus status);
}
