package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.CaseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseCategoryRepository extends JpaRepository<CaseCategory, Long> {

    List<CaseCategory> findByNextCategory_Id(Long nextCategoryId);
}
