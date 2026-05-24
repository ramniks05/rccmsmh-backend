package com.maharashtra.rccms.workflow.config;

import java.util.List;

/**
 * Workflow blueprint for a case category (e.g. Suit). Stored as JSON in {@code master_case_workflow_config}.
 */
public class CaseWorkflowDefinition {

    private String blueprintCode = "DEFAULT";
    private FilingConfig filing = new FilingConfig();
    private ScrutinyConfig scrutiny = new ScrutinyConfig();
    private NoticeConfig notice = new NoticeConfig();
    private RoznamaConfig roznama = new RoznamaConfig();
    private JudgmentConfig judgment = new JudgmentConfig();

    public String getBlueprintCode() {
        return blueprintCode;
    }

    public void setBlueprintCode(String blueprintCode) {
        this.blueprintCode = blueprintCode;
    }

    public FilingConfig getFiling() {
        return filing;
    }

    public void setFiling(FilingConfig filing) {
        this.filing = filing;
    }

    public ScrutinyConfig getScrutiny() {
        return scrutiny;
    }

    public void setScrutiny(ScrutinyConfig scrutiny) {
        this.scrutiny = scrutiny;
    }

    public NoticeConfig getNotice() {
        return notice;
    }

    public void setNotice(NoticeConfig notice) {
        this.notice = notice;
    }

    public RoznamaConfig getRoznama() {
        return roznama;
    }

    public void setRoznama(RoznamaConfig roznama) {
        this.roznama = roznama;
    }

    public JudgmentConfig getJudgment() {
        return judgment;
    }

    public void setJudgment(JudgmentConfig judgment) {
        this.judgment = judgment;
    }

    public static CaseWorkflowDefinition suitDefault() {
        CaseWorkflowDefinition d = new CaseWorkflowDefinition();
        d.setBlueprintCode("SUIT_STANDARD");

        FilingConfig filing = new FilingConfig();
        filing.setFilerTypes(List.of("ADVOCATE", "PARTY_IN_PERSON"));
        d.setFiling(filing);

        ScrutinyConfig scrutiny = new ScrutinyConfig();
        scrutiny.setClerkActions(List.of("RETURN_FOR_CORRECTION", "FORWARD_TO_PO"));
        scrutiny.setPoActions(List.of("ACCEPT", "REJECT", "RETURN_TO_CLERK"));
        d.setScrutiny(scrutiny);

        NoticeConfig notice = new NoticeConfig();
        notice.setMode("PO_ONLY");
        notice.setScope("PER_HEARING");
        notice.setSteps(List.of("DRAFT", "FINALIZE", "SIGN", "SERVE"));
        notice.setVisibleTo(List.of("CLERK", "PO", "APPLICANT", "ADVOCATE"));
        d.setNotice(notice);

        RoznamaConfig roznama = new RoznamaConfig();
        roznama.setRequiresNoticeServed(true);
        roznama.setUpdateMode("UPDATE_SAME_SHEET");
        roznama.setAllowReschedule(true);
        roznama.setAllowAttendance(true);
        d.setRoznama(roznama);

        JudgmentConfig judgment = new JudgmentConfig();
        judgment.setMode("PO_THEN_CLERK");
        judgment.setAuditTrailRequired(true);
        judgment.setSteps(List.of("PO_DRAFT", "CLERK_EDIT", "PO_SCRUTINY", "FINALIZE", "PUBLISH"));
        d.setJudgment(judgment);

        return d;
    }

    public static class FilingConfig {
        private List<String> filerTypes = List.of("ADVOCATE", "PARTY_IN_PERSON");

        public List<String> getFilerTypes() {
            return filerTypes;
        }

        public void setFilerTypes(List<String> filerTypes) {
            this.filerTypes = filerTypes;
        }
    }

    public static class ScrutinyConfig {
        private List<String> clerkActions = List.of("RETURN_FOR_CORRECTION", "FORWARD_TO_PO");
        private List<String> poActions = List.of("ACCEPT", "REJECT", "RETURN_TO_CLERK");

        public List<String> getClerkActions() {
            return clerkActions;
        }

        public void setClerkActions(List<String> clerkActions) {
            this.clerkActions = clerkActions;
        }

        public List<String> getPoActions() {
            return poActions;
        }

        public void setPoActions(List<String> poActions) {
            this.poActions = poActions;
        }
    }

    public static class NoticeConfig {
        private String mode = "PO_ONLY";
        private String scope = "PER_HEARING";
        private List<String> steps = List.of("DRAFT", "FINALIZE", "SIGN", "SERVE");
        private List<String> visibleTo = List.of("CLERK", "PO", "APPLICANT");

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public List<String> getSteps() {
            return steps;
        }

        public void setSteps(List<String> steps) {
            this.steps = steps;
        }

        public List<String> getVisibleTo() {
            return visibleTo;
        }

        public void setVisibleTo(List<String> visibleTo) {
            this.visibleTo = visibleTo;
        }
    }

    public static class RoznamaConfig {
        private boolean requiresNoticeServed = true;
        private String updateMode = "UPDATE_SAME_SHEET";
        private boolean allowReschedule = true;
        private boolean allowAttendance = true;

        public boolean isRequiresNoticeServed() {
            return requiresNoticeServed;
        }

        public void setRequiresNoticeServed(boolean requiresNoticeServed) {
            this.requiresNoticeServed = requiresNoticeServed;
        }

        public String getUpdateMode() {
            return updateMode;
        }

        public void setUpdateMode(String updateMode) {
            this.updateMode = updateMode;
        }

        public boolean isAllowReschedule() {
            return allowReschedule;
        }

        public void setAllowReschedule(boolean allowReschedule) {
            this.allowReschedule = allowReschedule;
        }

        public boolean isAllowAttendance() {
            return allowAttendance;
        }

        public void setAllowAttendance(boolean allowAttendance) {
            this.allowAttendance = allowAttendance;
        }
    }

    public static class JudgmentConfig {
        private String mode = "PO_THEN_CLERK";
        private boolean auditTrailRequired = true;
        private List<String> steps = List.of("PO_DRAFT", "CLERK_EDIT", "PO_SCRUTINY", "FINALIZE", "PUBLISH");

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public boolean isAuditTrailRequired() {
            return auditTrailRequired;
        }

        public void setAuditTrailRequired(boolean auditTrailRequired) {
            this.auditTrailRequired = auditTrailRequired;
        }

        public List<String> getSteps() {
            return steps;
        }

        public void setSteps(List<String> steps) {
            this.steps = steps;
        }
    }
}
