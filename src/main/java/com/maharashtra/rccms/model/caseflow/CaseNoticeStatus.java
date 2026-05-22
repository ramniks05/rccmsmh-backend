package com.maharashtra.rccms.model.caseflow;

public enum CaseNoticeStatus {
    /** @deprecated Legacy clerk drafts; new notices use PO_DRAFT. */
    CLERK_DRAFT,
    /** PO draft — notice is created and edited by Presiding Officer only. */
    PO_DRAFT,
    PO_SCRUTINY,
    PO_FINALIZED,
    PO_SIGNED,
    SERVED
}
