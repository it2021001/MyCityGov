package gr.mycitygov.controller;

import gr.mycitygov.dto.attachment.RequestAttachmentViewDto;
import gr.mycitygov.model.RequestAttachment;
import gr.mycitygov.repository.RequestAttachmentRepository;
import gr.mycitygov.service.RequestAttachmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/attachments")
public class RequestAttachmentController {

    private final RequestAttachmentService attachmentService;
    private final RequestAttachmentRepository attachmentRepository;

    public RequestAttachmentController(RequestAttachmentService attachmentService,
                                       RequestAttachmentRepository attachmentRepository) {
        this.attachmentService = attachmentService;
        this.attachmentRepository = attachmentRepository;
    }

    @PostMapping(consumes = "multipart/form-data")
    public RequestAttachment upload(@PathVariable Long requestId,
                                    @RequestParam String docType,
                                    @RequestParam MultipartFile file) {
        return attachmentService.addAttachment(requestId, docType, file);
    }


    @GetMapping
    public List<RequestAttachmentViewDto> list(@PathVariable Long requestId) {
        return attachmentRepository.findByRequestId(requestId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private RequestAttachmentViewDto toDto(RequestAttachment a) {
        RequestAttachmentViewDto dto = new RequestAttachmentViewDto();
        dto.setId(a.getId());
        dto.setDocType(a.getDocType());
        dto.setOriginalFilename(a.getOriginalFilename());
        dto.setContentType(a.getContentType());
        dto.setFileSize(a.getFileSize());
        dto.setUploadedAt(a.getUploadedAt());
        return dto;
    }
}
