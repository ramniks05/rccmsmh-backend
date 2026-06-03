package com.maharashtra.rccms.service;

import com.maharashtra.rccms.model.Employee;
import com.maharashtra.rccms.repository.EmployeeRepository;
import com.maharashtra.rccms.util.EmployeeLoginSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeLoginRepairService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeLoginRepairService.class);

    private final JdbcTemplate jdbcTemplate;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeLoginRepairService(
            JdbcTemplate jdbcTemplate,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Map<String, Object> repairOfficerLogins(boolean resetMissingToDefault) {
        int migratedFromLegacy = migrateFromOfficerRegistration();
        int defaulted = 0;
        List<Map<String, Object>> repaired = new ArrayList<>();

        if (resetMissingToDefault) {
            for (Employee employee : employeeRepository.findAll()) {
                if (EmployeeLoginSupport.hasText(employee.getPasswordHash())) {
                    continue;
                }
                employee.setPasswordHash(passwordEncoder.encode(EmployeeLoginSupport.DEFAULT_OFFICER_PASSWORD));
                employeeRepository.save(employee);
                defaulted++;
                repaired.add(repairEntry(employee, "default_password_set"));
            }
        }

        int stillMissing = (int) employeeRepository.findAll().stream()
                .filter(employee -> !EmployeeLoginSupport.hasText(employee.getPasswordHash()))
                .count();

        if (migratedFromLegacy > 0) {
            log.info("Officer login repair: migrated {} password(s) from officer_registration.", migratedFromLegacy);
        }
        if (defaulted > 0) {
            log.info("Officer login repair: set default password for {} employee(s).", defaulted);
        }
        if (stillMissing > 0) {
            log.warn("Officer login repair: {} employee(s) still have no password_hash.", stillMissing);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("migratedFromLegacyTable", migratedFromLegacy);
        result.put("defaultPasswordAppliedCount", defaulted);
        result.put("stillMissingPasswordCount", stillMissing);
        result.put("defaultPassword", EmployeeLoginSupport.DEFAULT_OFFICER_PASSWORD);
        result.put("repairedEmployees", repaired);
        result.put("message", stillMissing == 0
                ? "All employees have login passwords on the employee table."
                : "Some employees still have no password. Call this endpoint with resetMissingToDefault=true or use POST /{id}/sync-officer-login.");
        return result;
    }

    private int migrateFromOfficerRegistration() {
        if (!officerRegistrationTableExists()) {
            return 0;
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT email, password_hash FROM officer_registration WHERE password_hash IS NOT NULL"
        );
        int migrated = 0;
        for (Map<String, Object> row : rows) {
            String loginId = stringValue(row.get("email"));
            String passwordHash = stringValue(row.get("password_hash"));
            if (!EmployeeLoginSupport.hasText(loginId) || !EmployeeLoginSupport.hasText(passwordHash)) {
                continue;
            }

            Optional<Employee> employee = EmployeeLoginSupport.findByLoginId(employeeRepository, loginId);
            if (employee.isEmpty() || EmployeeLoginSupport.hasText(employee.get().getPasswordHash())) {
                continue;
            }

            Employee target = employee.get();
            target.setPasswordHash(passwordHash);
            employeeRepository.save(target);
            migrated++;
        }
        return migrated;
    }

    @Transactional
    public Map<String, Object> resetEmployeeLoginPassword(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee id"));
        String officerLoginId = EmployeeLoginSupport.buildLoginId(employee.getEmail(), employee.getEmployeeCode());
        employee.setPasswordHash(passwordEncoder.encode(EmployeeLoginSupport.DEFAULT_OFFICER_PASSWORD));
        employeeRepository.save(employee);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("employeeId", employee.getId());
        result.put("userId", officerLoginId);
        result.put("defaultPassword", EmployeeLoginSupport.DEFAULT_OFFICER_PASSWORD);
        result.put("message", "Employee login password reset. Use userId and defaultPassword to sign in.");
        return result;
    }

    private static Map<String, Object> repairEntry(Employee employee, String action) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("employeeId", employee.getId());
        entry.put("userId", EmployeeLoginSupport.buildLoginId(employee.getEmail(), employee.getEmployeeCode()));
        entry.put("action", action);
        return entry;
    }

    private boolean officerRegistrationTableExists() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'officer_registration'
                """,
                Integer.class
        );
        return count != null && count > 0;
    }

    private static String stringValue(Object value) {
        return value == null ? null : value.toString();
    }
}
