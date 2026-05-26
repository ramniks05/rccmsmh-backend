package com.maharashtra.rccms.model.caseflow;

public enum CaseOrderSheetStatus {
    /** @deprecated Legacy clerk drafts; new proceedings use PO_DRAFT. */
    CLERK_DRAFT,
    /** PO draft — proceeding/roznama written by Presiding Officer only. */
    PO_DRAFT,
    PO_SCRUTINY,
    PO_FINALIZED,
    PO_SIGNED
}
