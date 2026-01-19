package gr.mycitygov.repository;

import gr.mycitygov.model.DepartmentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface DepartmentScheduleRepository extends JpaRepository<DepartmentSchedule, Long> {
    Optional<DepartmentSchedule> findByDepartmentIdAndDayOfWeek(Long departmentId, DayOfWeek dayOfWeek);
    List<DepartmentSchedule> findByDepartmentIdOrderByDayOfWeekAsc(Long departmentId);
}
