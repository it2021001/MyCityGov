package gr.mycitygov.repository;

import gr.mycitygov.model.DepartmentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface DepartmentScheduleRepository extends JpaRepository<DepartmentSchedule, Long> {
    List<DepartmentSchedule> findByDepartmentIdAndDayOfWeek(Long departmentId, DayOfWeek dayOfWeek);
}
