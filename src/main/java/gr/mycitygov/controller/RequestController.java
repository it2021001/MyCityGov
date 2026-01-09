package gr.mycitygov.controller;

import gr.mycitygov.dto.request.CreateRequestDto;
import gr.mycitygov.model.Request;
import gr.mycitygov.repository.RequestRepository;
import gr.mycitygov.service.RequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;
    private final RequestRepository requestRepository;

    public RequestController(RequestService requestService, RequestRepository requestRepository) {
        this.requestService = requestService;
        this.requestRepository = requestRepository;
    }

    @PostMapping
    public Request create(@RequestBody CreateRequestDto dto) {
        return requestService.createRequest(
                dto.getRequestTypeId(),
                dto.getDescription(),
                dto.getLocationText(),
                dto.getAddress(),
                dto.getPurpose(),
                dto.getAfm(),
                dto.getAmka(),
                dto.getCitizenIdNumber()
        );
    }

    @GetMapping
    public List<Request> getAll() {
        return requestRepository.findAll();
    }
}
