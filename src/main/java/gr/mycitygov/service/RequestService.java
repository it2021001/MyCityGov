package gr.mycitygov.service;

import gr.mycitygov.model.Request;
import gr.mycitygov.model.RequestType;
import gr.mycitygov.enums.RequestCategory;
import gr.mycitygov.enums.RequestStatus;
import gr.mycitygov.repository.RequestRepository;
import gr.mycitygov.repository.RequestTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestTypeRepository requestTypeRepository;

    // Απλό protocol sequence για DEV/εργασία.
    // Format: MCG-YYYYMMDD-0001
    private final AtomicInteger dailyCounter = new AtomicInteger(0);
    private String lastDate = "";

    public RequestService(RequestRepository requestRepository,
                          RequestTypeRepository requestTypeRepository) {
        this.requestRepository = requestRepository;
        this.requestTypeRepository = requestTypeRepository;
    }

    @Transactional
    public Request createRequest(
            Long requestTypeId,
            String description,
            String locationText,  // PROBLEM_REPORT
            String address,       // APPLICATION
            String purpose,       // APPLICATION
            String afm,           // APPLICATION
            String amka,          // APPLICATION
            String citizenIdNumber       // APPLICATION
    ) {

        RequestType requestType = requestTypeRepository.findById(requestTypeId)
                .orElseThrow(() -> new RuntimeException("RequestType not found"));

        if (!Boolean.TRUE.equals(requestType.getActive())) {
            throw new RuntimeException("RequestType is not active");
        }

        if (description == null || description.isBlank()) {
            throw new RuntimeException("description is required");
        }
        if (requestType.getSlaDays() <= 0) {
            throw new RuntimeException("Invalid SLA days for RequestType");
        }

        // Validation ανά category
        if (requestType.getCategory() == RequestCategory.PROBLEM_REPORT) {
            if (locationText == null || locationText.isBlank()) {
                throw new RuntimeException("locationText is required for PROBLEM_REPORT");
            }
            // Για PROBLEM_REPORT δεν απαιτούμε τα application fields:
            // address/purpose/afm/amka/citizenIdNumber μπορούν να μείνουν null.
        }

        if (requestType.getCategory() == RequestCategory.APPLICATION) {
            if (address == null || address.isBlank()) {
                throw new RuntimeException("address is required for APPLICATION");
            }
            if (purpose == null || purpose.isBlank()) {
                throw new RuntimeException("purpose is required for APPLICATION");
            }
            if (afm == null || afm.isBlank()) {
                throw new RuntimeException("afm is required for APPLICATION");
            }
            if (amka == null || amka.isBlank()) {
                throw new RuntimeException("amka is required for APPLICATION");
            }
            if (citizenIdNumber == null || citizenIdNumber.isBlank()) {
                throw new RuntimeException("citizenIdNumber is required for APPLICATION");
            }

            // Format validation (simple)
            if (!isValidAfm(afm)) {
                throw new RuntimeException("Invalid AFM format (must be 9 digits)");
            }
            if (!isValidAmka(amka)) {
                throw new RuntimeException("Invalid AMKA format (must be 11 digits)");
            }
            if (!isValidIdNumber(citizenIdNumber)) {
                throw new RuntimeException("Invalid ID number format (e.g., AB123456)");
            }
        }

        LocalDateTime now = LocalDateTime.now();

        Request r = new Request();
        r.setRequestType(requestType);
        r.setStatus(RequestStatus.SUBMITTED);

        r.setDescription(description);

        r.setCreatedAt(now);
        r.setDueAt(now.plusDays(requestType.getSlaDays()));

        // Extra fields
        r.setLocationText(blankToNull(locationText));
        r.setAddress(blankToNull(address));
        r.setPurpose(blankToNull(purpose));

        r.setAfm(blankToNull(afm));
        r.setAmka(blankToNull(amka));
        r.setCitizenIdNumber(blankToNull(citizenIdNumber));

        r.setProtocolNumber(generateProtocolNumber(now));

        return requestRepository.save(r);
    }

    /**
     * Δημιουργεί protocol number σε μορφή: MCG-YYYYMMDD-0001
     * (Reset κάθε μέρα)
     */
    private synchronized String generateProtocolNumber(LocalDateTime now) {
        String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (!date.equals(lastDate)) {
            lastDate = date;
            dailyCounter.set(0);
        }

        int seq = dailyCounter.incrementAndGet();
        return "MCG-" + date + "-" + String.format("%04d", seq);
    }

    private String blankToNull(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isValidAfm(String afm) {
        return afm != null && afm.matches("\\d{9}");
    }

    private boolean isValidAmka(String amka) {
        return amka != null && amka.matches("\\d{11}");
    }

    private boolean isValidIdNumber(String id) {
        if (id == null) return false;
        String normalized = id.trim().toUpperCase();

        // Επιτρέπει Α-Ω και A-Z
        // 1-2 γράμματα + 6 ψηφία (ΑΒ123456 ή A123456)
        return normalized.matches("^[A-ZΑ-Ω]{1,2}\\d{6}$");
    }

}

