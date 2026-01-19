package gr.mycitygov.controller;

import gr.mycitygov.dto.appointment.AppointmentViewDto;
import gr.mycitygov.dto.appointment.AvailabilityViewDto;
import gr.mycitygov.dto.appointment.CreateAppointmentDto;
import gr.mycitygov.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // =========================================================
    // CITIZEN
    // =========================================================

    /**
     * CITIZEN: Διαθέσιμα slots για department/date.
     * (Δεν είναι public -> θέλει login)
     *
     * GET /api/appointments/availability?departmentId=1&date=2026-01-20
     */
    @PreAuthorize("hasRole('CITIZEN')")
    @GetMapping("/availability")
    public AvailabilityViewDto availability(
            @RequestParam Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<LocalDateTime> slots = appointmentService.getAvailableSlots(departmentId, date);

        AvailabilityViewDto dto = new AvailabilityViewDto();
        dto.setDepartmentId(departmentId);
        dto.setDate(date.toString());
        dto.setSlots(slots.stream().map(dt -> dt.toLocalTime().toString()).toList());
        return dto;
    }

    /**
     * CITIZEN: Κλείσιμο ραντεβού.
     * Παίρνει citizenIdNumber από DB (CitizenProfile) με βάση τον logged-in citizen.
     *
     * POST /api/appointments/citizen
     */
    @PreAuthorize("hasRole('CITIZEN')")
    @PostMapping("/citizen")
    public AppointmentViewDto bookCitizen(@RequestBody CreateAppointmentDto dto, Principal principal) {
        return appointmentService.bookForCitizen(principal.getName(), dto);
    }

    /**
     * CITIZEN: Λίστα ραντεβού του πολίτη (μόνο τα δικά του).
     *
     * GET /api/appointments/citizen
     */
    @PreAuthorize("hasRole('CITIZEN')")
    @GetMapping("/citizen")
    public List<AppointmentViewDto> myAppointments(Principal principal) {
        return appointmentService.getMyAppointments(principal.getName());
    }

    /**
     * CITIZEN: Ακύρωση δικού μου ραντεβού.
     *
     * PATCH /api/appointments/citizen/{id}/cancel
     */
    @PreAuthorize("hasRole('CITIZEN')")
    @PatchMapping("/citizen/{id}/cancel")
    public AppointmentViewDto cancelMyAppointment(@PathVariable Long id, Principal principal) {
        return appointmentService.cancelMyAppointment(principal.getName(), id);
    }

    // =========================================================
    // EMPLOYEE
    // =========================================================

    /**
     * EMPLOYEE: Λίστα ραντεβού που ανήκουν στο department του υπαλλήλου.
     *
     * GET /api/appointments/employee
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/employee")
    public List<AppointmentViewDto> departmentAppointments(Principal principal) {
        return appointmentService.getDepartmentAppointments(principal.getName());
    }

    /**
     * EMPLOYEE: Επιβεβαίωση ραντεβού (BOOKED -> CONFIRMED).
     * Μόνο αν το ραντεβού ανήκει στο department του employee.
     *
     * PATCH /api/appointments/employee/{id}/confirm
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/employee/{id}/confirm")
    public AppointmentViewDto confirm(@PathVariable Long id, Principal principal) {
        return appointmentService.confirmAsEmployee(principal.getName(), id);
    }

    public static class RescheduleDto {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startAt;
        public LocalDateTime getStartAt() { return startAt; }
        public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
    }

    /**
     * EMPLOYEE: Αλλαγή ώρας ραντεβού (reschedule).
     * Μόνο αν το ραντεβού ανήκει στο department του employee.
     *
     * PATCH /api/appointments/employee/{id}/reschedule
     * body: { "startAt": "2026-01-20T11:00:00" }
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/employee/{id}/reschedule")
    public AppointmentViewDto reschedule(@PathVariable Long id,
                                         @RequestBody RescheduleDto body,
                                         Principal principal) {
        return appointmentService.rescheduleAsEmployee(principal.getName(), id, body.getStartAt());
    }

    /**
     * EMPLOYEE: Ακύρωση ραντεβού.
     * Μόνο αν το ραντεβού ανήκει στο department του employee.
     *
     * PATCH /api/appointments/employee/{id}/cancel
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/employee/{id}/cancel")
    public AppointmentViewDto cancelAsEmployee(@PathVariable Long id, Principal principal) {
        return appointmentService.cancelAsEmployee(principal.getName(), id);
    }

    // =========================================================
    // ADMIN
    // =========================================================

    /**
     * ADMIN: Ακύρωση οποιουδήποτε ραντεβού.
     *
     * PATCH /api/appointments/admin/{id}/cancel
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{id}/cancel")
    public AppointmentViewDto cancelAsAdmin(@PathVariable Long id) {
        return appointmentService.cancelAsAdmin(id);
    }
}
