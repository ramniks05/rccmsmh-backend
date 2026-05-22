package com.maharashtra.rccms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Adds advocate profile/registration columns when Hibernate ddl-auto=update did not apply them
 * (common on existing PostgreSQL databases).
 */
@Component
@Order(0)
public class AdvocateRegistrationSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdvocateRegistrationSchemaPatch.class);

    private final JdbcTemplate jdbcTemplate;

    public AdvocateRegistrationSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!tableExists("advocate_registration")) {
            log.info("advocate_registration table not found; skipping schema patch.");
            return;
        }
        addColumn("first_name", "VARCHAR(80)");
        addColumn("middle_name", "VARCHAR(80)");
        addColumn("last_name", "VARCHAR(80)");
        addColumn("bar_enrollment_state", "VARCHAR(80)");
        addColumn("bar_enrollment_year", "INTEGER");
        addColumn("bar_enrollment_number", "VARCHAR(80)");
        addColumn("place_of_practice_state", "VARCHAR(80)");
        addColumn("place_of_practice_district", "VARCHAR(120)");
        addColumn("bar_enrollment_certificate_storage_key", "VARCHAR(512)");
        addColumn("bar_enrollment_certificate_file_name", "VARCHAR(255)");
        addColumn("gender", "VARCHAR(16)");
        addColumn("pin_code", "VARCHAR(6)");
        addColumn("state_id", "BIGINT");
        addColumn("state_name", "VARCHAR(120)");
        addColumn("district_id", "BIGINT");
        addColumn("district_name", "VARCHAR(120)");
        addColumn("subdistrict_id", "BIGINT");
        addColumn("subdistrict_name", "VARCHAR(120)");
        addColumn("village", "VARCHAR(120)");
        addColumn("address_line_1", "VARCHAR(255)");
        addColumn("address_line_2", "VARCHAR(255)");
        addColumn("address_line_3", "VARCHAR(255)");
        patchUpdatedAt();
        patchProfileComplete();
        relaxAddressColumn();
        log.info("Advocate registration schema patch completed.");
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

    private void addColumn(String column, String sqlType) {
        if (columnExists(column)) {
            return;
        }
        try {
            jdbcTemplate.execute(
                    "ALTER TABLE advocate_registration ADD COLUMN " + column + " " + sqlType
            );
            log.info("Added advocate_registration.{}", column);
        } catch (Exception ex) {
            log.warn("Could not add advocate_registration.{}: {}", column, ex.getMessage());
        }
    }

    private boolean columnExists(String column) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM information_schema.columns
                        WHERE table_schema = 'public'
                          AND table_name = 'advocate_registration'
                          AND column_name = ?
                        """,
                Integer.class,
                column
        );
        return count != null && count > 0;
    }

    private void patchUpdatedAt() {
        if (!columnExists("updated_at")) {
            try {
                jdbcTemplate.execute(
                        "ALTER TABLE advocate_registration ADD COLUMN updated_at TIMESTAMP"
                );
                log.info("Added advocate_registration.updated_at");
            } catch (Exception ex) {
                log.warn("Could not add updated_at: {}", ex.getMessage());
                return;
            }
        }
        try {
            jdbcTemplate.execute(
                    """
                            UPDATE advocate_registration
                            SET updated_at = COALESCE(created_at, CURRENT_TIMESTAMP)
                            WHERE updated_at IS NULL
                            """
            );
            jdbcTemplate.execute(
                    "ALTER TABLE advocate_registration ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP"
            );
            jdbcTemplate.execute(
                    "ALTER TABLE advocate_registration ALTER COLUMN updated_at SET NOT NULL"
            );
        } catch (Exception ex) {
            log.warn("Could not finalize updated_at: {}", ex.getMessage());
        }
    }

    private void patchProfileComplete() {
        if (!columnExists("profile_complete")) {
            try {
                jdbcTemplate.execute(
                        "ALTER TABLE advocate_registration ADD COLUMN profile_complete BOOLEAN DEFAULT FALSE"
                );
                log.info("Added advocate_registration.profile_complete");
            } catch (Exception ex) {
                log.warn("Could not add profile_complete: {}", ex.getMessage());
                return;
            }
        }
        try {
            jdbcTemplate.execute(
                    "UPDATE advocate_registration SET profile_complete = FALSE WHERE profile_complete IS NULL"
            );
            jdbcTemplate.execute(
                    "ALTER TABLE advocate_registration ALTER COLUMN profile_complete SET DEFAULT FALSE"
            );
            jdbcTemplate.execute(
                    "ALTER TABLE advocate_registration ALTER COLUMN profile_complete SET NOT NULL"
            );
        } catch (Exception ex) {
            log.warn("Could not finalize profile_complete: {}", ex.getMessage());
        }
    }

    /**
     * Address is filled on profile updation ({@code PUT /api/advocates/me/profile}), not at registration.
     */
    private void relaxAddressColumn() {
        try {
            jdbcTemplate.execute(
                    "ALTER TABLE advocate_registration ALTER COLUMN address TYPE VARCHAR(500)"
            );
        } catch (Exception ex) {
            log.debug("address column type widen skipped: {}", ex.getMessage());
        }
        try {
            jdbcTemplate.execute(
                    "ALTER TABLE advocate_registration ALTER COLUMN address DROP NOT NULL"
            );
            log.info("advocate_registration.address is nullable (set on profile updation).");
        } catch (Exception ex) {
            log.warn("Could not relax address NOT NULL: {}", ex.getMessage());
        }
    }
}
