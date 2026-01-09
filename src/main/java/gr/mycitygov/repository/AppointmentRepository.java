package gr.mycitygov.repository;

import gr.mycitygov.enums.AppointmentStatus;
import gr.mycitygov.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDepartmentIdAndStartAtBetweenAndStatus(
            Long departmentId, LocalDateTime from, LocalDateTime to, AppointmentStatus status);

    boolean existsByDepartmentIdAndStartAtLessThanAndEndAtGreaterThanAndStatus(
            Long departmentId, LocalDateTime endAt, LocalDateTime startAt, AppointmentStatus status);
}
