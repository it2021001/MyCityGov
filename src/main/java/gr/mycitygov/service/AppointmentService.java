package gr.mycitygov.service;

import gr.mycitygov.dto.appointment.AppointmentViewDto;
import gr.mycitygov.dto.appointment.CreateAppointmentDto;
import gr.mycitygov.enums.AppointmentStatus;
import gr.mycitygov.model.Appointment;
import gr.mycitygov.model.CitizenProfile;
import gr.mycitygov.model.Department;
import gr.mycitygov.model.DepartmentSchedule;
import gr.mycitygov.model.EmployeeProfile;
import gr.mycitygov.repository.AppointmentRepository;
import gr.mycitygov.repository.CitizenProfileRepository;
import gr.mycitygov.repository.DepartmentRepository;
import gr.mycitygov.repository.DepartmentScheduleRepository;
import gr.mycitygov.repository.EmployeeProfileRepository;
import gr.mycitygov.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;

    private final UserRepository userRepository;
    private final CitizenProfileRepository citizenProfileRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    // σταθερή διάρκεια slot 30'
    private final Duration slotDuration = Duration.ofMinutes(30);

    public AppointmentService(DepartmentRepository departmentRepository,
                              DepartmentScheduleRepository scheduleRepository,
                              AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              CitizenProfileRepository citizenProfileRepository,
                              EmployeeProfileRepository employeeProfileRepository) {
        this.departmentRepository = departmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.citizenProfileRepository = citizenProfileRepository;
        this.employeeProfileRepository = employeeProfileRepository;
    }

    // =========================================================
    // CITIZEN
    // =========================================================

    /**
     * CITIZEN: Κλείσιμο ραντεβού.
     * Το citizenIdNumber ανακτάται από DB μέσω logged-in username.
     */
    @Transactional
    public AppointmentViewDto bookForCitizen(String username, CreateAppointmentDto dto) {

        if (dto.getStartAt() == null) throw new RuntimeException("startAt is required");
        if (dto.getDepartmentId() == null) throw new RuntimeException("departmentId is required");

        CitizenProfile cp = loadCitizenProfileByUsername(username);
        String citizenIdNumber = cp.getCitizenIdNumber();
        if (citizenIdNumber == null || citizenIdNumber.isBlank()) {
            throw new RuntimeException("Citizen profile is missing citizenIdNumber");
        }

        // Rule: min 1 day in advance, max 60 days
        LocalDate today = LocalDate.now();
        LocalDate appointmentDate = dto.getStartAt().toLocalDate();
        if (appointmentDate.isBefore(today.plusDays(1))) throw new RuntimeException("Appointments must be booked at least 1 day in advance");
        if (appointmentDate.isAfter(today.plusDays(60))) throw new RuntimeException("You can book up to 60 days in advance");

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        LocalDateTime startAt = dto.getStartAt();
        LocalDateTime endAt = startAt.plus(slotDuration);

        // schedule validation
        validateInsideSchedule(department.getId(), startAt, endAt);

        // overlap
        if (appointmentRepository.existsOverlap(department.getId(), startAt, endAt)) {
            throw new RuntimeException("Time slot is not available");
        }

        if (appointmentRepository.existsCitizenOverlap(citizenIdNumber, startAt, endAt)) {
            throw new RuntimeException("Citizen already has a booked appointment in this time slot");
        }

        Appointment a = new Appointment();
        a.setDepartment(department);
        a.setCitizenIdNumber(citizenIdNumber);
        a.setStartAt(startAt);
        a.setEndAt(endAt);
        a.setStatus(AppointmentStatus.BOOKED);
        a.setCreatedAt(LocalDateTime.now());

        appointmentRepository.save(a);
        return toDto(a);
    }

    /**
     * CITIZEN: Επιστρέφει τα ραντεβού του πολίτη.
     */
    @Transactional(readOnly = true)
    public List<AppointmentViewDto> getMyAppointments(String username) {
        CitizenProfile cp = loadCitizenProfileByUsername(username);
        String citizenIdNumber = cp.getCitizenIdNumber();
        if (citizenIdNumber == null || citizenIdNumber.isBlank()) {
            throw new RuntimeException("Citizen profile is missing citizenIdNumber");
        }

        return appointmentRepository.findByCitizenIdNumberOrderByStartAtDesc(citizenIdNumber)
                .stream().map(this::toDto).toList();
    }

    /**
     * CITIZEN: Ακυρώνει ραντεβού του πολίτη (μόνο αν του ανήκει).
     */
    @Transactional
    public AppointmentViewDto cancelMyAppointment(String username, Long appointmentId) {
        CitizenProfile cp = loadCitizenProfileByUsername(username);
        String citizenIdNumber = cp.getCitizenIdNumber();
        if (citizenIdNumber == null || citizenIdNumber.isBlank()) {
            throw new RuntimeException("Citizen profile is missing citizenIdNumber");
        }

        Appointment a = appointmentRepository.findByIdAndCitizenIdNumber(appointmentId, citizenIdNumber)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        return cancelInternal(a);
    }

    // =========================================================
    // EMPLOYEE (εκφώνηση: confirm / reschedule / cancel)
    // =========================================================

    /**
     * EMPLOYEE: Λίστα ραντεβού της υπηρεσίας του (upcoming).
     */
    @Transactional(readOnly = true)
    public List<AppointmentViewDto> getDepartmentAppointments(String employeeUsername) {
        EmployeeProfile ep = loadEmployeeProfileByUsername(employeeUsername);

        var statuses = List.of(AppointmentStatus.BOOKED, AppointmentStatus.CONFIRMED);
        return appointmentRepository
                .findByDepartmentIdAndStatusInOrderByStartAtAsc(ep.getDepartmentId(), statuses)
                .stream().map(this::toDto).toList();
    }

    /**
     * EMPLOYEE: Επιβεβαίωση ραντεβού (BOOKED -> CONFIRMED) μόνο για το department του.
     */
    @Transactional
    public AppointmentViewDto confirmAsEmployee(String employeeUsername, Long appointmentId) {
        EmployeeProfile ep = loadEmployeeProfileByUsername(employeeUsername);

        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        ensureSameDepartment(ep, a);

        if (a.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment is cancelled");
        }
        if (a.getStartAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Past appointments cannot be confirmed");
        }

        a.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(a);
        return toDto(a);
    }

    /**
     * EMPLOYEE: Αλλαγή ώρας (reschedule) μόνο για το department του.
     * Κρατά status BOOKED/CONFIRMED (δεν το γυρνάμε πίσω).
     */
    @Transactional
    public AppointmentViewDto rescheduleAsEmployee(String employeeUsername, Long appointmentId, LocalDateTime newStartAt) {
        if (newStartAt == null) throw new RuntimeException("newStartAt is required");

        EmployeeProfile ep = loadEmployeeProfileByUsername(employeeUsername);

        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        ensureSameDepartment(ep, a);

        if (a.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment is cancelled");
        }

        LocalDateTime newEndAt = newStartAt.plus(slotDuration);

        // schedule validation
        validateInsideSchedule(ep.getDepartmentId(), newStartAt, newEndAt);

        // overlap excluding this appointment
        if (appointmentRepository.existsOverlapExcludingId(ep.getDepartmentId(), appointmentId, newStartAt, newEndAt)) {
            throw new RuntimeException("Time slot is not available");
        }

        a.setStartAt(newStartAt);
        a.setEndAt(newEndAt);

        appointmentRepository.save(a);
        return toDto(a);
    }

    /**
     * EMPLOYEE: Ακύρωση ραντεβού (μόνο για το department του).
     */
    @Transactional
    public AppointmentViewDto cancelAsEmployee(String employeeUsername, Long appointmentId) {
        EmployeeProfile ep = loadEmployeeProfileByUsername(employeeUsername);

        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        ensureSameDepartment(ep, a);
        return cancelInternal(a);
    }

    // =========================================================
    // ADMIN
    // =========================================================

    /**
     * ADMIN: Ακυρώνει οποιοδήποτε ραντεβού.
     */
    @Transactional
    public AppointmentViewDto cancelAsAdmin(Long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return cancelInternal(a);
    }

    // =========================================================
    // AVAILABILITY
    // =========================================================

    /**
     * (Used by controller) Διαθέσιμα slots για department/date.
     */
    @Transactional(readOnly = true)
    public List<LocalDateTime> getAvailableSlots(Long departmentId, LocalDate date) {

        DayOfWeek day = date.getDayOfWeek();

        DepartmentSchedule schedule = scheduleRepository
                .findByDepartmentIdAndDayOfWeek(departmentId, day)
                .orElseThrow(() -> new IllegalArgumentException("No schedule for department " + departmentId + " on " + day));

        LocalDateTime dayStart = LocalDateTime.of(date, schedule.getStartTime());
        LocalDateTime dayEnd = LocalDateTime.of(date, schedule.getEndTime());

        var booked = appointmentRepository.findBookedByDepartmentAndDay(departmentId, dayStart, dayEnd);

        Set<LocalDateTime> takenStarts = booked.stream()
                .map(Appointment::getStartAt)
                .collect(Collectors.toSet());

        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime cursor = dayStart;

        while (!cursor.plus(slotDuration).isAfter(dayEnd)) {
            if (!takenStarts.contains(cursor)) {
                slots.add(cursor);
            }
            cursor = cursor.plus(slotDuration);
        }

        return slots;
    }

    // =========================================================
    // Helpers
    // =========================================================

    private AppointmentViewDto cancelInternal(Appointment a) {
        if (a.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment is already cancelled");
        }
        if (a.getStartAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Past appointments cannot be cancelled");
        }

        a.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(a);
        return toDto(a);
    }

    private void validateInsideSchedule(Long departmentId, LocalDateTime startAt, LocalDateTime endAt) {
        DayOfWeek day = startAt.getDayOfWeek();

        DepartmentSchedule schedule = scheduleRepository
                .findByDepartmentIdAndDayOfWeek(departmentId, day)
                .orElseThrow(() -> new RuntimeException("No schedule for department " + departmentId + " on " + day));

        LocalDateTime dayStart = LocalDateTime.of(startAt.toLocalDate(), schedule.getStartTime());
        LocalDateTime dayEnd = LocalDateTime.of(startAt.toLocalDate(), schedule.getEndTime());

        if (startAt.isBefore(dayStart) || endAt.isAfter(dayEnd)) {
            throw new RuntimeException("Appointment is outside schedule hours");
        }
    }

    private void ensureSameDepartment(EmployeeProfile ep, Appointment a) {
        Long depId = a.getDepartment() != null ? a.getDepartment().getId() : null;
        if (depId == null || !depId.equals(ep.getDepartmentId())) {
            throw new RuntimeException("Forbidden: appointment is not in your department");
        }
    }

    private CitizenProfile loadCitizenProfileByUsername(String username) {
        Long userId = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getId();

        return citizenProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Citizen profile not found"));
    }

    private EmployeeProfile loadEmployeeProfileByUsername(String username) {
        Long userId = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getId();

        return employeeProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));
    }

    private AppointmentViewDto toDto(Appointment a) {
        AppointmentViewDto dto = new AppointmentViewDto();
        dto.setId(a.getId());
        dto.setDepartmentId(a.getDepartment() != null ? a.getDepartment().getId() : null);
        dto.setCitizenIdNumber(a.getCitizenIdNumber());
        dto.setStartAt(a.getStartAt());
        dto.setEndAt(a.getEndAt());
        dto.setStatus(a.getStatus());
        return dto;
    }
}
