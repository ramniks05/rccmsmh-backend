package com.maharashtra.rccms.model.filing;

public enum ApplicationHistoryAction {
    DRAFT_SAVED,
    SUBMITTED,
    FORWARDED_TO_PO,
    RETURNED_TO_CLERK,
    PO_REJECTED,
    CASE_REGISTERED,
    /** Case proceeding: hearing scheduled. */
    HEARING_SCHEDULED,
    /** Case proceeding: notice drafted (officer view). */
    NOTICE_DRAFTED,
    /** Case proceeding: notice under PO scrutiny. */
    NOTICE_IN_PO_SCRUTINY,
    /** Case proceeding: notice finalized by PO. */
    NOTICE_FINALIZED,
    /** Case proceeding: notice digitally signed. */
    NOTICE_SIGNED,
    /** Case proceeding: notice served on parties. */
    NOTICE_SERVED,
    /** Case proceeding: order sheet (roznama) draft saved for current hearing. */
    ORDER_SHEET_DRAFT_SAVED,
    /** Case proceeding: order sheet submitted to PO scrutiny. */
    ORDER_SHEET_SUBMITTED_TO_PO,
    /** Case proceeding: order sheet finalized by PO. */
    ORDER_SHEET_FINALIZED,
    /** Case proceeding: order sheet signed (hearing proceeding completed). */
    ORDER_SHEET_SIGNED,
    /** Case proceeding: judgment draft saved by clerk. */
    JUDGMENT_DRAFT_SAVED,
    /** Case proceeding: judgment sent to PO. */
    JUDGMENT_SUBMITTED_TO_PO,
    /** Case proceeding: judgment finalized by PO. */
    JUDGMENT_FINALIZED,
    /** Case proceeding: judgment published. */
    JUDGMENT_PUBLISHED
}
