package gr.mycitygov.service;

import gr.mycitygov.model.Request;
import gr.mycitygov.model.RequestAttachment;
import gr.mycitygov.repository.RequestAttachmentRepository;
import gr.mycitygov.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RequestAttachmentService {

    private final RequestRepository requestRepository;
    private final RequestAttachmentRepository attachmentRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public RequestAttachmentService(RequestRepository requestRepository,
                                    RequestAttachmentRepository attachmentRepository) {
        this.requestRepository = requestRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @Transactional
    public RequestAttachment addAttachment(Long requestId, String docType, MultipartFile file) {

        // 1) Basic validation
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        if (docType == null || docType.isBlank()) {
            throw new RuntimeException("docType is required");
        }

        // 2) PDF validation
        String contentType = file.getContentType();
        String originalNameLower = (file.getOriginalFilename() == null) ? "" : file.getOriginalFilename().toLowerCase();

        boolean isPdf = "application/pdf".equalsIgnoreCase(contentType) || originalNameLower.endsWith(".pdf");
        if (!isPdf) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        // 3) Load request
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // 4) (Optional) Max attachments check (based on requestType.requiredAttachments)
        // Αν θέλεις να ΜΗΝ επιτρέπεις παραπάνω από το required_attachments, άστο ενεργό.
        // Αν θέλεις να επιτρέπεις extra, σχολίασέ το.
        Integer required = request.getRequestType().getRequiredAttachments(); // χρειάζεται να υπάρχει στο entity
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
            a.setDocType(docType.trim());
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
}
