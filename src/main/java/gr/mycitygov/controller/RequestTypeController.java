package gr.mycitygov.controller;

import gr.mycitygov.dto.requesttype.RequestTypeViewDto;
import gr.mycitygov.model.RequestType;
import gr.mycitygov.enums.RequestCategory;
import gr.mycitygov.service.RequestTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-types")
public class RequestTypeController {

    private final RequestTypeService requestTypeService;

    public RequestTypeController(RequestTypeService requestTypeService) {
        this.requestTypeService = requestTypeService;
    }

    @GetMapping
    public List<RequestTypeViewDto> getActive(@RequestParam(required = false) RequestCategory category) {

        List<RequestType> types = (category == null)
                ? requestTypeService.getAllActive()
                : requestTypeService.getActiveByCategory(category);

        return types.stream().map(this::toDto).toList();
    }

    private RequestTypeViewDto toDto(RequestType rt) {
        RequestTypeViewDto dto = new RequestTypeViewDto();
        dto.setId(rt.getId());
        dto.setName(rt.getName());
        dto.setDescription(rt.getDescription());
        dto.setSlaDays(rt.getSlaDays());
        dto.setCategory(rt.getCategory());
        dto.setDepartmentId(rt.getDepartment().getId());
        dto.setRequiredAttachments(rt.getRequiredAttachments());
        return dto;
    }
}

