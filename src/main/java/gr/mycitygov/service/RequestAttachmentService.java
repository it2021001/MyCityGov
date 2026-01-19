package gr.mycitygov.service;

import gr.mycitygov.dto.attachment.RequestAttachmentViewDto;
import gr.mycitygov.model.Request;
import gr.mycitygov.model.RequestAttachment;
import gr.mycitygov.repository.RequestAttachmentRepository;
import gr.mycitygov.repository.RequestRepository;
import gr.mycitygov.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RequestAttachmentService {

    private final RequestRepository requestRepository;
    private final RequestAttachmentRepository attachmentRepository;
    private final UserRepository userRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public RequestAttachmentService(RequestRepository requestRepository,
                                    RequestAttachmentRepository attachmentRepository,
                                    UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // Public API (Controller calls)
    // =========================

    @Transactional
    public RequestAttachment addAttachment(Long requestId, MultipartFile file) {

        // 1) Basic validation
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        // 2) PDF validation
        String contentType = file.getContentType();
        String originalNameLower = (file.getOriginalFilename() == null) ? "" : file.getOriginalFilename().toLowerCase();

        boolean isPdf = "application/pdf".equalsIgnoreCase(contentType) || originalNameLower.endsWith(".pdf");
        if (!isPdf) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        // 3) Load request + AUTHZ
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        assertCanAccessRequest(request); // ownership check

        // 4) Max attachments check (based on requestType.requiredAttachments)
        Integer required = request.getRequestType().getRequiredAttachments();
        if (required != null && required > 0) {
            long alreadyUploaded = attachmentRepository.countByRequestId(requestId);
            if (alreadyUploaded >= required) {
                throw new RuntimeException("Maximum number of attachments reached (" + required + ")");
            }
        }

        // 5) Store file
        String original = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank())
                ? "file.pdf"
                : file.getOriginalFilename();

        String safeOriginal = original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String stored = UUID.randomUUID() + "_" + safeOriginal;

        try {
            Path folder = Path.of(uploadDir, "requests", String.valueOf(requestId));
            Files.createDirectories(folder);

            Path target = folder.resolve(stored);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            RequestAttachment a = new RequestAttachment();
            a.setRequest(request);
            a.setOriginalFilename(original);
            a.setStoredFilename(stored);
            a.setContentType(contentType == null ? "application/octet-stream" : contentType);
            a.setFileSize(file.getSize());
            a.setStoragePath(target.toString());
            a.setUploadedAt(LocalDateTime.now());

            return attachmentRepository.save(a);

        } catch (Exception e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<RequestAttachmentViewDto> listAttachments(Long requestId) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        assertCanAccessRequest(request); // ownership check

        return attachmentRepository.findByRequestId(requestId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void deleteAttachment(Long requestId, Long attachmentId) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        assertCanAccessRequest(request); // ownership check

        RequestAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        // ασφάλεια: να ανήκει στο σωστό request
        if (!attachment.getRequest().getId().equals(requestId)) {
            throw new RuntimeException("Attachment does not belong to this request");
        }

        // διαγραφή αρχείου από disk
        try {
            Files.deleteIfExists(Path.of(attachment.getStoragePath()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from storage");
        }

        // διαγραφή από DB
        attachmentRepository.delete(attachment);
    }

    // =========================
    // Authorization helper
    // =========================

    private void assertCanAccessRequest(Request request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return;

        boolean isCitizen = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CITIZEN"));

        if (isCitizen) {
            Long currentUserId = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new AccessDeniedException("User not found"))
                    .getId();

            if (!request.getCitizenUserId().equals(currentUserId)) {
                throw new AccessDeniedException("Forbidden");
            }
            return;
        }

        // EMPLOYEE: θα το υλοποιήσουμε όταν φτιάξουμε employee endpoints (department access)
        throw new AccessDeniedException("Forbidden");
    }

    private RequestAttachmentViewDto toDto(RequestAttachment a) {
        RequestAttachmentViewDto dto = new RequestAttachmentViewDto();
        dto.setId(a.getId());
        dto.setOriginalFilename(a.getOriginalFilename());
        dto.setContentType(a.getContentType());
        dto.setFileSize(a.getFileSize());
        dto.setUploadedAt(a.getUploadedAt());
        return dto;
    }
}
