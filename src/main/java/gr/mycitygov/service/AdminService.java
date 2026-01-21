package gr.mycitygov.service;

import gr.mycitygov.dto.department.DepartmentScheduleDto;
import gr.mycitygov.dto.requesttype.CreateRequestTypeDto;
import gr.mycitygov.dto.requesttype.RequestTypeViewDto;
import gr.mycitygov.dto.requesttype.UpdateRequestTypeDto;
import gr.mycitygov.model.Department;
import gr.mycitygov.model.DepartmentSchedule;
import gr.mycitygov.model.RequestType;
import gr.mycitygov.repository.DepartmentRepository;
import gr.mycitygov.repository.DepartmentScheduleRepository;
import gr.mycitygov.repository.RequestTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Service
public class AdminService {

    private final RequestTypeRepository requestTypeRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentScheduleRepository departmentScheduleRepository;

    public AdminService(RequestTypeRepository requestTypeRepository,
                        DepartmentRepository departmentRepository,
                        DepartmentScheduleRepository departmentScheduleRepository) {
        this.requestTypeRepository = requestTypeRepository;
        this.departmentRepository = departmentRepository;
        this.departmentScheduleRepository = departmentScheduleRepository;
    }

    // =========================================================
    // REQUEST TYPES
    // =========================================================

    @Transactional
    public RequestType createRequestType(CreateRequestTypeDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) throw new RuntimeException("name is required");
        if (dto.getCategory() == null) throw new RuntimeException("category is required");
        if (dto.getDepartmentId() == null) throw new RuntimeException("departmentId is required");

        Integer slaDays = parsePositiveInt(dto.getSlaDays(), "slaDays", true);

        // requiredAttachments στο table είναι NOT NULL -> βάλε default 0 αν δεν δίνεται
        Integer requiredAttachments = (dto.getRequiredAttachments() == null)
                ? 0
                : parseNonNegativeInt(dto.getRequiredAttachments(), "requiredAttachments", true);

        Department dep = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        RequestType rt = new RequestType();
        rt.setName(dto.getName().trim());
        rt.setDescription(dto.getDescription());
        rt.setCategory(dto.getCategory());
        rt.setSlaDays(slaDays);
        rt.setRequiredAttachments(requiredAttachments);
        rt.setActive(true);
        rt.setDepartment(dep);

        return requestTypeRepository.save(rt);
    }

    @Transactional
    public RequestType updateRequestType(Long id, UpdateRequestTypeDto dto) {
        RequestType rt = requestTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RequestType not found"));

        if (dto.getName() != null) rt.setName(dto.getName().trim());
        if (dto.getDescription() != null) rt.setDescription(dto.getDescription());
        if (dto.getCategory() != null) rt.setCategory(dto.getCategory());

        // slaDays: επιτρέπεται update μόνο αν δόθηκε
        if (dto.getSlaDays() != null) {
            Integer slaDays = parsePositiveInt(dto.getSlaDays(), "slaDays", true);
            rt.setSlaDays(slaDays);
        }

        // requiredAttachments: επιτρέπεται update μόνο αν δόθηκε
        if (dto.getRequiredAttachments() != null) {
            Integer ra = parseNonNegativeInt(dto.getRequiredAttachments(), "requiredAttachments", true);
            rt.setRequiredAttachments(ra);
        }

        /**
         * ADMIN: Αντιστοίχιση τύπου αιτήματος με υπηρεσία/τμήμα.
         * Αν δοθεί departmentId, αλλάζει το department του RequestType.
         */
        if (dto.getDepartmentId() != null) {
            Department dep = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            rt.setDepartment(dep);
        }

        return requestTypeRepository.save(rt);
    }

    /**
     * ADMIN: Ενεργοποίηση/Απενεργοποίηση RequestType.
     */
    @Transactional
    public RequestType setRequestTypeActive(Long id, boolean active) {
        RequestType rt = requestTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RequestType not found"));
        rt.setActive(active);
        return requestTypeRepository.save(rt);
    }

    public RequestTypeViewDto toRequestTypeViewDto(RequestType rt) {
        RequestTypeViewDto dto = new RequestTypeViewDto();
        dto.setId(rt.getId());
        dto.setName(rt.getName());
        dto.setDescription(rt.getDescription());
        dto.setCategory(rt.getCategory());
        dto.setSlaDays(rt.getSlaDays());
        dto.setActive(rt.getActive());
        dto.setRequiredAttachments(rt.getRequiredAttachments());
        dto.setDepartmentId(rt.getDepartment() != null ? rt.getDepartment().getId() : null);
        return dto;
    }

    // =========================================================
    // SCHEDULES
    // =========================================================

    /**
     * ADMIN: Ορισμός/Ενημέρωση ωραρίου (schedule) για ραντεβού ανά υπηρεσία/τμήμα και ημέρα.
     * Αν δεν υπάρχει schedule για (departmentId, dayOfWeek) → δημιουργείται.
     * Αν υπάρχει → γίνεται update.
     */
    @Transactional
    public DepartmentSchedule upsertDepartmentSchedule(Long departmentId, DayOfWeek dayOfWeek, DepartmentScheduleDto dto) {
        if (dto.getStartTime() == null) throw new RuntimeException("startTime is required");
        if (dto.getEndTime() == null) throw new RuntimeException("endTime is required");
        if (!dto.getStartTime().isBefore(dto.getEndTime())) throw new RuntimeException("startTime must be before endTime");

        Department dep = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        DepartmentSchedule s = departmentScheduleRepository
                .findByDepartmentIdAndDayOfWeek(departmentId, dayOfWeek)
                .orElseGet(() -> {
                    DepartmentSchedule ns = new DepartmentSchedule();
                    ns.setDepartment(dep);
                    ns.setDayOfWeek(dayOfWeek);
                    return ns;
                });

        s.setStartTime(dto.getStartTime());
        s.setEndTime(dto.getEndTime());

        return departmentScheduleRepository.save(s);
    }

    /**
     * ADMIN: Λίστα όλων των schedules ενός department.
     */
    @Transactional(readOnly = true)
    public List<DepartmentSchedule> getDepartmentSchedules(Long departmentId) {
        return departmentScheduleRepository.findByDepartmentIdOrderByDayOfWeekAsc(departmentId);
    }

    /**
     * ADMIN: Διαγραφή schedule συγκεκριμένης ημέρας.
     */
    @Transactional
    public void deleteDepartmentSchedule(Long departmentId, DayOfWeek dayOfWeek) {
        DepartmentSchedule s = departmentScheduleRepository
                .findByDepartmentIdAndDayOfWeek(departmentId, dayOfWeek)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        departmentScheduleRepository.delete(s);
    }

    public DepartmentScheduleDto toScheduleDto(DepartmentSchedule s) {
        DepartmentScheduleDto dto = new DepartmentScheduleDto();
        dto.setDepartmentId(s.getDepartment() != null ? s.getDepartment().getId() : null);
        dto.setDayOfWeek(s.getDayOfWeek().name());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        return dto;
    }

    // =========================================================
    // Parsing helpers (για να μη σε νοιάζει αν DTO έχει String ή Integer)
    // =========================================================

    private Integer parsePositiveInt(Object value, String field, boolean required) {
        Integer n = parseInt(value, field, required);
        if (n != null && n <= 0) throw new RuntimeException(field + " must be > 0");
        return n;
    }

    private Integer parseNonNegativeInt(Object value, String field, boolean required) {
        Integer n = parseInt(value, field, required);
        if (n != null && n < 0) throw new RuntimeException(field + " must be >= 0");
        return n;
    }

    private Integer parseInt(Object value, String field, boolean required) {
        if (value == null) {
            if (required) throw new RuntimeException(field + " is required");
            return null;
        }

        if (value instanceof Integer i) return i;

        if (value instanceof String s) {
            String trimmed = s.trim();
            if (trimmed.isEmpty()) {
                if (required) throw new RuntimeException(field + " is required");
                return null;
            }
            try {
                return Integer.parseInt(trimmed);
            } catch (NumberFormatException e) {
                throw new RuntimeException(field + " must be an integer");
            }
        }

        // fallback (π.χ. Long κλπ)
        try {
            return Integer.valueOf(value.toString());
        } catch (Exception e) {
            throw new RuntimeException(field + " must be an integer");
        }
    }
}
