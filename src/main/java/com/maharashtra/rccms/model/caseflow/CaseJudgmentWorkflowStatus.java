package com.maharashtra.rccms.model.caseflow;

public enum CaseJudgmentWorkflowStatus {
    /** PO initial draft (Suit workflow: PO drafts first). */
    PO_DRAFT,
    /** Clerk editing after PO sends for changes. */
    CLERK_DRAFT,
    /** Back with PO for review after clerk submit. */
    PO_SCRUTINY,
    PO_FINALIZED,
    PUBLISHED
}
