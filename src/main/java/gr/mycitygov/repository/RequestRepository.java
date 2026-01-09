package gr.mycitygov.repository;

import gr.mycitygov.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByProtocolNumber(String protocolNumber);
}

