package com.maharashtra.rccms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class PartyInPersonRegistrationSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PartyInPersonRegistrationSchemaPatch.class);

    private final JdbcTemplate jdbcTemplate;

    public PartyInPersonRegistrationSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!tableExists("party_in_person_registration")) {
            log.info("party_in_person_registration table not found; skipping schema patch.");
            return;
        }
        addColumn("pin_code", "VARCHAR(6)");
        addColumn("state_name", "VARCHAR(120)");
        addColumn("district_name", "VARCHAR(120)");
        addColumn("subdistrict_name", "VARCHAR(120)");
        addColumn("village", "VARCHAR(120)");
        addColumn("address_line_1", "VARCHAR(255)");
        addColumn("address_line_2", "VARCHAR(255)");
        addColumn("address_line_3", "VARCHAR(255)");
        widenAddressColumn();
        log.info("Party in person registration schema patch completed.");
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
                    "ALTER TABLE party_in_person_registration ADD COLUMN " + column + " " + sqlType
            );
            log.info("Added party_in_person_registration.{}", column);
        } catch (Exception ex) {
            log.warn("Could not add party_in_person_registration.{}: {}", column, ex.getMessage());
        }
    }

    private boolean columnExists(String column) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM information_schema.columns
                        WHERE table_schema = 'public'
                          AND table_name = 'party_in_person_registration'
                          AND column_name = ?
                        """,
                Integer.class,
                column
        );
        return count != null && count > 0;
    }

    private void widenAddressColumn() {
        try {
            jdbcTemplate.execute(
                    "ALTER TABLE party_in_person_registration ALTER COLUMN address TYPE VARCHAR(500)"
            );
        } catch (Exception ex) {
            log.debug("party address column widen skipped: {}", ex.getMessage());
        }
    }
}
