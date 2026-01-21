package gr.mycitygov.controller;

import gr.mycitygov.dto.requesttype.CreateRequestTypeDto;
import gr.mycitygov.dto.requesttype.UpdateRequestTypeDto;
import gr.mycitygov.dto.requesttype.RequestTypeViewDto;
import gr.mycitygov.dto.department.DepartmentScheduleDto;
import gr.mycitygov.model.DepartmentSchedule;
import gr.mycitygov.model.RequestType;
import gr.mycitygov.repository.RequestTypeRepository;
import gr.mycitygov.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final RequestTypeRepository requestTypeRepository;

    public AdminController(AdminService adminService, RequestTypeRepository requestTypeRepository) {
        this.adminService = adminService;
        this.requestTypeRepository = requestTypeRepository;

    }

    // =========================================================
    // REQUEST TYPES (ADMIN)
    // =========================================================

    /**
     * ADMIN: Λίστα όλων των request types (για διαχείριση).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/request-types")
    public List<RequestTypeViewDto> getAllRequestTypes() {
        return requestTypeRepository.findAll()
                .stream()
                .map(adminService::toRequestTypeViewDto)
                .toList();
    }
    
    
    /**
     * ADMIN: Προσθήκη νέου Request Type.
     * Περιλαμβάνει και την αντιστοίχιση με Department μέσω departmentId.
     */
    @PostMapping("/request-types")
    public RequestTypeViewDto createRequestType(@RequestBody CreateRequestTypeDto dto) {
        RequestType rt = adminService.createRequestType(dto);
        return adminService.toRequestTypeViewDto(rt);
    }

    /**
     * ADMIN: Ενημέρωση Request Type (π.χ. name/desc/sla/category/requiredAttachments/departmentId).
     * Χρησιμοποιείται και για αλλαγή department (αντιστοίχιση).
     */
    @PatchMapping("/request-types/{id}")
    public RequestTypeViewDto updateRequestType(@PathVariable Long id, @RequestBody UpdateRequestTypeDto dto) {
        RequestType rt = adminService.updateRequestType(id, dto);
        return adminService.toRequestTypeViewDto(rt);
    }

    /**
     * ADMIN: Ενεργοποίηση Request Type (active=true).
     */
    @PatchMapping("/request-types/{id}/activate")
    public RequestTypeViewDto activate(@PathVariable Long id) {
        RequestType rt = adminService.setRequestTypeActive(id, true);
        return adminService.toRequestTypeViewDto(rt);
    }

    /**
     * ADMIN: Απενεργοποίηση Request Type (active=false).
     */
    @PatchMapping("/request-types/{id}/deactivate")
    public RequestTypeViewDto deactivate(@PathVariable Long id) {
        RequestType rt = adminService.setRequestTypeActive(id, false);
        return adminService.toRequestTypeViewDto(rt);
    }

    // =========================================================
    // DEPARTMENT SCHEDULE (ADMIN)
    // =========================================================

    /**
     * ADMIN: Upsert ωραρίου για συγκεκριμένο department και συγκεκριμένη ημέρα.
     * Αν υπάρχει ήδη record για (departmentId, dayOfWeek) => update.
     * Αν δεν υπάρχει => create.
     *
     * dayOfWeek: MONDAY, TUESDAY, ..., SUNDAY
     */
    @PutMapping("/departments/{departmentId}/schedule/{dayOfWeek}")
    public DepartmentScheduleDto upsertSchedule(@PathVariable Long departmentId,
                                                @PathVariable DayOfWeek dayOfWeek,
                                                @RequestBody DepartmentScheduleDto dto) {
        DepartmentSchedule s = adminService.upsertDepartmentSchedule(departmentId, dayOfWeek, dto);
        return adminService.toScheduleDto(s);
    }

    /**
     * ADMIN: Προβολή όλων των ωραρίων ενός department.
     */
    @GetMapping("/departments/{departmentId}/schedule")
    public List<DepartmentScheduleDto> getSchedules(@PathVariable Long departmentId) {
        return adminService.getDepartmentSchedules(departmentId)
                .stream().map(adminService::toScheduleDto).toList();
    }

    /**
     * ADMIN: Διαγραφή ωραρίου για συγκεκριμένη ημέρα ενός department.
     */
    @DeleteMapping("/departments/{departmentId}/schedule/{dayOfWeek}")
    public void deleteSchedule(@PathVariable Long departmentId, @PathVariable DayOfWeek dayOfWeek) {
        adminService.deleteDepartmentSchedule(departmentId, dayOfWeek);
    }
}
