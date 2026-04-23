package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.EmployeeCreateRequest;
import com.maharashtra.rccms.dto.EmployeePostingCloseRequest;
import com.maharashtra.rccms.dto.EmployeePostingCreateRequest;
import com.maharashtra.rccms.dto.EmployeePostingResponse;
import com.maharashtra.rccms.dto.EmployeeResponse;
import com.maharashtra.rccms.dto.EmployeeUpdateRequest;
import com.maharashtra.rccms.model.Employee;
import com.maharashtra.rccms.model.EmployeePosting;
import com.maharashtra.rccms.model.master.Designation;
import com.maharashtra.rccms.model.master.Office;
import com.maharashtra.rccms.repository.DesignationRepository;
import com.maharashtra.rccms.repository.EmployeePostingRepository;
import com.maharashtra.rccms.repository.EmployeeRepository;
import com.maharashtra.rccms.repository.OfficeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/employees")
public class AdminEmployeeController {

    private final EmployeeRepository employeeRepository;
    private final EmployeePostingRepository employeePostingRepository;
    private final OfficeRepository officeRepository;
    private final DesignationRepository designationRepository;

    public AdminEmployeeController(
            EmployeeRepository employeeRepository,
            EmployeePostingRepository employeePostingRepository,
            OfficeRepository officeRepository,
            DesignationRepository designationRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.employeePostingRepository = employeePostingRepository;
        this.officeRepository = officeRepository;
        this.designationRepository = designationRepository;
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeCreateRequest request) {
        try {
            Employee employee = new Employee();
            applyEmployeeFields(employee, request.getEmployeeCode(), request.getFullName(), request.getFullNameLocal(),
                    request.getMobile(), request.getEmail(), true);
            employee = employeeRepository.save(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(toEmployeeResponse(employee));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listEmployees(@RequestParam(name = "active", required = false) Boolean active) {
        List<EmployeeResponse> items = employeeRepository.findAll().stream()
                .filter(e -> active == null || (e.getIsActive() != null && active.equals(e.getIsActive())))
                .map(AdminEmployeeController::toEmployeeResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable("id") Long id, @RequestBody EmployeeUpdateRequest request) {
        try {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid employee id"));
            Boolean isActive = request.getIsActive() == null ? employee.getIsActive() : request.getIsActive();
            applyEmployeeFields(employee, request.getEmployeeCode(), request.getFullName(), request.getFullNameLocal(),
                    request.getMobile(), request.getEmail(), isActive);
            employee = employeeRepository.save(employee);
            return ResponseEntity.ok(toEmployeeResponse(employee));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{id}/postings")
    public ResponseEntity<?> addPosting(@PathVariable("id") Long employeeId, @RequestBody EmployeePostingCreateRequest request) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid employee id"));

            Long officeId = request.getOfficeId();
            if (officeId == null) throw new IllegalArgumentException("officeId is required");
            Office office = officeRepository.findById(officeId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid officeId"));

            Long designationId = request.getDesignationId();
            if (designationId == null) throw new IllegalArgumentException("designationId is required");
            Designation designation = designationRepository.findById(designationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid designationId"));

            LocalDate fromDate = request.getFromDate();
            if (fromDate == null) throw new IllegalArgumentException("fromDate is required");

            EmployeePosting current = employeePostingRepository.findFirstByEmployeeIdAndToDateIsNull(employeeId).orElse(null);
            if (current != null) {
                if (current.getFromDate() != null && !fromDate.isAfter(current.getFromDate())) {
                    throw new IllegalArgumentException("fromDate must be after current posting fromDate");
                }
                current.setToDate(fromDate.minusDays(1));
                employeePostingRepository.save(current);
            }

            EmployeePosting posting = new EmployeePosting();
            posting.setEmployee(employee);
            posting.setOffice(office);
            posting.setDesignation(designation);
            posting.setFromDate(fromDate);
            posting.setToDate(null);

            posting = employeePostingRepository.save(posting);
            return ResponseEntity.status(HttpStatus.CREATED).body(toPostingResponse(posting));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{id}/postings")
    public ResponseEntity<?> listPostings(@PathVariable("id") Long employeeId) {
        List<EmployeePostingResponse> items = employeePostingRepository.findByEmployeeIdOrderByFromDateDesc(employeeId).stream()
                .map(AdminEmployeeController::toPostingResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/postings/{postingId}/close")
    public ResponseEntity<?> closePosting(@PathVariable("postingId") Long postingId, @RequestBody EmployeePostingCloseRequest request) {
        try {
            EmployeePosting posting = employeePostingRepository.findById(postingId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid posting id"));
            if (posting.getToDate() != null) throw new IllegalArgumentException("Posting already closed");
            if (request.getToDate() == null) throw new IllegalArgumentException("toDate is required");
            if (posting.getFromDate() != null && request.getToDate().isBefore(posting.getFromDate())) {
                throw new IllegalArgumentException("toDate must be >= fromDate");
            }
            posting.setToDate(request.getToDate());
            posting = employeePostingRepository.save(posting);
            return ResponseEntity.ok(toPostingResponse(posting));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    private static void applyEmployeeFields(Employee employee,
                                            String employeeCode,
                                            String fullName,
                                            String fullNameLocal,
                                            String mobile,
                                            String email,
                                            Boolean isActive) {
        if (employeeCode == null || employeeCode.trim().isEmpty()) throw new IllegalArgumentException("employeeCode is required");
        if (fullName == null || fullName.trim().isEmpty()) throw new IllegalArgumentException("fullName is required");
        employee.setEmployeeCode(employeeCode.trim());
        employee.setFullName(fullName.trim());
        employee.setFullNameLocal(fullNameLocal);
        employee.setMobile(mobile);
        employee.setEmail(email);
        employee.setIsActive(isActive == null ? true : isActive);
    }

    private static EmployeeResponse toEmployeeResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getFullNameLocal(),
                employee.getMobile(),
                employee.getEmail(),
                employee.getIsActive()
        );
    }

    private static EmployeePostingResponse toPostingResponse(EmployeePosting posting) {
        Employee employee = posting.getEmployee();
        Office office = posting.getOffice();
        Designation designation = posting.getDesignation();

        Long employeeId = employee == null ? null : employee.getId();
        Long officeId = office == null ? null : office.getId();
        String officeName = office == null ? null : office.getName();
        Long designationId = designation == null ? null : designation.getId();
        String designationName = designation == null ? null : designation.getName();

        return new EmployeePostingResponse(
                posting.getId(),
                employeeId,
                officeId,
                officeName,
                designationId,
                designationName,
                posting.getFromDate(),
                posting.getToDate()
        );
    }
}

