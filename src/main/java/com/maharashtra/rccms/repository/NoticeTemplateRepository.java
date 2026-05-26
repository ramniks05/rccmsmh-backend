package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.NoticeTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeTemplateRepository extends JpaRepository<NoticeTemplate, Long> {
    Optional<NoticeTemplate> findByCaseCategoryIdAndNoticeTypeIgnoreCaseAndActiveTrue(
            Long caseCategoryId,
            String noticeType
    );

    List<NoticeTemplate> findByCaseCategoryIdOrderByNoticeTypeAsc(Long caseCategoryId);
}
