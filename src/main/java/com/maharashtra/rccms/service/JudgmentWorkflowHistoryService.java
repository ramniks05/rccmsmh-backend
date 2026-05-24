package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.workflow.JudgmentWorkflowHistoryResponse;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflow;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflowHistory;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflowStatus;
import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import com.maharashtra.rccms.repository.CaseJudgmentWorkflowHistoryRepository;
import com.maharashtra.rccms.workflow.WorkflowAction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings("null")
public class JudgmentWorkflowHistoryService {

    private final CaseJudgmentWorkflowHistoryRepository historyRepository;

    public JudgmentWorkflowHistoryService(CaseJudgmentWorkflowHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Transactional
    public void record(
            CaseRegistry caseRow,
            CaseJudgmentWorkflow workflow,
            CaseJudgmentWorkflowStatus from,
            CaseJudgmentWorkflowStatus to,
            WorkflowAction action,
            String summarySnapshot,
            String remarks,
            String actorRole,
            String actorLoginId
    ) {
        CaseJudgmentWorkflowHistory hist = new CaseJudgmentWorkflowHistory();
        hist.setCaseRegistry(caseRow);
        hist.setWorkflow(workflow);
        hist.setFromStatus(from != null ? from.name() : null);
        hist.setToStatus(to != null ? to.name() : null);
        hist.setActionCode(action != null ? action.name() : "UNKNOWN");
        hist.setSummarySnapshot(summarySnapshot);
        hist.setRemarks(remarks);
        hist.setActorRole(actorRole);
        hist.setActorLoginId(actorLoginId);
        historyRepository.save(hist);
    }

    @Transactional(readOnly = true)
    public List<JudgmentWorkflowHistoryResponse> listForCase(Long caseId) {
        List<JudgmentWorkflowHistoryResponse> out = new ArrayList<>();
        for (CaseJudgmentWorkflowHistory row : historyRepository.findByCaseRegistryIdOrderByCreatedAtAscIdAsc(caseId)) {
            JudgmentWorkflowHistoryResponse dto = new JudgmentWorkflowHistoryResponse();
            dto.setId(row.getId());
            dto.setFromStatus(row.getFromStatus());
            dto.setToStatus(row.getToStatus());
            dto.setActionCode(row.getActionCode());
            dto.setSummarySnapshot(row.getSummarySnapshot());
            dto.setRemarks(row.getRemarks());
            dto.setActorRole(row.getActorRole());
            dto.setActorLoginId(row.getActorLoginId());
            dto.setCreatedAt(row.getCreatedAt());
            out.add(dto);
        }
        return out;
    }
}
