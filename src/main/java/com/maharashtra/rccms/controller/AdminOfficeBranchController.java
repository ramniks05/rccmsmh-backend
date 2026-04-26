package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.OfficeBranchResponse;
import com.maharashtra.rccms.model.master.Office;
import com.maharashtra.rccms.model.master.OfficeBranch;
import com.maharashtra.rccms.repository.OfficeBranchRepository;
import com.maharashtra.rccms.repository.OfficeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@SuppressWarnings("null")
public class AdminOfficeBranchController {

    private final OfficeRepository officeRepository;
    private final OfficeBranchRepository officeBranchRepository;

    public AdminOfficeBranchController(OfficeRepository officeRepository, OfficeBranchRepository officeBranchRepository) {
        this.officeRepository = officeRepository;
        this.officeBranchRepository = officeBranchRepository;
    }

    /**
     * Read-only API for branch dropdown filtering by office.
     */
    @GetMapping("/offices/{officeId}/branches")
    public ResponseEntity<?> listBranchesByOffice(@PathVariable("officeId") Long officeId) {
        Office office = officeRepository.findById(officeId).orElse(null);
        if (office == null) return ResponseEntity.badRequest().body(Map.of("error", "Invalid officeId"));

        List<OfficeBranchResponse> items = officeBranchRepository.findByOfficeIdOrderByNameAsc(officeId).stream()
                .map(AdminOfficeBranchController::toResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    /**
     * Read-only API to resolve a single branch by id.
     */
    @GetMapping("/branches/{branchId}")
    public ResponseEntity<?> getBranch(@PathVariable("branchId") Long branchId) {
        OfficeBranch branch = officeBranchRepository.findById(branchId).orElse(null);
        if (branch == null) return ResponseEntity.badRequest().body(Map.of("error", "Invalid branchId"));
        return ResponseEntity.ok(toResponse(branch));
    }

    private static OfficeBranchResponse toResponse(OfficeBranch branch) {
        Office office = branch.getOffice();
        Long officeId = office == null ? null : office.getId();
        return new OfficeBranchResponse(
                branch.getId(),
                officeId,
                branch.getName(),
                branch.getLocalName(),
                branch.getShortName(),
                branch.getShortNameLocal()
        );
    }
}

