package com.maharashtra.rccms.config;

import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.model.master.CaseWorkflowConfig;
import com.maharashtra.rccms.model.master.NoticeTemplate;
import com.maharashtra.rccms.repository.CaseCategoryRepository;
import com.maharashtra.rccms.repository.CaseWorkflowConfigRepository;
import com.maharashtra.rccms.repository.NoticeTemplateRepository;
import com.maharashtra.rccms.service.CaseWorkflowConfigService;
import com.maharashtra.rccms.workflow.config.CaseWorkflowDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Optional startup seed for workflow blueprint and notice templates.
 * Disabled by default — enable when you are ready: {@code rccms.workflow.seed-on-startup=true}
 * Until then, {@link com.maharashtra.rccms.service.WorkflowPolicyService} uses in-code Suit defaults.
 */
@Component
@Order(20)
@ConditionalOnProperty(name = "rccms.workflow.seed-on-startup", havingValue = "true")
public class WorkflowMasterDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(WorkflowMasterDataSeeder.class);

    private final CaseCategoryRepository caseCategoryRepository;
    private final CaseWorkflowConfigRepository workflowConfigRepository;
    private final NoticeTemplateRepository noticeTemplateRepository;
    private final CaseWorkflowConfigService workflowConfigService;

    public WorkflowMasterDataSeeder(
            CaseCategoryRepository caseCategoryRepository,
            CaseWorkflowConfigRepository workflowConfigRepository,
            NoticeTemplateRepository noticeTemplateRepository,
            CaseWorkflowConfigService workflowConfigService
    ) {
        this.caseCategoryRepository = caseCategoryRepository;
        this.workflowConfigRepository = workflowConfigRepository;
        this.noticeTemplateRepository = noticeTemplateRepository;
        this.workflowConfigService = workflowConfigService;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<CaseCategory> categories = caseCategoryRepository.findAll();
        for (CaseCategory category : categories) {
            seedWorkflowConfig(category);
            if (isSuitCategory(category)) {
                seedNoticeTemplate(category);
            }
        }
    }

    private void seedWorkflowConfig(CaseCategory category) {
        if (workflowConfigRepository.findByCaseCategoryId(category.getId()).isPresent()) {
            return;
        }
        CaseWorkflowDefinition def = CaseWorkflowDefinition.suitDefault();
        CaseWorkflowConfig row = workflowConfigService.saveForCategory(category.getId(), def);
        log.info("Seeded workflow config {} for case category {}", row.getBlueprintCode(), category.getCode());
    }

    private void seedNoticeTemplate(CaseCategory category) {
        if (noticeTemplateRepository.findByCaseCategoryIdAndNoticeTypeIgnoreCaseAndActiveTrue(
                category.getId(),
                "HEARING_NOTICE"
        ).isPresent()) {
            return;
        }
        NoticeTemplate t = new NoticeTemplate();
        t.setCaseCategory(category);
        t.setNoticeType("HEARING_NOTICE");
        t.setName("Hearing Notice - " + category.getName());
        t.setBodyHtml(defaultNoticeHtml());
        t.setVersionNo(1);
        t.setActive(true);
        noticeTemplateRepository.save(t);
        log.info("Seeded notice template HEARING_NOTICE for category {}", category.getCode());
    }

    private static boolean isSuitCategory(CaseCategory category) {
        if (category.getCode() == null) {
            return false;
        }
        String code = category.getCode().trim().toUpperCase();
        return code.contains("SUIT") || "CS".equals(code);
    }

    private static String defaultNoticeHtml() {
        return """
                <div style="font-family: serif;">
                  <h3 style="text-align:center;">NOTICE OF HEARING</h3>
                  <p><strong>Case No:</strong> {{caseNo}}</p>
                  <p><strong>Application No:</strong> {{applicationNo}}</p>
                  <p><strong>Category:</strong> {{caseCategory}}</p>
                  <p><strong>Office:</strong> {{officeName}}</p>
                  <p><strong>Date of Hearing:</strong> {{hearingDate}}</p>
                  <p><strong>Parties:</strong></p>
                  <div>{{partyNames}}</div>
                  <p>You are hereby required to remain present before this Court on the date mentioned above.</p>
                  <p>Date: {{today}}</p>
                </div>
                """;
    }
}
