package com.maharashtra.rccms.service;

import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.model.master.CaseWorkflowConfig;
import com.maharashtra.rccms.repository.CaseCategoryRepository;
import com.maharashtra.rccms.repository.CaseWorkflowConfigRepository;
import com.maharashtra.rccms.workflow.config.CaseWorkflowDefinition;
import com.maharashtra.rccms.workflow.config.WorkflowConfigJsonMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuppressWarnings("null")
public class CaseWorkflowConfigService {

    private final CaseWorkflowConfigRepository configRepository;
    private final CaseCategoryRepository caseCategoryRepository;
    private final WorkflowConfigJsonMapper jsonMapper;

    public CaseWorkflowConfigService(
            CaseWorkflowConfigRepository configRepository,
            CaseCategoryRepository caseCategoryRepository,
            WorkflowConfigJsonMapper jsonMapper
    ) {
        this.configRepository = configRepository;
        this.caseCategoryRepository = caseCategoryRepository;
        this.jsonMapper = jsonMapper;
    }

    @Transactional(readOnly = true)
    public CaseWorkflowDefinition resolveForCategory(CaseCategory category) {
        if (category == null || category.getId() == null) {
            return CaseWorkflowDefinition.suitDefault();
        }
        return configRepository.findByCaseCategoryIdAndActiveTrue(category.getId())
                .map(row -> jsonMapper.fromJson(row.getConfigJson()))
                .orElse(CaseWorkflowDefinition.suitDefault());
    }

    @Transactional(readOnly = true)
    public CaseWorkflowDefinition resolveForCategoryId(Long caseCategoryId) {
        if (caseCategoryId == null) {
            return CaseWorkflowDefinition.suitDefault();
        }
        CaseCategory category = caseCategoryRepository.findById(caseCategoryId).orElse(null);
        return resolveForCategory(category);
    }

    @Transactional(readOnly = true)
    public CaseWorkflowConfig findEntityForCategoryId(Long caseCategoryId) {
        if (caseCategoryId == null) {
            return null;
        }
        return configRepository.findByCaseCategoryId(caseCategoryId).orElse(null);
    }

    @Transactional
    public CaseWorkflowConfig saveForCategory(Long caseCategoryId, CaseWorkflowDefinition definition) {
        CaseCategory category = caseCategoryRepository.findById(caseCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Case category not found."));
        if (definition == null) {
            definition = CaseWorkflowDefinition.suitDefault();
        }
        CaseWorkflowConfig row = configRepository.findByCaseCategoryId(caseCategoryId)
                .orElseGet(CaseWorkflowConfig::new);
        row.setCaseCategory(category);
        row.setBlueprintCode(definition.getBlueprintCode() != null ? definition.getBlueprintCode() : "DEFAULT");
        row.setConfigJson(jsonMapper.toJson(definition));
        row.setActive(true);
        return configRepository.save(row);
    }
}
