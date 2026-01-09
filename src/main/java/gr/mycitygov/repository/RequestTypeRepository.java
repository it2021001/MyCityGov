package gr.mycitygov.repository;

import gr.mycitygov.model.RequestType;
import gr.mycitygov.enums.RequestCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestTypeRepository extends JpaRepository<RequestType, Long> {

    Optional<RequestType> findByName(String name);

    List<RequestType> findByActiveTrue();

    List<RequestType> findByCategoryAndActiveTrue(RequestCategory category);

}

