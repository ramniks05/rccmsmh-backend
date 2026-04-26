package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.SubjectResponse;
import com.maharashtra.rccms.model.master.Department;
import com.maharashtra.rccms.model.master.Subject;
import com.maharashtra.rccms.repository.SubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subjects")
@SuppressWarnings("null")
public class SubjectController {

    private final SubjectRepository subjectRepository;

    public SubjectController(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    /**
     * Dropdown API for any logged-in user (advocate / party-in-person / etc.).
     */
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(name = "departmentId", required = false) Long departmentId) {
        List<SubjectResponse> items = subjectRepository.findAll().stream()
                .filter(s -> departmentId == null
                        || (s.getDepartment() != null && departmentId.equals(s.getDepartment().getId())))
                .map(SubjectController::toResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        Subject subject = subjectRepository.findById(id).orElse(null);
        if (subject == null) return ResponseEntity.badRequest().body(Map.of("error", "Invalid subjectId"));
        return ResponseEntity.ok(toResponse(subject));
    }

    private static SubjectResponse toResponse(Subject s) {
        Department d = s.getDepartment();
        Long departmentId = d == null ? null : d.getId();
        String departmentName = d == null ? null : d.getName();
        String departmentLocalName = d == null ? null : d.getLocalName();

        return new SubjectResponse(
                s.getId(),
                departmentId,
                departmentName,
                departmentLocalName,
                s.getSubjectCode(),
                s.getSubjectName(),
                s.getSubjectNameLocal()
        );
    }
}

