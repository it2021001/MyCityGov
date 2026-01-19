package gr.mycitygov.repository;

import gr.mycitygov.model.CitizenProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CitizenProfileRepository extends JpaRepository<CitizenProfile, Long> {
    boolean existsByEmail(String email);
    Optional<CitizenProfile> findByEmail(String email);
}

