package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.ActResponse;
import com.maharashtra.rccms.dto.SectionResponse;
import com.maharashtra.rccms.dto.OfficeResponse;
import com.maharashtra.rccms.model.master.Act;
import com.maharashtra.rccms.model.master.Section;
import com.maharashtra.rccms.model.master.Office;
import com.maharashtra.rccms.repository.ActRepository;
import com.maharashtra.rccms.repository.SectionRepository;
import com.maharashtra.rccms.repository.OfficeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/lookups")
public class MastersLookupController {

    private final ActRepository actRepository;
    private final SectionRepository sectionRepository;
    private final OfficeRepository officeRepository;

    public MastersLookupController(ActRepository actRepository,
                                   SectionRepository sectionRepository,
                                   OfficeRepository officeRepository) {
        this.actRepository = actRepository;
        this.sectionRepository = sectionRepository;
        this.officeRepository = officeRepository;
    }

    @GetMapping("/acts")
    public ResponseEntity<?> listActs() {
        List<ActResponse> items = actRepository.findAll().stream()
                .sorted(Comparator
                        .comparing((Act a) -> safe(a.getActCode()), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(a -> safe(a.getActName()), String.CASE_INSENSITIVE_ORDER))
                .map(a -> new ActResponse(a.getId(), a.getActCode(), a.getActName(), a.getActNameLocal()))
                .toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/sections")
    public ResponseEntity<?> listSections(@RequestParam(name = "actId", required = false) Long actId) {
        List<Section> source = actId == null ? sectionRepository.findAll() : sectionRepository.findByActId(actId);
        List<SectionResponse> items = source.stream()
                .sorted(Comparator
                        .comparing((Section s) -> safe(s.getSectionCode()), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(s -> safe(s.getSectionName()), String.CASE_INSENSITIVE_ORDER))
                .map(this::toSectionResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    /**
     * Offices for a given tehsil (taluka) for dropdowns.
     * Uses master Office rows where level = "TALUKA" and locationId = talukaId.
     */
    @GetMapping("/offices/by-taluka")
    public ResponseEntity<?> listOfficesByTaluka(
            @RequestParam("talukaId") Long talukaId,
            @RequestParam(name = "departmentId", required = false) Long departmentId
    ) {
        String level = "TALUKA";
        List<Office> offices = (departmentId == null)
                ? officeRepository.findByLevelAndLocationIdOrderByNameAsc(level, talukaId)
                : officeRepository.findByDepartmentIdAndLevelAndLocationIdOrderByNameAsc(departmentId, level, talukaId);

        List<OfficeResponse> items = offices.stream()
                .map(this::toOfficeResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    private SectionResponse toSectionResponse(Section section) {
        Act act = section.getAct();
        return new SectionResponse(
                section.getId(),
                act == null ? null : act.getId(),
                act == null ? null : act.getActCode(),
                act == null ? null : act.getActName(),
                act == null ? null : act.getActNameLocal(),
                section.getSectionCode(),
                section.getSectionName(),
                section.getSectionNameLocal()
        );
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private OfficeResponse toOfficeResponse(Office office) {
        return new OfficeResponse(
                office.getId(),
                office.getDepartment() == null ? null : office.getDepartment().getId(),
                office.getDepartment() == null ? null : office.getDepartment().getName(),
                office.getDepartment() == null ? null : office.getDepartment().getLocalName(),
                office.getOfficeType() == null ? null : office.getOfficeType().getId(),
                office.getOfficeType() == null ? null : office.getOfficeType().getName(),
                office.getOfficeType() == null ? null : office.getOfficeType().getLocalName(),
                office.getLevel(),
                office.getLocationId(),
                office.getName(),
                office.getLocalName(),
                office.getShortName(),
                office.getShortNameLocal()
        );
    }

}
