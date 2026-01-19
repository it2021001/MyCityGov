package gr.mycitygov.controller;

import gr.mycitygov.dto.attachment.RequestAttachmentViewDto;
import gr.mycitygov.model.RequestAttachment;
import gr.mycitygov.service.RequestAttachmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/attachments")
public class RequestAttachmentController {

    private final RequestAttachmentService attachmentService;

    public RequestAttachmentController(RequestAttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    /**
     * CITIZEN / ADMIN:
     * Ανέβασμα συνημμένου αρχείου (PDF) σε αίτημα.
     * Ο πολίτης μπορεί να ανεβάσει αρχείο μόνο σε αιτήματα που του ανήκουν.
     */
    @PreAuthorize("hasAnyRole('CITIZEN','ADMIN')")
    @PostMapping(consumes = "multipart/form-data")
    public RequestAttachment upload(@PathVariable Long requestId,
                                    @RequestParam MultipartFile file) {
        return attachmentService.addAttachment(requestId, file);
    }

    /**
     * CITIZEN / ADMIN:
     * Λίστα συνημμένων αρχείων ενός αιτήματος.
     * Ο πολίτης βλέπει μόνο συνημμένα από δικά του αιτήματα.
     */
    @PreAuthorize("hasAnyRole('CITIZEN','ADMIN')")
    @GetMapping
    public List<RequestAttachmentViewDto> list(@PathVariable Long requestId) {
        return attachmentService.listAttachments(requestId);
    }

    /**
     * CITIZEN / ADMIN:
     * Διαγραφή συνημμένου αρχείου από αίτημα.
     * Επιτρέπεται μόνο αν το αίτημα ανήκει στον πολίτη.
     */
    @PreAuthorize("hasAnyRole('CITIZEN','ADMIN')")
    @DeleteMapping("/{attachmentId}")
    public void delete(@PathVariable Long requestId,
                       @PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(requestId, attachmentId);
    }
}
