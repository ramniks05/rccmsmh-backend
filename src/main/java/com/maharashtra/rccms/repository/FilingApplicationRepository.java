package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.filing.FilingApplication;
import com.maharashtra.rccms.model.filing.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FilingApplicationRepository extends JpaRepository<FilingApplication, Long> {

    Optional<FilingApplication> findByClientApplicationRef(UUID clientApplicationRef);

    List<FilingApplication> findByOfficeIdAndStatusOrderBySubmittedAtDescCreatedAtDesc(
            Long officeId,
            ApplicationStatus status
    );

    Optional<FilingApplication> findByIdAndOfficeIdAndStatus(
            Long id,
            Long officeId,
            ApplicationStatus status
    );
}
