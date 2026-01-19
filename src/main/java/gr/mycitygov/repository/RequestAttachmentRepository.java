package gr.mycitygov.repository;

import gr.mycitygov.model.RequestAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestAttachmentRepository extends JpaRepository<RequestAttachment, Long> {
    long countByRequestId(Long requestId);
    List<RequestAttachment> findByRequestId(Long requestId);
}



