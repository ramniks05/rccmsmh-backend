package com.maharashtra.rccms.config;

import com.maharashtra.rccms.service.EmployeeLoginRepairService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * On startup: copy legacy {@code officer_registration.password_hash} into {@code employee},
 * then set default password for any employee still missing one.
 */
@Component
public class OfficerRegistrationPasswordMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(OfficerRegistrationPasswordMigration.class);

    private final EmployeeLoginRepairService employeeLoginRepairService;

    public OfficerRegistrationPasswordMigration(EmployeeLoginRepairService employeeLoginRepairService) {
        this.employeeLoginRepairService = employeeLoginRepairService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            employeeLoginRepairService.repairOfficerLogins(true);
        } catch (Exception ex) {
            log.warn("Officer login repair on startup skipped: {}", ex.getMessage());
        }
    }
}
