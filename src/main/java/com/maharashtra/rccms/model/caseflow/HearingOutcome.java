package com.maharashtra.rccms.model.caseflow;

/**
 * PO decision recorded when signing roznamma for a hearing.
 * ADJOURN and FINAL are mutually exclusive.
 */
public enum HearingOutcome {
    /** Matter adjourned; schedule next hearing (and serve notice again). */
    ADJOURN,
    /** No further hearing; proceed to judgment. */
    FINAL
}
