package gr.mycitygov.service;

import gr.mycitygov.model.RequestType;
import gr.mycitygov.enums.RequestCategory;
import gr.mycitygov.repository.RequestTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestTypeService {

    private final RequestTypeRepository requestTypeRepository;

    public RequestTypeService(RequestTypeRepository requestTypeRepository) {
        this.requestTypeRepository = requestTypeRepository;
    }


    public List<RequestType> getAllActive() {
        return requestTypeRepository.findByActiveTrue();
    }

    public List<RequestType> getActiveByCategory(RequestCategory category) {
        return requestTypeRepository.findByCategoryAndActiveTrue(category);
    }

    public RequestType getById(Long id) {
        return requestTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RequestType not found"));
    }
}
