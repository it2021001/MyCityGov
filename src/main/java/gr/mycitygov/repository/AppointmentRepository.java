package gr.mycitygov.repository;

import gr.mycitygov.enums.AppointmentStatus;
import gr.mycitygov.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // =========================
    // Department day bookings (availability)
    // =========================
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.department.id = :departmentId
          AND a.status <> gr.mycitygov.enums.AppointmentStatus.CANCELLED
          AND a.startAt >= :dayStart
          AND a.startAt < :dayEnd
    """)
    List<Appointment> findBookedByDepartmentAndDay(Long departmentId, LocalDateTime dayStart, LocalDateTime dayEnd);

    // =========================
    // Citizen: list my appointments
    // =========================
    List<Appointment> findByCitizenIdNumberOrderByStartAtDesc(String citizenIdNumber);

    // Citizen: cancel only if belongs to him
    Optional<Appointment> findByIdAndCitizenIdNumber(Long id, String citizenIdNumber);

    // =========================
    // Overlap checks
    // =========================
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.department.id = :departmentId
          AND a.status <> gr.mycitygov.enums.AppointmentStatus.CANCELLED
          AND a.startAt < :endAt
          AND a.endAt > :startAt
    """)
    boolean existsOverlap(Long departmentId, LocalDateTime startAt, LocalDateTime endAt);

    /**
     * IMPORTANT RULE (όπως ζήτησες):
     * Ο πολίτης ΔΕΝ μπορεί να κλείσει 2 ραντεβού την ίδια ώρα, ακόμα κι αν είναι σε άλλο department.
     */
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.citizenIdNumber = :citizenIdNumber
          AND a.status <> gr.mycitygov.enums.AppointmentStatus.CANCELLED
          AND a.startAt < :endAt
          AND a.endAt > :startAt
    """)
    boolean existsCitizenOverlap(String citizenIdNumber, LocalDateTime startAt, LocalDateTime endAt);

    // overlap when rescheduling (exclude itself)
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.department.id = :departmentId
          AND a.id <> :appointmentId
          AND a.status <> gr.mycitygov.enums.AppointmentStatus.CANCELLED
          AND a.startAt < :endAt
          AND a.endAt > :startAt
    """)
    boolean existsOverlapExcludingId(Long departmentId, Long appointmentId, LocalDateTime startAt, LocalDateTime endAt);

    // =========================
    // Employee: department appointments
    // =========================
    List<Appointment> findByDepartmentIdAndStatusInOrderByStartAtAsc(Long departmentId, Collection<AppointmentStatus> statuses);
}
