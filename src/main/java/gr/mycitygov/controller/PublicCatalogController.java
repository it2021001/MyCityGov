package gr.mycitygov.controller;

import gr.mycitygov.dto.department.DepartmentViewDto;
import gr.mycitygov.dto.requesttype.RequestTypeViewDto;
import gr.mycitygov.model.Department;
import gr.mycitygov.model.RequestType;
import gr.mycitygov.service.DepartmentService;
import gr.mycitygov.service.RequestTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicCatalogController {

    private final DepartmentService departmentService;
    private final RequestTypeService requestTypeService;

    public PublicCatalogController(DepartmentService departmentService,
                                   RequestTypeService requestTypeService) {
        this.departmentService = departmentService;
        this.requestTypeService = requestTypeService;
    }

    /**
     * Public: Εμφανίζει τις διαθέσιμες υπηρεσίες (departments).
     * Δεν απαιτεί login.
     */
    @GetMapping("/departments")
    public List<DepartmentViewDto> departments() {
        return departmentService.findAll()
                .stream()
                .map(this::toDepartmentViewDto)
                .toList();
    }

    /**
     * Public: Εμφανίζει τον κατάλογο αιτημάτων (request types).
     * Δεν απαιτεί login.
     */
    @GetMapping("/request-types")
    public List<RequestTypeViewDto> requestTypes() {
        return requestTypeService.getAllActive()
                .stream()
                .map(this::toRequestTypeViewDto)
                .toList();
    }

    private DepartmentViewDto toDepartmentViewDto(Department d) {
        DepartmentViewDto dto = new DepartmentViewDto();
        dto.setId(d.getId());
        dto.setName(d.getName());
        return dto;
    }

    private RequestTypeViewDto toRequestTypeViewDto(RequestType rt) {
        RequestTypeViewDto dto = new RequestTypeViewDto();
        dto.setId(rt.getId());
        dto.setName(rt.getName());
        dto.setDescription(rt.getDescription());
        dto.setSlaDays(rt.getSlaDays());
        dto.setCategory(rt.getCategory());
        dto.setDepartmentId(rt.getDepartment() != null ? rt.getDepartment().getId() : null);
        dto.setActive(rt.getActive());
        dto.setRequiredAttachments(rt.getRequiredAttachments());
        return dto;
    }
}
