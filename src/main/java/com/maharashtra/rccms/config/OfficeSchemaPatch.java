package com.maharashtra.rccms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Adds office contact/address columns to {@code master_office} when Hibernate ddl-auto=update
 * did not apply them on an existing PostgreSQL database.
 */
@Component
@Order(0)
public class OfficeSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(OfficeSchemaPatch.class);

    private final JdbcTemplate jdbcTemplate;

    public OfficeSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!tableExists("master_office")) {
            log.info("master_office table not found; skipping office schema patch.");
            return;
        }
        addColumn("office_address", "VARCHAR(512)");
        addColumn("office_address_local", "VARCHAR(512)");
        addColumn("email", "VARCHAR(190)");
        addColumn("office_contact_no", "VARCHAR(32)");
        addColumn("state_id", "BIGINT");
        addColumn("division_id", "BIGINT");
        addColumn("district_id", "BIGINT");
        addColumn("taluka_id", "BIGINT");
        addColumn("state_lgd_code", "VARCHAR(64)");
        addColumn("district_lgd_code", "VARCHAR(64)");
        addColumn("taluka_lgd_code", "VARCHAR(64)");
        log.info("Office schema patch completed.");
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
                          AND table_name = 'master_office'
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
                    "ALTER TABLE master_office ADD COLUMN " + column + " " + sqlType
            );
            log.info("Added master_office.{}", column);
        } catch (Exception ex) {
            log.warn("Could not add master_office.{}: {}", column, ex.getMessage());
        }
    }
}
