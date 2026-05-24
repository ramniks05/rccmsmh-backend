package com.maharashtra.rccms.service;

import com.maharashtra.rccms.model.EmployeePosting;
import com.maharashtra.rccms.model.caseflow.CaseHearing;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflow;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflowStatus;
import com.maharashtra.rccms.model.caseflow.CaseNotice;
import com.maharashtra.rccms.model.caseflow.CaseNoticeStatus;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheet;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheetStatus;
import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import com.maharashtra.rccms.model.filing.ApplicationStatus;
import com.maharashtra.rccms.model.filing.FilingApplication;
import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.workflow.WorkflowAction;
import com.maharashtra.rccms.workflow.config.CaseWorkflowDefinition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class WorkflowPolicyService {

    private static final Long PRESIDING_OFFICER_DESIGNATION_ID = 1L;

    private final CaseWorkflowConfigService workflowConfigService;

    public WorkflowPolicyService(CaseWorkflowConfigService workflowConfigService) {
        this.workflowConfigService = workflowConfigService;
    }

    public CaseWorkflowDefinition definitionFor(CaseRegistry caseRow) {
        CaseCategory category = caseRow != null ? caseRow.getCaseCategory() : null;
        return workflowConfigService.resolveForCategory(category);
    }

    public CaseWorkflowDefinition definitionFor(FilingApplication app) {
        return workflowConfigService.resolveForCategory(app != null ? app.getCaseCategory() : null);
    }

    public void requireAction(WorkflowAction action, Set<WorkflowAction> allowed) {
        if (allowed == null || !allowed.contains(action)) {
            throw new IllegalArgumentException("Action not allowed in current workflow state: " + action.name());
        }
    }

    public boolean isPo(EmployeePosting posting) {
        Long designationId = posting.getDesignation() != null ? posting.getDesignation().getId() : null;
        return Objects.equals(designationId, PRESIDING_OFFICER_DESIGNATION_ID);
    }

    public boolean isClerk(EmployeePosting posting) {
        return !isPo(posting);
    }

    public List<String> filingAllowedActions(FilingApplication app, EmployeePosting posting) {
        Set<WorkflowAction> actions = new LinkedHashSet<>();
        CaseWorkflowDefinition def = definitionFor(app);
        boolean po = isPo(posting);
        boolean clerk = isClerk(posting);

        if (app.getStatus() == ApplicationStatus.DRAFT) {
            actions.add(WorkflowAction.SAVE_DRAFT);
            actions.add(WorkflowAction.SUBMIT_APPLICATION);
            return toNames(actions);
        }

        if (app.getStatus() != ApplicationStatus.SUBMITTED) {
            return List.of();
        }
        if (Boolean.TRUE.equals(app.getPoApproved()) || Boolean.TRUE.equals(app.getPoRejected())) {
            return List.of();
        }

        if (clerk && def.getScrutiny().getClerkActions() != null) {
            if (!Boolean.TRUE.equals(app.getForwardedToPo())) {
                if (def.getScrutiny().getClerkActions().contains("RETURN_FOR_CORRECTION")) {
                    actions.add(WorkflowAction.RETURN_FOR_CORRECTION);
                }
                if (def.getScrutiny().getClerkActions().contains("FORWARD_TO_PO")) {
                    actions.add(WorkflowAction.FORWARD_TO_PO);
                }
            }
        }

        if (po && Boolean.TRUE.equals(app.getForwardedToPo()) && def.getScrutiny().getPoActions() != null) {
            if (def.getScrutiny().getPoActions().contains("ACCEPT")) {
                actions.add(WorkflowAction.PO_ACCEPT_CASE);
            }
            if (def.getScrutiny().getPoActions().contains("REJECT")) {
                actions.add(WorkflowAction.PO_REJECT);
            }
            if (def.getScrutiny().getPoActions().contains("RETURN_TO_CLERK")) {
                actions.add(WorkflowAction.PO_RETURN_TO_CLERK);
            }
        }

        return toNames(actions);
    }

    public List<String> caseAllowedActions(
            CaseRegistry caseRow,
            EmployeePosting posting,
            CaseHearing activeHearing,
            CaseNotice draftNoticeForHearing,
            CaseOrderSheet orderSheet,
            CaseJudgmentWorkflow judgment
    ) {
        Set<WorkflowAction> actions = new LinkedHashSet<>();
        if (caseRow == null || "DISPOSED".equalsIgnoreCase(caseRow.getStatus())) {
            return List.of();
        }
        boolean po = isPo(posting);
        if (po) {
            actions.add(WorkflowAction.SCHEDULE_HEARING);
        }
        for (String code : noticeAllowed(caseRow, posting, activeHearing, draftNoticeForHearing)) {
            actions.add(WorkflowAction.valueOf(code));
        }
        for (String code : roznamaAllowed(caseRow, posting, activeHearing, orderSheet)) {
            actions.add(WorkflowAction.valueOf(code));
        }
        for (String code : judgmentAllowed(caseRow, posting, judgment)) {
            actions.add(WorkflowAction.valueOf(code));
        }
        return toNames(actions);
    }

    public List<String> noticeAllowed(
            CaseRegistry caseRow,
            EmployeePosting posting,
            CaseHearing hearing,
            CaseNotice existingDraft
    ) {
        Set<WorkflowAction> actions = new LinkedHashSet<>();
        CaseWorkflowDefinition def = definitionFor(caseRow);
        if (!"PO_ONLY".equalsIgnoreCase(def.getNotice().getMode()) && !poOnlyNotice(def)) {
            return List.of();
        }
        if (!isPo(posting) || hearing == null || "DISPOSED".equalsIgnoreCase(caseRow.getStatus())) {
            return List.of();
        }
        if (Boolean.TRUE.equals(hearing.getNoticeServed())) {
            return List.of();
        }

        actions.add(WorkflowAction.SERVE_NOTICE_TO_PARTY);
        return toNames(actions);
    }

    public List<String> roznamaAllowed(
            CaseRegistry caseRow,
            EmployeePosting posting,
            CaseHearing hearing,
            CaseOrderSheet sheet
    ) {
        Set<WorkflowAction> actions = new LinkedHashSet<>();
        CaseWorkflowDefinition def = definitionFor(caseRow);
        if (!isPo(posting) || hearing == null) {
            return List.of();
        }
        if (def.getRoznama().isRequiresNoticeServed() && !Boolean.TRUE.equals(hearing.getNoticeServed())) {
            if (def.getRoznama().isAllowReschedule()) {
                actions.add(WorkflowAction.RESCHEDULE_HEARING);
            }
            return toNames(actions);
        }

        if (def.getRoznama().isAllowReschedule()) {
            actions.add(WorkflowAction.RESCHEDULE_HEARING);
        }

        boolean signedForThisHearing = sheet != null
                && sheet.getStatus() == CaseOrderSheetStatus.PO_SIGNED
                && sheet.getCurrentHearing() != null
                && Objects.equals(sheet.getCurrentHearing().getId(), hearing.getId());
        if (!signedForThisHearing) {
            actions.add(WorkflowAction.COMPLETE_ROZNAMA);
        }
        return toNames(actions);
    }

    public List<String> judgmentAllowed(
            CaseRegistry caseRow,
            EmployeePosting posting,
            CaseJudgmentWorkflow row
    ) {
        Set<WorkflowAction> actions = new LinkedHashSet<>();
        CaseWorkflowDefinition def = definitionFor(caseRow);
        boolean poThenClerk = "PO_THEN_CLERK".equalsIgnoreCase(def.getJudgment().getMode());
        boolean po = isPo(posting);
        boolean clerk = isClerk(posting);

        if (caseRow != null && po && "READY_FOR_JUDGMENT".equalsIgnoreCase(caseRow.getStatus())) {
            actions.add(WorkflowAction.PO_DRAFT_JUDGMENT);
            actions.add(WorkflowAction.UPDATE_PO_JUDGMENT);
            return toNames(actions);
        }

        if (row == null || row.getStatus() == null) {
            if (po && poThenClerk) {
                actions.add(WorkflowAction.PO_DRAFT_JUDGMENT);
            } else if (clerk) {
                actions.add(WorkflowAction.CLERK_UPDATE_JUDGMENT);
            }
            return toNames(actions);
        }

        CaseJudgmentWorkflowStatus status = row.getStatus();
        if (status == CaseJudgmentWorkflowStatus.PUBLISHED) {
            return List.of();
        }

        if (poThenClerk) {
            if (status == CaseJudgmentWorkflowStatus.PO_DRAFT && po) {
                actions.add(WorkflowAction.UPDATE_PO_JUDGMENT);
                actions.add(WorkflowAction.SEND_JUDGMENT_TO_CLERK);
            } else if (status == CaseJudgmentWorkflowStatus.CLERK_DRAFT && clerk) {
                actions.add(WorkflowAction.CLERK_UPDATE_JUDGMENT);
                actions.add(WorkflowAction.SUBMIT_JUDGMENT_TO_PO);
            } else if (status == CaseJudgmentWorkflowStatus.CLERK_DRAFT && po) {
                actions.add(WorkflowAction.REVERT_JUDGMENT_TO_CLERK);
            } else if (status == CaseJudgmentWorkflowStatus.PO_SCRUTINY && po) {
                actions.add(WorkflowAction.FINALIZE_JUDGMENT);
                actions.add(WorkflowAction.REVERT_JUDGMENT_TO_CLERK);
            } else if (status == CaseJudgmentWorkflowStatus.PO_FINALIZED && po) {
                actions.add(WorkflowAction.PUBLISH_JUDGMENT);
                actions.add(WorkflowAction.REVERT_JUDGMENT_TO_CLERK);
            }
        } else {
            if (status == CaseJudgmentWorkflowStatus.CLERK_DRAFT && clerk) {
                actions.add(WorkflowAction.CLERK_UPDATE_JUDGMENT);
                actions.add(WorkflowAction.SUBMIT_JUDGMENT_TO_PO);
            } else if (status == CaseJudgmentWorkflowStatus.PO_SCRUTINY && po) {
                actions.add(WorkflowAction.FINALIZE_JUDGMENT);
                actions.add(WorkflowAction.REVERT_JUDGMENT_TO_CLERK);
            } else if (status == CaseJudgmentWorkflowStatus.PO_FINALIZED && po) {
                actions.add(WorkflowAction.PUBLISH_JUDGMENT);
            }
        }
        return toNames(actions);
    }

    public static Set<CaseNoticeStatus> editableNoticeStatuses() {
        return EnumSet.of(CaseNoticeStatus.PO_DRAFT, CaseNoticeStatus.CLERK_DRAFT, CaseNoticeStatus.PO_SCRUTINY);
    }

    private static boolean poOnlyNotice(CaseWorkflowDefinition def) {
        return def.getNotice().getMode() == null
                || "PO_ONLY".equalsIgnoreCase(def.getNotice().getMode());
    }

    private static List<String> toNames(Set<WorkflowAction> actions) {
        List<String> out = new ArrayList<>();
        for (WorkflowAction a : actions) {
            out.add(a.name());
        }
        return out;
    }
}
