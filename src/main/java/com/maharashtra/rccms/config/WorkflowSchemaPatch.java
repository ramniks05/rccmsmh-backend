package com.maharashtra.rccms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Widens PostgreSQL CHECK constraints for workflow status enums after adding PO_SCRUTINY.
 * Hibernate ddl-auto=update does not alter existing check constraints.
 */
@Component
public class WorkflowSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(WorkflowSchemaPatch.class);

    private final JdbcTemplate jdbcTemplate;

    public WorkflowSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        patchNoticeStatus();
        patchOrderSheetStatus();
        patchJudgmentWorkflowStatus();
    }

    private void patchNoticeStatus() {
        try {
            jdbcTemplate.execute("ALTER TABLE case_notice DROP CONSTRAINT IF EXISTS case_notice_status_check");
            jdbcTemplate.execute("""
                    ALTER TABLE case_notice ADD CONSTRAINT case_notice_status_check
                    CHECK (status IN ('CLERK_DRAFT', 'PO_SCRUTINY', 'PO_FINALIZED', 'PO_SIGNED', 'SERVED'))
                    """);
            log.info("Patched case_notice_status_check for PO_SCRUTINY");
        } catch (Exception ex) {
            log.warn("Could not patch case_notice status constraint: {}", ex.getMessage());
        }
    }

    private void patchOrderSheetStatus() {
        try {
            jdbcTemplate.execute("ALTER TABLE case_order_sheet DROP CONSTRAINT IF EXISTS case_order_sheet_status_check");
            jdbcTemplate.execute("""
                    ALTER TABLE case_order_sheet ADD CONSTRAINT case_order_sheet_status_check
                    CHECK (status IN ('CLERK_DRAFT', 'PO_SCRUTINY', 'PO_FINALIZED', 'PO_SIGNED'))
                    """);
            log.info("Patched case_order_sheet_status_check for PO_SCRUTINY");
        } catch (Exception ex) {
            log.warn("Could not patch case_order_sheet status constraint: {}", ex.getMessage());
        }
    }

    private void patchJudgmentWorkflowStatus() {
        try {
            jdbcTemplate.execute("ALTER TABLE case_judgment_workflow DROP CONSTRAINT IF EXISTS case_judgment_workflow_status_check");
            jdbcTemplate.execute("""
                    ALTER TABLE case_judgment_workflow ADD CONSTRAINT case_judgment_workflow_status_check
                    CHECK (status IN ('CLERK_DRAFT', 'PO_SCRUTINY', 'PO_FINALIZED', 'PUBLISHED'))
                    """);
            log.info("Patched case_judgment_workflow_status_check for PO_SCRUTINY");
        } catch (Exception ex) {
            log.warn("Could not patch case_judgment_workflow status constraint: {}", ex.getMessage());
        }
    }
}
