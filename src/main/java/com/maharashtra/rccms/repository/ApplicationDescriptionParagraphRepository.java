package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.filing.ApplicationDescriptionParagraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationDescriptionParagraphRepository extends JpaRepository<ApplicationDescriptionParagraph, Long> {

    List<ApplicationDescriptionParagraph> findByApplicationIdOrderByParaNoAsc(Long applicationId);

    void deleteByApplicationId(Long applicationId);
}
