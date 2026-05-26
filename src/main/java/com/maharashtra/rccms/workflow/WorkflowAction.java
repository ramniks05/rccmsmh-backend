package com.maharashtra.rccms.workflow;

/**
 * Stable action codes returned in {@code allowedActions} and validated before mutations.
 */
public enum WorkflowAction {
    // Filing / scrutiny
    SAVE_DRAFT,
    SUBMIT_APPLICATION,
    RETURN_FOR_CORRECTION,
    FORWARD_TO_PO,
    PO_RETURN_TO_CLERK,
    PO_REJECT,
    PO_ACCEPT_CASE,

    // Case / hearing
    SCHEDULE_HEARING,

    // Notice (per hearing)
    DRAFT_NOTICE,
    UPDATE_NOTICE,
    FINALIZE_NOTICE,
    SIGN_NOTICE,
    SERVE_NOTICE,
    /** One-shot: draft (if needed), finalize, sign, and serve to selected parties. */
    SERVE_NOTICE_TO_PARTY,
    REVERT_NOTICE,

    // Roznama / proceeding
    DRAFT_ROZNAMA,
    UPDATE_ROZNAMA,
    FINALIZE_ROZNAMA,
    SIGN_ROZNAMA,
    /** One-shot: save content, finalize, sign, and apply hearing outcome (ADJOURN or FINAL). */
    COMPLETE_ROZNAMA,
    REVERT_ROZNAMA,
    RESCHEDULE_HEARING,

    // Judgment
    PO_DRAFT_JUDGMENT,
    UPDATE_PO_JUDGMENT,
    SEND_JUDGMENT_TO_CLERK,
    CLERK_UPDATE_JUDGMENT,
    SUBMIT_JUDGMENT_TO_PO,
    REVERT_JUDGMENT_TO_CLERK,
    FINALIZE_JUDGMENT,
    PUBLISH_JUDGMENT,
    /** One-shot: finalize (if under PO scrutiny), sign, publish, and dispose case. */
    SIGN_AND_PUBLISH_JUDGMENT
}
