package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.workflow.CaseWorkflowConfigResponse;
import com.maharashtra.rccms.dto.workflow.CaseWorkflowConfigSaveRequest;
import com.maharashtra.rccms.dto.workflow.NoticeTemplateResponse;
import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.model.master.CaseWorkflowConfig;
import com.maharashtra.rccms.model.master.NoticeTemplate;
import com.maharashtra.rccms.repository.CaseCategoryRepository;
import com.maharashtra.rccms.repository.NoticeTemplateRepository;
import com.maharashtra.rccms.service.CaseWorkflowConfigService;
import com.maharashtra.rccms.workflow.config.CaseWorkflowDefinition;
import com.maharashtra.rccms.workflow.config.WorkflowConfigJsonMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/workflow")
@SuppressWarnings("null")
public class AdminWorkflowController {

    private final CaseWorkflowConfigService workflowConfigService;
    private final CaseCategoryRepository caseCategoryRepository;
    private final NoticeTemplateRepository noticeTemplateRepository;
    private final WorkflowConfigJsonMapper jsonMapper;

    public AdminWorkflowController(
            CaseWorkflowConfigService workflowConfigService,
            CaseCategoryRepository caseCategoryRepository,
            NoticeTemplateRepository noticeTemplateRepository,
            WorkflowConfigJsonMapper jsonMapper
    ) {
        this.workflowConfigService = workflowConfigService;
        this.caseCategoryRepository = caseCategoryRepository;
        this.noticeTemplateRepository = noticeTemplateRepository;
        this.jsonMapper = jsonMapper;
    }

    @GetMapping("/configs")
    public ResponseEntity<?> listConfigs() {
        List<CaseWorkflowConfigResponse> out = caseCategoryRepository.findAll().stream()
                .map(this::toConfigResponse)
                .toList();
        return ResponseEntity.ok(out);
    }

    @GetMapping("/configs/{caseCategoryId}")
    public ResponseEntity<?> getConfig(@PathVariable Long caseCategoryId) {
        return ResponseEntity.ok(toConfigResponse(caseCategoryRepository.findById(caseCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Case category not found."))));
    }

    @PutMapping("/configs")
    public ResponseEntity<?> saveConfig(@RequestBody CaseWorkflowConfigSaveRequest request) {
        try {
            if (request.getCaseCategoryId() == null) {
                throw new IllegalArgumentException("caseCategoryId is required.");
            }
            CaseWorkflowDefinition def = request.getDefinition() != null
                    ? request.getDefinition()
                    : CaseWorkflowDefinition.suitDefault();
            CaseWorkflowConfig saved = workflowConfigService.saveForCategory(request.getCaseCategoryId(), def);
            CaseWorkflowConfigResponse out = new CaseWorkflowConfigResponse();
            out.setId(saved.getId());
            out.setCaseCategoryId(request.getCaseCategoryId());
            out.setBlueprintCode(saved.getBlueprintCode());
            out.setActive(saved.isActive());
            out.setDefinition(jsonMapper.fromJson(saved.getConfigJson()));
            return ResponseEntity.ok(out);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/notice-templates")
    public ResponseEntity<?> listNoticeTemplates(@RequestParam Long caseCategoryId) {
        List<NoticeTemplateResponse> items = noticeTemplateRepository.findByCaseCategoryIdOrderByNoticeTypeAsc(caseCategoryId)
                .stream()
                .map(AdminWorkflowController::toTemplateResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/notice-templates")
    public ResponseEntity<?> saveNoticeTemplate(@RequestBody NoticeTemplateResponse request) {
        try {
            if (request.getCaseCategoryId() == null || request.getNoticeType() == null) {
                throw new IllegalArgumentException("caseCategoryId and noticeType are required.");
            }
            CaseCategory category = caseCategoryRepository.findById(request.getCaseCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Case category not found."));
            NoticeTemplate row = noticeTemplateRepository
                    .findByCaseCategoryIdAndNoticeTypeIgnoreCaseAndActiveTrue(category.getId(), request.getNoticeType())
                    .orElseGet(NoticeTemplate::new);
            row.setCaseCategory(category);
            row.setNoticeType(request.getNoticeType().trim());
            row.setName(request.getName() != null ? request.getName() : request.getNoticeType());
            row.setBodyHtml(request.getBodyHtml() != null ? request.getBodyHtml() : "");
            row.setVersionNo(request.getVersionNo() > 0 ? request.getVersionNo() : 1);
            row.setActive(request.isActive());
            row = noticeTemplateRepository.save(row);
            return ResponseEntity.ok(toTemplateResponse(row));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    private CaseWorkflowConfigResponse toConfigResponse(CaseCategory category) {
        CaseWorkflowConfigResponse out = new CaseWorkflowConfigResponse();
        out.setCaseCategoryId(category.getId());
        out.setCaseCategoryCode(category.getCode());
        CaseWorkflowConfig entity = workflowConfigService.findEntityForCategoryId(category.getId());
        if (entity != null) {
            out.setId(entity.getId());
            out.setBlueprintCode(entity.getBlueprintCode());
            out.setActive(entity.isActive());
            out.setDefinition(jsonMapper.fromJson(entity.getConfigJson()));
        } else {
            CaseWorkflowDefinition def = CaseWorkflowDefinition.suitDefault();
            out.setBlueprintCode(def.getBlueprintCode());
            out.setActive(false);
            out.setDefinition(def);
        }
        return out;
    }

    private static NoticeTemplateResponse toTemplateResponse(NoticeTemplate row) {
        NoticeTemplateResponse out = new NoticeTemplateResponse();
        out.setId(row.getId());
        out.setCaseCategoryId(row.getCaseCategory().getId());
        out.setNoticeType(row.getNoticeType());
        out.setName(row.getName());
        out.setBodyHtml(row.getBodyHtml());
        out.setVersionNo(row.getVersionNo());
        out.setActive(row.isActive());
        return out;
    }
}
