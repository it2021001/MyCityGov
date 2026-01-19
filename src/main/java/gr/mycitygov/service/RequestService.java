package gr.mycitygov.service;

import gr.mycitygov.dto.request.CreateRequestDto;
import gr.mycitygov.dto.request.EmployeeDecisionDto;
import gr.mycitygov.dto.request.UpdateRequestStatusDto;
import gr.mycitygov.enums.RequestCategory;
import gr.mycitygov.enums.RequestNoteType;
import gr.mycitygov.enums.RequestStatus;
import gr.mycitygov.model.CitizenProfile;
import gr.mycitygov.model.EmployeeProfile;
import gr.mycitygov.model.Request;
import gr.mycitygov.model.RequestNote;
import gr.mycitygov.model.RequestType;
import gr.mycitygov.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final UserRepository userRepository;
    private final CitizenProfileRepository citizenProfileRepository;

    // EMPLOYEE
    private final EmployeeProfileRepository employeeProfileRepository;
    private final RequestNoteRepository requestNoteRepository;

    // Format: MCG-YYYYMMDD-0001
    private final AtomicInteger dailyCounter = new AtomicInteger(0);
    private String lastDate = "";

    public RequestService(RequestRepository requestRepository,
                          RequestTypeRepository requestTypeRepository,
                          UserRepository userRepository,
                          CitizenProfileRepository citizenProfileRepository,
                          EmployeeProfileRepository employeeProfileRepository,
                          RequestNoteRepository requestNoteRepository) {
        this.requestRepository = requestRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.userRepository = userRepository;
        this.citizenProfileRepository = citizenProfileRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.requestNoteRepository = requestNoteRepository;
    }

    // =========================================================
    // CITIZEN
    // =========================================================

    @Transactional
    public Request createForCitizen(String citizenUsername, CreateRequestDto dto) {
        Long citizenUserId = resolveUserIdByUsername(citizenUsername);

        CitizenProfile cp = citizenProfileRepository.findById(citizenUserId)
                .orElseThrow(() -> new RuntimeException("Citizen profile not found"));

        return createRequest(
                citizenUserId,
                dto.getRequestTypeId(),
                dto.getDescription(),
                dto.getLocationText(),
                dto.getAddress(),
                dto.getPurpose(),
                cp.getAfm(),
                cp.getAmka(),
                cp.getCitizenIdNumber()
        );
    }

    @Transactional(readOnly = true)
    public List<Request> getMyRequests(String citizenUsername) {
        Long citizenUserId = resolveUserIdByUsername(citizenUsername);
        return requestRepository.findByCitizenUserIdOrderByCreatedAtDesc(citizenUserId);
    }

    @Transactional(readOnly = true)
    public Request getMyRequest(String citizenUsername, Long requestId) {
        Long citizenUserId = resolveUserIdByUsername(citizenUsername);
        return requestRepository.findByIdAndCitizenUserId(requestId, citizenUserId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    // =========================================================
    // EMPLOYEE
    // =========================================================

    /**
     * EMPLOYEE: Λίστα αιτημάτων που ανήκουν στη δική του υπηρεσία (department).
     */
    @Transactional(readOnly = true)
    public List<Request> getDepartmentRequests(String employeeUsername) {
        Long employeeUserId = resolveUserIdByUsername(employeeUsername);
        Long deptId = resolveEmployeeDepartmentId(employeeUserId);
        return requestRepository.findByDepartmentIdOrderByCreatedAtDesc(deptId);
    }

    /**
     * EMPLOYEE: Ανάληψη "επόμενου" αιτήματος με προτεραιότητα:
     * - dueAt ASC (λήγει πιο σύντομα)
     * - createdAt ASC (πιο παλιό)
     */
    @Transactional
    public Request takeNextPriority(String employeeUsername) {
        Long employeeUserId = resolveUserIdByUsername(employeeUsername);
        Long deptId = resolveEmployeeDepartmentId(employeeUserId);

        List<Request> candidates = requestRepository.findPriorityUnassignedByDepartment(deptId, RequestStatus.SUBMITTED);
        if (candidates.isEmpty()) {
            throw new RuntimeException("No pending requests to take for your department");
        }

        Request r = candidates.get(0);

        r.setAssignedEmployeeUserId(employeeUserId);
        r.setStatus(RequestStatus.RECEIVED); // μόλις αναλήφθηκε
        requestRepository.save(r);

        addNote(r.getId(), employeeUserId, RequestNoteType.STATUS,
                "Ανάληψη αιτήματος (RECEIVED) με προτεραιότητα dueAt/createdAt");

        return r;
    }

    /**
     * EMPLOYEE: Ενημέρωση status + σχόλιο/παρατήρηση.
     * Κανόνας: το αίτημα πρέπει να ανήκει στο department του employee
     * και να είναι ανατεθειμένο στον ίδιο.
     */
    @Transactional
    public Request updateStatusAsEmployee(String employeeUsername, Long requestId, UpdateRequestStatusDto dto) {
        Long employeeUserId = resolveUserIdByUsername(employeeUsername);
        Long deptId = resolveEmployeeDepartmentId(employeeUserId);

        Request r = requestRepository.findByIdAndDepartmentId(requestId, deptId)
                .orElseThrow(() -> new RuntimeException("Request not found for your department"));

        if (r.getAssignedEmployeeUserId() == null || !r.getAssignedEmployeeUserId().equals(employeeUserId)) {
            throw new RuntimeException("You must take the request before updating it");
        }

        RequestStatus newStatus = RequestStatus.valueOf(dto.getStatus());

        // μικρός κανόνας: δεν αλλάζουμε CLOSED αιτήματα
        if (r.getStatus() == RequestStatus.COMPLETED || r.getStatus() == RequestStatus.REJECTED) {
            throw new RuntimeException("Request is already closed");
        }

        r.setStatus(newStatus);

        // αν κλείσει, κλείνουμε και closedAt
        if (newStatus == RequestStatus.COMPLETED || newStatus == RequestStatus.REJECTED) {
            r.setClosedAt(LocalDateTime.now());
        }

        requestRepository.save(r);

        // Note: STATUS change
        String text = (dto.getComment() == null || dto.getComment().isBlank())
                ? ("Αλλαγή κατάστασης σε: " + newStatus)
                : ("Αλλαγή κατάστασης σε: " + newStatus + " | " + dto.getComment().trim());

        addNote(r.getId(), employeeUserId, RequestNoteType.STATUS, text);

        return r;
    }

    /**
     * EMPLOYEE: Έγκριση/Απόρριψη με τεκμηρίωση.
     * approved=true  => COMPLETED
     * approved=false => REJECTED
     */
    @Transactional
    public Request decideAsEmployee(String employeeUsername, Long requestId, EmployeeDecisionDto dto) {
        Long employeeUserId = resolveUserIdByUsername(employeeUsername);
        Long deptId = resolveEmployeeDepartmentId(employeeUserId);

        Request r = requestRepository.findByIdAndDepartmentId(requestId, deptId)
                .orElseThrow(() -> new RuntimeException("Request not found for your department"));

        if (r.getAssignedEmployeeUserId() == null || !r.getAssignedEmployeeUserId().equals(employeeUserId)) {
            throw new RuntimeException("You must take the request before deciding");
        }

        if (dto.getReason() == null || dto.getReason().isBlank()) {
            throw new RuntimeException("Decision reason is required");
        }

        // κλείσιμο
        if (dto.isApproved()) {
            r.setStatus(RequestStatus.COMPLETED);
        } else {
            r.setStatus(RequestStatus.REJECTED);
        }
        r.setClosedAt(LocalDateTime.now());
        requestRepository.save(r);

        addNote(r.getId(), employeeUserId, RequestNoteType.DECISION, dto.getReason().trim());

        return r;
    }

    /**
     * EMPLOYEE: Προβολή notes ενός request (π.χ. για debugging ή UI).
     * (Μπορούμε να το περιορίσουμε με department/assignment, αλλά για τώρα κρατάμε απλό.)
     */
    @Transactional(readOnly = true)
    public List<RequestNote> getNotesForRequest(Long requestId) {
        return requestNoteRepository.findByRequestId(requestId);
    }

    // =========================================================
    // CORE CREATE (citizen)
    // =========================================================

    @Transactional
    public Request createRequest(
            Long citizenUserId,
            Long requestTypeId,
            String description,
            String locationText,   // PROBLEM_REPORT
            String address,        // APPLICATION
            String purpose,        // APPLICATION
            String afm,            // APPLICATION
            String amka,           // APPLICATION
            String citizenIdNumber // APPLICATION
    ) {

        if (citizenUserId == null) throw new RuntimeException("citizenUserId is required");

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

        if (requestType.getCategory() == RequestCategory.PROBLEM_REPORT) {
            if (locationText == null || locationText.isBlank()) {
                throw new RuntimeException("locationText is required for PROBLEM_REPORT");
            }
        }

        if (requestType.getCategory() == RequestCategory.APPLICATION) {
            if (address == null || address.isBlank()) throw new RuntimeException("address is required for APPLICATION");
            if (purpose == null || purpose.isBlank()) throw new RuntimeException("purpose is required for APPLICATION");
            if (afm == null || afm.isBlank()) throw new RuntimeException("afm is required for APPLICATION");
            if (amka == null || amka.isBlank()) throw new RuntimeException("amka is required for APPLICATION");
            if (citizenIdNumber == null || citizenIdNumber.isBlank()) throw new RuntimeException("citizenIdNumber is required for APPLICATION");

            if (!isValidAfm(afm)) throw new RuntimeException("Invalid AFM format (must be 9 digits)");
            if (!isValidAmka(amka)) throw new RuntimeException("Invalid AMKA format (must be 11 digits)");
            if (!isValidIdNumber(citizenIdNumber)) throw new RuntimeException("Invalid ID number format (e.g. AB123456)");
        }

        LocalDateTime now = LocalDateTime.now();

        Request r = new Request();
        r.setCitizenUserId(citizenUserId);
        r.setRequestType(requestType);

        r.setStatus(RequestStatus.SUBMITTED);
        r.setDescription(description);

        r.setCreatedAt(now);
        r.setDueAt(now.plusDays(requestType.getSlaDays()));

        r.setLocationText(blankToNull(locationText));
        r.setAddress(blankToNull(address));
        r.setPurpose(blankToNull(purpose));
        r.setAfm(blankToNull(afm));
        r.setAmka(blankToNull(amka));
        r.setCitizenIdNumber(blankToNull(citizenIdNumber));

        r.setProtocolNumber(generateProtocolNumber(now));

        return requestRepository.save(r);
    }

    // =========================================================
    // Helpers
    // =========================================================

    private void addNote(Long requestId, Long employeeUserId, RequestNoteType type, String text) {
        RequestNote note = new RequestNote();
        note.setRequestId(requestId);
        note.setEmployeeUserId(employeeUserId);
        note.setNoteType(type);
        note.setText(text.trim());
        requestNoteRepository.save(note);
    }

    private Long resolveUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getId();
    }

    private Long resolveEmployeeDepartmentId(Long employeeUserId) {
        EmployeeProfile ep = employeeProfileRepository.findById(employeeUserId)
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));
        return ep.getDepartmentId();
    }

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
        return normalized.matches("^[A-ZΑ-Ω]{1,2}\\d{6}$");
    }
}
