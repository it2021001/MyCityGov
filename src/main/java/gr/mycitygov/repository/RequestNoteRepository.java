package gr.mycitygov.repository;

import gr.mycitygov.model.RequestNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestNoteRepository extends JpaRepository<RequestNote, Long> {

    List<RequestNote> findByRequestId(Long requestId);
}
