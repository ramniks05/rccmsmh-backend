package com.maharashtra.rccms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ensures the Maharashtra covered state row has LGD code {@code 27} for lookups and admin filters.
 */
@Component
@Order(0)
public class StateSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StateSchemaPatch.class);

    private final JdbcTemplate jdbcTemplate;
    private final String coveredStateLgdCode;

    public StateSchemaPatch(
            JdbcTemplate jdbcTemplate,
            @Value("${rccms.boundary.covered-state-lgd-code:27}") String coveredStateLgdCode
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.coveredStateLgdCode = coveredStateLgdCode.trim();
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!tableExists("master_state")) {
            log.info("master_state table not found; skipping state schema patch.");
            return;
        }
        backfillMaharashtraLgdCode();
        log.info("State schema patch completed.");
    }

    private void backfillMaharashtraLgdCode() {
        try {
            int updated = jdbcTemplate.update(
                    """
                            UPDATE master_state
                            SET lgd_code = ?
                            WHERE (lgd_code IS NULL OR trim(lgd_code) = '')
                              AND lower(trim(name)) = 'maharashtra'
                            """,
                    coveredStateLgdCode
            );
            if (updated > 0) {
                log.info("Set lgd_code={} on {} Maharashtra master_state row(s).", coveredStateLgdCode, updated);
            }
        } catch (Exception ex) {
            log.warn("Could not backfill master_state.lgd_code for Maharashtra: {}", ex.getMessage());
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
}
