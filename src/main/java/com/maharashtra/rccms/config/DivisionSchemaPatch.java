package com.maharashtra.rccms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Adds {@code division_code} to {@code master_division} when Hibernate ddl-auto=update
 * did not apply it on an existing PostgreSQL database.
 */
@Component
@Order(0)
public class DivisionSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DivisionSchemaPatch.class);

    private final JdbcTemplate jdbcTemplate;

    public DivisionSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!tableExists("master_division")) {
            log.info("master_division table not found; skipping division schema patch.");
            return;
        }
        addColumn("division_code", "VARCHAR(64)");
        log.info("Division schema patch completed.");
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
                          AND table_name = 'master_division'
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
                    "ALTER TABLE master_division ADD COLUMN " + column + " " + sqlType
            );
            log.info("Added master_division.{}", column);
        } catch (Exception ex) {
            log.warn("Could not add master_division.{}: {}", column, ex.getMessage());
        }
    }
}
