package com.maharashtra.rccms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Adds {@code boundary_level} to {@code master_office_type} when Hibernate ddl-auto=update
 * did not apply it on an existing PostgreSQL database.
 */
@Component
@Order(0)
public class OfficeTypeSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(OfficeTypeSchemaPatch.class);

    private static final Map<Long, String> DEFAULT_BOUNDARY_BY_ID = Map.of(
            1L, "STATE",
            2L, "DIVISION",
            3L, "DISTRICT",
            4L, "TALUKA"
    );

    private final JdbcTemplate jdbcTemplate;

    public OfficeTypeSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!tableExists("master_office_type")) {
            log.info("master_office_type table not found; skipping office type schema patch.");
            return;
        }
        addBoundaryLevelColumn();
        seedBoundaryLevels();
        enforceNotNull();
        log.info("Office type schema patch completed.");
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
                          AND table_name = 'master_office_type'
                          AND column_name = ?
                        """,
                Integer.class,
                column
        );
        return count != null && count > 0;
    }

    private void addBoundaryLevelColumn() {
        if (columnExists("boundary_level")) {
            return;
        }
        try {
            jdbcTemplate.execute(
                    "ALTER TABLE master_office_type ADD COLUMN boundary_level VARCHAR(32)"
            );
            log.info("Added master_office_type.boundary_level");
        } catch (Exception ex) {
            log.warn("Could not add master_office_type.boundary_level: {}", ex.getMessage());
        }
    }

    private void seedBoundaryLevels() {
        if (!columnExists("boundary_level")) {
            return;
        }
        DEFAULT_BOUNDARY_BY_ID.forEach((id, level) -> {
            try {
                jdbcTemplate.update(
                        """
                                UPDATE master_office_type
                                SET boundary_level = ?
                                WHERE id = ? AND (boundary_level IS NULL OR TRIM(boundary_level) = '')
                                """,
                        level,
                        id
                );
            } catch (Exception ex) {
                log.warn("Could not seed boundary_level for office type id {}: {}", id, ex.getMessage());
            }
        });
        try {
            int updated = jdbcTemplate.update(
                    """
                            UPDATE master_office_type
                            SET boundary_level = 'DISTRICT'
                            WHERE boundary_level IS NULL OR TRIM(boundary_level) = ''
                            """
            );
            if (updated > 0) {
                log.info("Set default boundary_level=DISTRICT for {} office type row(s).", updated);
            }
        } catch (Exception ex) {
            log.warn("Could not backfill default boundary_level: {}", ex.getMessage());
        }
    }

    private void enforceNotNull() {
        if (!columnExists("boundary_level")) {
            return;
        }
        try {
            jdbcTemplate.execute(
                    "ALTER TABLE master_office_type ALTER COLUMN boundary_level SET NOT NULL"
            );
        } catch (Exception ex) {
            log.warn("Could not set master_office_type.boundary_level NOT NULL: {}", ex.getMessage());
        }
    }
}
