package com.maharashtra.rccms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Adds {@code division_code} to {@code master_district} and backfills from legacy {@code division_id}.
 */
@Component
@Order(1)
public class DistrictSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DistrictSchemaPatch.class);

    private final JdbcTemplate jdbcTemplate;

    public DistrictSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!tableExists("master_district")) {
            log.info("master_district table not found; skipping district schema patch.");
            return;
        }
        addColumn("division_code", "VARCHAR(64)");
        backfillDivisionCodeFromLegacyFk();
        log.info("District schema patch completed.");
    }

    private void backfillDivisionCodeFromLegacyFk() {
        if (!columnExists("division_id")) {
            return;
        }
        try {
            int updated = jdbcTemplate.update(
                    """
                            UPDATE master_district d
                            SET division_code = dv.division_code
                            FROM master_division dv
                            WHERE d.division_id = dv.id
                              AND d.division_code IS NULL
                              AND dv.division_code IS NOT NULL
                            """
            );
            if (updated > 0) {
                log.info("Backfilled division_code on {} master_district row(s) from division_id.", updated);
            }
        } catch (Exception ex) {
            log.warn("Could not backfill master_district.division_code: {}", ex.getMessage());
        }
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM information_schema.tables
                        WHERE table_schema = 'public' AND table_name = ?
                        """,
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }

    private boolean columnExists(String column) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM information_schema.columns
                        WHERE table_schema = 'public'
                          AND table_name = 'master_district'
                          AND column_name = ?
                        """,
                Integer.class,
                column
        );
        return count != null && count > 0;
    }

    private void addColumn(String column, String sqlType) {
        if (columnExists(column)) {
            return;
        }
        try {
            jdbcTemplate.execute(
                    "ALTER TABLE master_district ADD COLUMN " + column + " " + sqlType
            );
            log.info("Added master_district.{}", column);
        } catch (Exception ex) {
            log.warn("Could not add master_district.{}: {}", column, ex.getMessage());
        }
    }
}
