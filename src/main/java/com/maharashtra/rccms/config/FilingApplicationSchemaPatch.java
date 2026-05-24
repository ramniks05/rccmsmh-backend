package com.maharashtra.rccms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FilingApplicationSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(FilingApplicationSchemaPatch.class);

    private final JdbcTemplate jdbcTemplate;

    public FilingApplicationSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        patchFilingApplicationHeader();
        patchDisputedOrderSearch();
        patchDisputedLandColumns();
        ensureDescriptionParagraphTable();
    }

    private void patchFilingApplicationHeader() {
        addColumn("filing_application", "affidavit_text", "ALTER TABLE filing_application ADD COLUMN affidavit_text TEXT");
        addColumn("filing_application", "prayer_text", "ALTER TABLE filing_application ADD COLUMN prayer_text TEXT");
        addColumn("filing_application", "form_snapshot_json", "ALTER TABLE filing_application ADD COLUMN form_snapshot_json TEXT");
    }

    private void patchDisputedOrderSearch() {
        addColumn("application_disputed_order", "land_channel", "ALTER TABLE application_disputed_order ADD COLUMN land_channel VARCHAR(32)");
        addColumn("application_disputed_order", "search_criteria_code", "ALTER TABLE application_disputed_order ADD COLUMN search_criteria_code VARCHAR(64)");
        addColumn("application_disputed_order", "search_display_text", "ALTER TABLE application_disputed_order ADD COLUMN search_display_text VARCHAR(1024)");
        addColumn("application_disputed_order", "resolved_inward_number", "ALTER TABLE application_disputed_order ADD COLUMN resolved_inward_number VARCHAR(255)");
        addColumn("application_disputed_order", "location_json", "ALTER TABLE application_disputed_order ADD COLUMN location_json TEXT");
        addColumn("application_disputed_order", "criteria_values_json", "ALTER TABLE application_disputed_order ADD COLUMN criteria_values_json TEXT");
        addColumn("application_disputed_order", "mutation_snapshot_json", "ALTER TABLE application_disputed_order ADD COLUMN mutation_snapshot_json TEXT");
        addColumn("application_disputed_order", "external_refs_json", "ALTER TABLE application_disputed_order ADD COLUMN external_refs_json TEXT");
        addColumn("application_disputed_order", "notice9_json", "ALTER TABLE application_disputed_order ADD COLUMN notice9_json TEXT");
    }

    private void patchDisputedLandColumns() {
        addColumn("application_disputed_land", "parent_cts_no", "ALTER TABLE application_disputed_land ADD COLUMN parent_cts_no VARCHAR(128)");
        addColumn("application_disputed_land", "sub_cts_no", "ALTER TABLE application_disputed_land ADD COLUMN sub_cts_no VARCHAR(128)");
        addColumn("application_disputed_land", "total_area", "ALTER TABLE application_disputed_land ADD COLUMN total_area VARCHAR(64)");
        addColumn("application_disputed_land", "disputed_area", "ALTER TABLE application_disputed_land ADD COLUMN disputed_area VARCHAR(64)");
        addColumn("application_disputed_land", "area_unit", "ALTER TABLE application_disputed_land ADD COLUMN area_unit VARCHAR(32)");
        addColumn("application_disputed_land", "land_holders_text", "ALTER TABLE application_disputed_land ADD COLUMN land_holders_text VARCHAR(2048)");
        addColumn("application_disputed_land", "land_detail_json", "ALTER TABLE application_disputed_land ADD COLUMN land_detail_json TEXT");
    }

    private void ensureDescriptionParagraphTable() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    """
                            SELECT COUNT(*) FROM information_schema.tables
                            WHERE table_schema = 'public' AND table_name = 'application_description_paragraph'
                            """,
                    Integer.class
            );
            if (count != null && count > 0) {
                return;
            }
            jdbcTemplate.execute("""
                    CREATE TABLE application_description_paragraph (
                        id BIGSERIAL PRIMARY KEY,
                        application_id BIGINT NOT NULL REFERENCES filing_application(id) ON DELETE CASCADE,
                        para_no INTEGER NOT NULL,
                        text TEXT NOT NULL,
                        text_mr TEXT,
                        CONSTRAINT uk_app_desc_para UNIQUE (application_id, para_no)
                    )
                    """);
            log.info("Created application_description_paragraph");
        } catch (Exception ex) {
            log.warn("Could not ensure application_description_paragraph: {}", ex.getMessage());
        }
    }

    private void addColumn(String table, String column, String ddl) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    """
                            SELECT COUNT(*) FROM information_schema.columns
                            WHERE table_schema = 'public' AND table_name = ? AND column_name = ?
                            """,
                    Integer.class,
                    table,
                    column
            );
            if (count == null || count == 0) {
                jdbcTemplate.execute(ddl);
                log.info("Added {}.{}", table, column);
            }
        } catch (Exception ex) {
            log.warn("Could not add {}.{}: {}", table, column, ex.getMessage());
        }
    }
}
