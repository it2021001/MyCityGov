package gr.mycitygov.service;

import gr.mycitygov.enums.DocumentType;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static gr.mycitygov.enums.DocumentType.*;

@Service
public class RequiredDocsService {

    // requestTypeId -> required docs
    private final Map<Long, Set<DocumentType>> requiredDocs = Map.of(
            1L, Set.of(ID_COPY, PROOF_OF_ADDRESS),

            2L, Set.of(ID_COPY, RESPONSIBLE_DECLARATION),

            3L, Set.of(ID_COPY, AMKA_CERTIFICATE),

            4L, Set.of(ID_COPY, RESPONSIBLE_DECLARATION),

            5L, Set.of(ID_COPY, SITE_PHOTO_OR_PLAN, PAYMENT_RECEIPT),

            6L, Set.of(ID_COPY, PROPERTY_TITLE_OR_LEASE, TECHNICAL_PLANS),

            7L, Set.of(ID_COPY, AFM_CERTIFICATE),

            8L, Set.of(ID_COPY, AFM_CERTIFICATE, DEBT_NOTICE, RESPONSIBLE_DECLARATION),

            9L, Set.of(ID_COPY, INCOME_PROOF, FAMILY_CERTIFICATE),

            10L, Set.of(ID_COPY, MEDICAL_REPORT, FAMILY_CERTIFICATE, INCOME_PROOF)
    );

    public Set<DocumentType> requiredFor(Long requestTypeId) {
        return requiredDocs.getOrDefault(requestTypeId, Set.of());
    }
}

