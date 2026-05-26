package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.workflow.NoticeTemplateResolvedResponse;
import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import com.maharashtra.rccms.model.filing.FilingApplication;
import com.maharashtra.rccms.model.master.NoticeTemplate;
import com.maharashtra.rccms.repository.FilingApplicationRepository;
import com.maharashtra.rccms.repository.NoticeTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@SuppressWarnings("null")
public class NoticeTemplateService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final NoticeTemplateRepository noticeTemplateRepository;
    private final FilingApplicationRepository filingApplicationRepository;

    public NoticeTemplateService(
            NoticeTemplateRepository noticeTemplateRepository,
            FilingApplicationRepository filingApplicationRepository
    ) {
        this.noticeTemplateRepository = noticeTemplateRepository;
        this.filingApplicationRepository = filingApplicationRepository;
    }

    @Transactional(readOnly = true)
    public NoticeTemplateResolvedResponse resolveForCase(
            CaseRegistry caseRow,
            String noticeType,
            LocalDate hearingDate,
            String partyNamesBlock
    ) {
        String type = noticeType != null && !noticeType.isBlank() ? noticeType.trim() : "HEARING_NOTICE";
        Long categoryId = caseRow.getCaseCategory() != null ? caseRow.getCaseCategory().getId() : null;

        NoticeTemplate template = null;
        if (categoryId != null) {
            template = noticeTemplateRepository
                    .findByCaseCategoryIdAndNoticeTypeIgnoreCaseAndActiveTrue(categoryId, type)
                    .orElse(null);
        }

        String body = template != null ? template.getBodyHtml() : defaultHearingNoticeHtml();
        String merged = mergePlaceholders(body, caseRow, hearingDate, partyNamesBlock);

        NoticeTemplateResolvedResponse out = new NoticeTemplateResolvedResponse();
        out.setNoticeType(type);
        out.setTemplateId(template != null ? template.getId() : null);
        out.setTemplateVersion(template != null ? template.getVersionNo() : 0);
        out.setBodyHtml(merged);
        return out;
    }

    private String mergePlaceholders(
            String body,
            CaseRegistry caseRow,
            LocalDate hearingDate,
            String partyNamesBlock
    ) {
        String caseNo = caseRow.getCaseNo() != null ? caseRow.getCaseNo() : "";
        String category = caseRow.getCaseCategory() != null ? caseRow.getCaseCategory().getName() : "";
        String office = caseRow.getOffice() != null ? caseRow.getOffice().getName() : "";
        String hearing = hearingDate != null ? hearingDate.format(DATE_FMT) : "";
        String parties = partyNamesBlock != null ? partyNamesBlock : "";

        FilingApplication app = null;
        if (caseRow.getFilingApplicationId() != null) {
            app = filingApplicationRepository.findById(caseRow.getFilingApplicationId()).orElse(null);
        }
        String applicationNo = app != null && app.getApplicationNo() != null ? app.getApplicationNo() : "";

        return body
                .replace("{{caseNo}}", caseNo)
                .replace("{{case_no}}", caseNo)
                .replace("{{applicationNo}}", applicationNo)
                .replace("{{application_no}}", applicationNo)
                .replace("{{caseCategory}}", category)
                .replace("{{case_category}}", category)
                .replace("{{officeName}}", office)
                .replace("{{office_name}}", office)
                .replace("{{hearingDate}}", hearing)
                .replace("{{hearing_date}}", hearing)
                .replace("{{partyNames}}", parties)
                .replace("{{party_names}}", parties)
                .replace("{{today}}", LocalDate.now().format(DATE_FMT));
    }

    private static String defaultHearingNoticeHtml() {
        return """
                <div>
                  <p><strong>Case No:</strong> {{caseNo}}</p>
                  <p><strong>Category:</strong> {{caseCategory}}</p>
                  <p><strong>Hearing Date:</strong> {{hearingDate}}</p>
                  <p><strong>Parties:</strong></p>
                  <div>{{partyNames}}</div>
                  <p>You are hereby notified to remain present on the above date.</p>
                </div>
                """;
    }
}
