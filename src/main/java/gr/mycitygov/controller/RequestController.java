package gr.mycitygov.controller;

import gr.mycitygov.dto.request.CreateRequestDto;
import gr.mycitygov.dto.request.EmployeeDecisionDto;
import gr.mycitygov.dto.request.UpdateRequestStatusDto;
import gr.mycitygov.model.Request;
import gr.mycitygov.model.RequestNote;
import gr.mycitygov.service.RequestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    // =========================================================
    // CITIZEN
    // =========================================================

    /**
     * CITIZEN: Δημιουργία αιτήματος.
     * Παίρνουμε AFM/AMKA/ID_NUMBER από DB (CitizenProfile) με βάση τον logged-in citizen.
     */
    @PreAuthorize("hasRole('CITIZEN')")
    @PostMapping("/citizen")
    public Request createCitizen(@RequestBody CreateRequestDto dto, Principal principal) {
        return requestService.createForCitizen(principal.getName(), dto);
    }

    /**
     * CITIZEN: Λίστα αιτημάτων μου.
     */
    @PreAuthorize("hasRole('CITIZEN')")
    @GetMapping("/citizen")
    public List<Request> myRequests(Principal principal) {
        return requestService.getMyRequests(principal.getName());
    }

    /**
     * CITIZEN: Προβολή ενός αιτήματος μου (by id).
     */
    @PreAuthorize("hasRole('CITIZEN')")
    @GetMapping("/citizen/{id}")
    public Request myRequest(@PathVariable Long id, Principal principal) {
        return requestService.getMyRequest(principal.getName(), id);
    }

    // =========================================================
    // EMPLOYEE
    // =========================================================

    /**
     * EMPLOYEE: Λίστα αιτημάτων που ανήκουν στη δική του υπηρεσία (department).
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/employee")
    public List<Request> departmentRequests(Principal principal) {
        return requestService.getDepartmentRequests(principal.getName());
    }

    /**
     * EMPLOYEE: Ανάληψη "επόμενου" αιτήματος με προτεραιότητα:
     * - dueAt ASC (λήγει πιο σύντομα)
     * - createdAt ASC (πιο παλιό)
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/employee/take-next")
    public Request takeNext(Principal principal) {
        return requestService.takeNextPriority(principal.getName());
    }

    /**
     * EMPLOYEE: Ενημέρωση status + σχόλιο/παρατήρηση.
     * Απαιτεί το αίτημα να είναι ανατεθειμένο στον ίδιο employee.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/employee/{id}/status")
    public Request updateStatus(@PathVariable Long id,
                                @RequestBody UpdateRequestStatusDto dto,
                                Principal principal) {
        return requestService.updateStatusAsEmployee(principal.getName(), id, dto);
    }

    /**
     * EMPLOYEE: Έγκριση/Απόρριψη αιτήματος με τεκμηρίωση.
     * approved=true  => COMPLETED
     * approved=false => REJECTED
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/employee/{id}/decision")
    public Request decision(@PathVariable Long id,
                            @RequestBody EmployeeDecisionDto dto,
                            Principal principal) {
        return requestService.decideAsEmployee(principal.getName(), id, dto);
    }

    /**
     * EMPLOYEE: Προβολή notes ενός αιτήματος (π.χ. για να δεις status/decision notes).
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/employee/{id}/notes")
    public List<RequestNote> notes(@PathVariable Long id) {
        return requestService.getNotesForRequest(id);
    }
}
