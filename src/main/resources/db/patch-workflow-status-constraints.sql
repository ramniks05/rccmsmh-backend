-- Run once if submit-to-po returns 500 due to case_*_status_check constraint.
-- Hibernate ddl-auto=update does not widen existing PostgreSQL CHECK constraints.

ALTER TABLE case_notice DROP CONSTRAINT IF EXISTS case_notice_status_check;
ALTER TABLE case_notice ADD CONSTRAINT case_notice_status_check
    CHECK (status IN ('CLERK_DRAFT', 'PO_SCRUTINY', 'PO_FINALIZED', 'PO_SIGNED', 'SERVED'));

ALTER TABLE case_order_sheet DROP CONSTRAINT IF EXISTS case_order_sheet_status_check;
ALTER TABLE case_order_sheet ADD CONSTRAINT case_order_sheet_status_check
    CHECK (status IN ('CLERK_DRAFT', 'PO_SCRUTINY', 'PO_FINALIZED', 'PO_SIGNED'));

ALTER TABLE case_judgment_workflow DROP CONSTRAINT IF EXISTS case_judgment_workflow_status_check;
ALTER TABLE case_judgment_workflow ADD CONSTRAINT case_judgment_workflow_status_check
    CHECK (status IN ('CLERK_DRAFT', 'PO_SCRUTINY', 'PO_FINALIZED', 'PUBLISHED'));
