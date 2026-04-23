package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.ActCreateRequest;
import com.maharashtra.rccms.dto.ActResponse;
import com.maharashtra.rccms.dto.ActUpdateRequest;
import com.maharashtra.rccms.dto.BoundaryMasterCreateRequest;
import com.maharashtra.rccms.dto.BoundaryMasterResponse;
import com.maharashtra.rccms.dto.DepartmentCreateRequest;
import com.maharashtra.rccms.dto.DepartmentResponse;
import com.maharashtra.rccms.dto.DepartmentUpdateRequest;
import com.maharashtra.rccms.dto.DistrictCreateRequest;
import com.maharashtra.rccms.dto.DivisionCreateRequest;
import com.maharashtra.rccms.dto.SubdistrictCreateRequest;
import com.maharashtra.rccms.dto.TalukaCreateRequest;
import com.maharashtra.rccms.dto.SectionCreateRequest;
import com.maharashtra.rccms.dto.SectionResponse;
import com.maharashtra.rccms.dto.SectionUpdateRequest;
import com.maharashtra.rccms.dto.SubjectCreateRequest;
import com.maharashtra.rccms.dto.SubjectResponse;
import com.maharashtra.rccms.dto.SubjectUpdateRequest;
import com.maharashtra.rccms.dto.OfficeCreateRequest;
import com.maharashtra.rccms.dto.OfficeResponse;
import com.maharashtra.rccms.dto.OfficeUpdateRequest;
import com.maharashtra.rccms.dto.OfficeTypeCreateRequest;
import com.maharashtra.rccms.dto.OfficeTypeResponse;
import com.maharashtra.rccms.dto.OfficeTypeUpdateRequest;
import com.maharashtra.rccms.dto.VillageCreateRequest;
import com.maharashtra.rccms.model.master.Act;
import com.maharashtra.rccms.model.master.Department;
import com.maharashtra.rccms.model.master.Section;
import com.maharashtra.rccms.model.master.Subject;
import com.maharashtra.rccms.model.master.Office;
import com.maharashtra.rccms.model.master.OfficeType;
import com.maharashtra.rccms.model.master.District;
import com.maharashtra.rccms.model.master.Division;
import com.maharashtra.rccms.model.master.State;
import com.maharashtra.rccms.model.master.Subdistrict;
import com.maharashtra.rccms.model.master.Taluka;
import com.maharashtra.rccms.model.master.Village;
import com.maharashtra.rccms.repository.ActRepository;
import com.maharashtra.rccms.repository.DepartmentRepository;
import com.maharashtra.rccms.repository.DistrictRepository;
import com.maharashtra.rccms.repository.DivisionRepository;
import com.maharashtra.rccms.repository.OfficeRepository;
import com.maharashtra.rccms.repository.OfficeTypeRepository;
import com.maharashtra.rccms.repository.SectionRepository;
import com.maharashtra.rccms.repository.StateRepository;
import com.maharashtra.rccms.repository.SubdistrictRepository;
import com.maharashtra.rccms.repository.SubjectRepository;
import com.maharashtra.rccms.repository.TalukaRepository;
import com.maharashtra.rccms.repository.VillageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/masters")
public class AdminMastersController {

    private final ActRepository actRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final OfficeRepository officeRepository;
    private final OfficeTypeRepository officeTypeRepository;
    private final StateRepository stateRepository;
    private final DivisionRepository divisionRepository;
    private final DistrictRepository districtRepository;
    private final SubdistrictRepository subdistrictRepository;
    private final TalukaRepository talukaRepository;
    private final VillageRepository villageRepository;

    public AdminMastersController(
            ActRepository actRepository,
            SectionRepository sectionRepository,
            SubjectRepository subjectRepository,
            DepartmentRepository departmentRepository,
            OfficeRepository officeRepository,
            OfficeTypeRepository officeTypeRepository,
            StateRepository stateRepository,
            DivisionRepository divisionRepository,
            DistrictRepository districtRepository,
            SubdistrictRepository subdistrictRepository,
            TalukaRepository talukaRepository,
            VillageRepository villageRepository
    ) {
        this.actRepository = actRepository;
        this.sectionRepository = sectionRepository;
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.officeRepository = officeRepository;
        this.officeTypeRepository = officeTypeRepository;
        this.stateRepository = stateRepository;
        this.divisionRepository = divisionRepository;
        this.districtRepository = districtRepository;
        this.subdistrictRepository = subdistrictRepository;
        this.talukaRepository = talukaRepository;
        this.villageRepository = villageRepository;
    }

    @PostMapping("/office-types")
    public ResponseEntity<?> createOfficeType(@RequestBody OfficeTypeCreateRequest request) {
        try {
            Long departmentId = request.getDepartmentId();
            if (departmentId == null) throw new IllegalArgumentException("departmentId is required");
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid departmentId"));

            OfficeType officeType = new OfficeType();
            applyOfficeTypeFields(officeType, request.getName(), request.getLocalName(), request.getShortName(), request.getShortNameLocal());
            officeType.setDepartment(department);
            officeType = officeTypeRepository.save(officeType);
            return ResponseEntity.status(HttpStatus.CREATED).body(toOfficeTypeResponse(officeType));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/offices")
    public ResponseEntity<?> createOffice(@RequestBody OfficeCreateRequest request) {
        try {
            Department department = requireDepartment(request.getDepartmentId());
            OfficeType officeType = requireOfficeType(request.getOfficeTypeId());
            validateLocation(request.getLevel(), request.getLocationId());

            Office office = new Office();
            office.setDepartment(department);
            office.setOfficeType(officeType);
            applyOfficeFields(office, request.getLevel(), request.getLocationId(), request.getName(), request.getLocalName(),
                    request.getShortName(), request.getShortNameLocal());
            office = officeRepository.save(office);
            return ResponseEntity.status(HttpStatus.CREATED).body(toOfficeResponse(office));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/offices")
    public ResponseEntity<?> listOffices(
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "officeTypeId", required = false) Long officeTypeId,
            @RequestParam(name = "level", required = false) String level,
            @RequestParam(name = "locationId", required = false) Long locationId
    ) {
        List<OfficeResponse> items = officeRepository.findAll().stream()
                .filter(o -> departmentId == null || (o.getDepartment() != null && departmentId.equals(o.getDepartment().getId())))
                .filter(o -> officeTypeId == null || (o.getOfficeType() != null && officeTypeId.equals(o.getOfficeType().getId())))
                .filter(o -> level == null || (o.getLevel() != null && level.equalsIgnoreCase(o.getLevel())))
                .filter(o -> locationId == null || (o.getLocationId() != null && locationId.equals(o.getLocationId())))
                .map(this::toOfficeResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/offices/{id}")
    public ResponseEntity<?> updateOffice(@PathVariable("id") Long id, @RequestBody OfficeUpdateRequest request) {
        try {
            Long officeId = id;
            Office office = officeRepository.findById(officeId).orElseThrow(() -> new IllegalArgumentException("Invalid office id"));

            Department department = requireDepartment(request.getDepartmentId());
            OfficeType officeType = requireOfficeType(request.getOfficeTypeId());
            validateLocation(request.getLevel(), request.getLocationId());

            office.setDepartment(department);
            office.setOfficeType(officeType);
            applyOfficeFields(office, request.getLevel(), request.getLocationId(), request.getName(), request.getLocalName(),
                    request.getShortName(), request.getShortNameLocal());
            office = officeRepository.save(office);
            return ResponseEntity.ok(toOfficeResponse(office));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/offices/{id}")
    public ResponseEntity<?> deleteOffice(@PathVariable("id") Long id) {
        Long officeId = id;
        if (!officeRepository.existsById(officeId)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Invalid office id");
            return ResponseEntity.badRequest().body(body);
        }
        officeRepository.deleteById(officeId);
        Map<String, Object> body = new HashMap<>();
        body.put("deleted", true);
        body.put("id", officeId);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/office-types")
    public ResponseEntity<?> listOfficeTypes(@RequestParam(name = "departmentId", required = false) Long departmentId) {
        List<OfficeTypeResponse> items = officeTypeRepository.findAll().stream()
                .filter(o -> departmentId == null || (o.getDepartment() != null && departmentId.equals(o.getDepartment().getId())))
                .map(AdminMastersController::toOfficeTypeResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/office-types/{id}")
    public ResponseEntity<?> updateOfficeType(@PathVariable("id") Long id, @RequestBody OfficeTypeUpdateRequest request) {
        try {
            Long officeTypeId = id;
            OfficeType officeType = officeTypeRepository.findById(officeTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid office type id"));

            Long departmentId = request.getDepartmentId();
            if (departmentId == null) throw new IllegalArgumentException("departmentId is required");
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid departmentId"));

            applyOfficeTypeFields(officeType, request.getName(), request.getLocalName(), request.getShortName(), request.getShortNameLocal());
            officeType.setDepartment(department);
            officeType = officeTypeRepository.save(officeType);
            return ResponseEntity.ok(toOfficeTypeResponse(officeType));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/office-types/{id}")
    public ResponseEntity<?> deleteOfficeType(@PathVariable("id") Long id) {
        Long officeTypeId = id;
        if (!officeTypeRepository.existsById(officeTypeId)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Invalid office type id");
            return ResponseEntity.badRequest().body(body);
        }
        officeTypeRepository.deleteById(officeTypeId);
        Map<String, Object> body = new HashMap<>();
        body.put("deleted", true);
        body.put("id", officeTypeId);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/subjects")
    public ResponseEntity<?> createSubject(@RequestBody SubjectCreateRequest request) {
        try {
            Long departmentId = request.getDepartmentId();
            if (departmentId == null) throw new IllegalArgumentException("departmentId is required");
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid departmentId"));

            Subject subject = new Subject();
            applySubjectFields(subject, department, request.getSubjectCode(), request.getSubjectName(), request.getSubjectNameLocal());
            subject = subjectRepository.save(subject);
            return ResponseEntity.status(HttpStatus.CREATED).body(toSubjectResponse(subject));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/subjects")
    public ResponseEntity<?> listSubjects(@RequestParam(name = "departmentId", required = false) Long departmentId) {
        List<SubjectResponse> items = subjectRepository.findAll().stream()
                .filter(s -> departmentId == null || (s.getDepartment() != null && departmentId.equals(s.getDepartment().getId())))
                .map(AdminMastersController::toSubjectResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/subjects/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable("id") Long id, @RequestBody SubjectUpdateRequest request) {
        try {
            Long subjectId = id;
            Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new IllegalArgumentException("Invalid subject id"));

            Long departmentId = request.getDepartmentId();
            if (departmentId == null) throw new IllegalArgumentException("departmentId is required");
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid departmentId"));

            applySubjectFields(subject, department, request.getSubjectCode(), request.getSubjectName(), request.getSubjectNameLocal());
            subject = subjectRepository.save(subject);
            return ResponseEntity.ok(toSubjectResponse(subject));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable("id") Long id) {
        Long subjectId = id;
        if (!subjectRepository.existsById(subjectId)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Invalid subject id");
            return ResponseEntity.badRequest().body(body);
        }
        subjectRepository.deleteById(subjectId);
        Map<String, Object> body = new HashMap<>();
        body.put("deleted", true);
        body.put("id", subjectId);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/acts")
    public ResponseEntity<?> createAct(@RequestBody ActCreateRequest request) {
        try {
            Act act = new Act();
            applyActFields(act, request.getActCode(), request.getActName(), request.getActNameLocal());
            act = actRepository.save(act);
            return ResponseEntity.status(HttpStatus.CREATED).body(toActResponse(act));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/acts")
    public ResponseEntity<?> listActs() {
        List<ActResponse> items = actRepository.findAll().stream()
                .map(AdminMastersController::toActResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/acts/{id}")
    public ResponseEntity<?> updateAct(@PathVariable("id") Long id, @RequestBody ActUpdateRequest request) {
        try {
            Long actId = id;
            Act act = actRepository.findById(actId).orElseThrow(() -> new IllegalArgumentException("Invalid act id"));
            applyActFields(act, request.getActCode(), request.getActName(), request.getActNameLocal());
            act = actRepository.save(act);
            return ResponseEntity.ok(toActResponse(act));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/acts/{id}")
    public ResponseEntity<?> deleteAct(@PathVariable("id") Long id) {
        Long actId = id;
        if (!actRepository.existsById(actId)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Invalid act id");
            return ResponseEntity.badRequest().body(body);
        }
        sectionRepository.deleteByActId(actId);
        actRepository.deleteById(actId);
        Map<String, Object> body = new HashMap<>();
        body.put("deleted", true);
        body.put("id", actId);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/sections")
    public ResponseEntity<?> createSection(@RequestBody SectionCreateRequest request) {
        try {
            Long actId = request.getActId();
            if (actId == null) throw new IllegalArgumentException("actId is required");
            Act act = actRepository.findById(actId).orElseThrow(() -> new IllegalArgumentException("Invalid actId"));

            Section section = new Section();
            applySectionFields(section, request.getSectionCode(), request.getSectionName(), request.getSectionNameLocal());
            section.setAct(act);
            section = sectionRepository.save(section);
            return ResponseEntity.status(HttpStatus.CREATED).body(toSectionResponse(section));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/sections")
    public ResponseEntity<?> listSections(@RequestParam(name = "actId", required = false) Long actId) {
        List<SectionResponse> items = sectionRepository.findAll().stream()
                .filter(s -> actId == null || (s.getAct() != null && actId.equals(s.getAct().getId())))
                .map(AdminMastersController::toSectionResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<?> updateSection(@PathVariable("id") Long id, @RequestBody SectionUpdateRequest request) {
        try {
            Long sectionId = id;
            Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new IllegalArgumentException("Invalid section id"));

            Long actId = request.getActId();
            if (actId == null) throw new IllegalArgumentException("actId is required");
            Act act = actRepository.findById(actId).orElseThrow(() -> new IllegalArgumentException("Invalid actId"));

            applySectionFields(section, request.getSectionCode(), request.getSectionName(), request.getSectionNameLocal());
            section.setAct(act);
            section = sectionRepository.save(section);
            return ResponseEntity.ok(toSectionResponse(section));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/sections/{id}")
    public ResponseEntity<?> deleteSection(@PathVariable("id") Long id) {
        Long sectionId = id;
        if (!sectionRepository.existsById(sectionId)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Invalid section id");
            return ResponseEntity.badRequest().body(body);
        }
        sectionRepository.deleteById(sectionId);
        Map<String, Object> body = new HashMap<>();
        body.put("deleted", true);
        body.put("id", sectionId);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/departments")
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentCreateRequest request) {
        try {
            Long stateId = request.getStateId();
            if (stateId == null) throw new IllegalArgumentException("stateId is required");
            State state = stateRepository.findById(stateId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid stateId"));

            Department department = new Department();
            applyDepartmentFields(department, request.getName(), request.getLocalName(), request.getLgdCode());
            department.setState(state);
            department = departmentRepository.save(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDepartmentResponse(department));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/departments")
    public ResponseEntity<?> listDepartments() {
        List<DepartmentResponse> items = departmentRepository.findAll().stream()
                .map(AdminMastersController::toDepartmentResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable("id") Long id, @RequestBody DepartmentUpdateRequest request) {
        try {
            Long departmentId = id;
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department id"));

            Long stateId = request.getStateId();
            if (stateId == null) throw new IllegalArgumentException("stateId is required");
            State state = stateRepository.findById(stateId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid stateId"));

            applyDepartmentFields(department, request.getName(), request.getLocalName(), request.getLgdCode());
            department.setState(state);
            department = departmentRepository.save(department);
            return ResponseEntity.ok(toDepartmentResponse(department));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable("id") Long id) {
        Long departmentId = id;
        if (!departmentRepository.existsById(departmentId)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Invalid department id");
            return ResponseEntity.badRequest().body(body);
        }
        departmentRepository.deleteById(departmentId);
        Map<String, Object> body = new HashMap<>();
        body.put("deleted", true);
        body.put("id", departmentId);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/states")
    public ResponseEntity<?> createState(@RequestBody BoundaryMasterCreateRequest request) {
        try {
            State state = new State();
            applyBoundaryFields(state, request);
            state = stateRepository.save(state);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(state, null, null, null, null, null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/states")
    public ResponseEntity<?> listStates() {
        List<BoundaryMasterResponse> items = stateRepository.findAll().stream()
                .map(state -> toResponse(state, null, null, null, null, null))
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/divisions")
    public ResponseEntity<?> createDivision(@RequestBody DivisionCreateRequest request) {
        try {
            Long stateId = request.getStateId();
            if (stateId == null) throw new IllegalArgumentException("stateId is required");
            State state = stateRepository.findById(stateId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid stateId"));

            Division division = new Division();
            applyBoundaryFields(division, request);
            division.setState(state);
            division = divisionRepository.save(division);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(division, state.getId(), null, null, null, null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/divisions")
    public ResponseEntity<?> listDivisions(@RequestParam(name = "stateId", required = false) Long stateId) {
        List<BoundaryMasterResponse> items = divisionRepository.findAll().stream()
                .filter(d -> stateId == null || (d.getState() != null && stateId.equals(d.getState().getId())))
                .map(d -> toResponse(d, d.getState() == null ? null : d.getState().getId(), null, null, null, null))
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/districts")
    public ResponseEntity<?> createDistrict(@RequestBody DistrictCreateRequest request) {
        try {
            Long stateId = request.getStateId();
            if (stateId == null) throw new IllegalArgumentException("stateId is required");
            State state = stateRepository.findById(stateId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid stateId"));

            Division division = null;
            Long divisionId = request.getDivisionId();
            if (divisionId != null) {
                division = divisionRepository.findById(divisionId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid divisionId"));
            }

            District district = new District();
            applyBoundaryFields(district, request);
            district.setState(state);
            district.setDivision(division);
            district = districtRepository.save(district);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(district, state.getId(), division == null ? null : division.getId(), null, null, null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/districts")
    public ResponseEntity<?> listDistricts(
            @RequestParam(name = "stateId", required = false) Long stateId,
            @RequestParam(name = "divisionId", required = false) Long divisionId
    ) {
        List<BoundaryMasterResponse> items = districtRepository.findAll().stream()
                .filter(d -> stateId == null || (d.getState() != null && stateId.equals(d.getState().getId())))
                .filter(d -> divisionId == null || (d.getDivision() != null && divisionId.equals(d.getDivision().getId())))
                .map(d -> toResponse(
                        d,
                        d.getState() == null ? null : d.getState().getId(),
                        d.getDivision() == null ? null : d.getDivision().getId(),
                        null,
                        null,
                        null
                ))
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/subdistricts")
    public ResponseEntity<?> createSubdistrict(@RequestBody SubdistrictCreateRequest request) {
        try {
            Long districtId = request.getDistrictId();
            if (districtId == null) throw new IllegalArgumentException("districtId is required");
            District district = districtRepository.findById(districtId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid districtId"));

            Subdistrict subdistrict = new Subdistrict();
            applyBoundaryFields(subdistrict, request);
            subdistrict.setDistrict(district);
            subdistrict = subdistrictRepository.save(subdistrict);

            Long stateId = district.getState() == null ? null : district.getState().getId();
            Long divisionId = district.getDivision() == null ? null : district.getDivision().getId();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(subdistrict, stateId, divisionId, district.getId(), null, null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/subdistricts")
    public ResponseEntity<?> listSubdistricts(@RequestParam(name = "districtId", required = false) Long districtId) {
        List<BoundaryMasterResponse> items = subdistrictRepository.findAll().stream()
                .filter(s -> districtId == null || (s.getDistrict() != null && districtId.equals(s.getDistrict().getId())))
                .map(s -> {
                    District d = s.getDistrict();
                    Long stateId = (d == null || d.getState() == null) ? null : d.getState().getId();
                    Long divisionId = (d == null || d.getDivision() == null) ? null : d.getDivision().getId();
                    Long dId = d == null ? null : d.getId();
                    return toResponse(s, stateId, divisionId, dId, null, null);
                })
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/talukas")
    public ResponseEntity<?> createTaluka(@RequestBody TalukaCreateRequest request) {
        try {
            Long districtId = request.getDistrictId();
            if (districtId == null) throw new IllegalArgumentException("districtId is required");
            District district = districtRepository.findById(districtId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid districtId"));

            Long subdistrictId = request.getSubdistrictId();
            if (subdistrictId == null) throw new IllegalArgumentException("subdistrictId is required");
            Subdistrict subdistrict = subdistrictRepository.findById(subdistrictId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid subdistrictId"));
            if (subdistrict.getDistrict() == null || subdistrict.getDistrict().getId() == null
                    || !subdistrict.getDistrict().getId().equals(districtId)) {
                throw new IllegalArgumentException("subdistrictId does not belong to districtId");
            }

            Taluka taluka = new Taluka();
            applyBoundaryFields(taluka, request);
            taluka.setDistrict(district);
            taluka.setSubdistrict(subdistrict);
            taluka = talukaRepository.save(taluka);

            Long stateId = district.getState() == null ? null : district.getState().getId();
            Long divisionId = district.getDivision() == null ? null : district.getDivision().getId();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(taluka, stateId, divisionId, district.getId(), subdistrict.getId(), null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/talukas")
    public ResponseEntity<?> listTalukas(@RequestParam(name = "districtId", required = false) Long districtId) {
        List<BoundaryMasterResponse> items = talukaRepository.findAll().stream()
                .filter(t -> districtId == null || (t.getDistrict() != null && districtId.equals(t.getDistrict().getId())))
                .map(t -> {
                    District d = t.getDistrict();
                    Long stateId = (d == null || d.getState() == null) ? null : d.getState().getId();
                    Long divisionId = (d == null || d.getDivision() == null) ? null : d.getDivision().getId();
                    Long dId = d == null ? null : d.getId();
                    Long sdId = (t.getSubdistrict() == null) ? null : t.getSubdistrict().getId();
                    return toResponse(t, stateId, divisionId, dId, sdId, null);
                })
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/villages")
    public ResponseEntity<?> createVillage(@RequestBody VillageCreateRequest request) {
        try {
            Long talukaId = request.getTalukaId();
            if (talukaId == null) throw new IllegalArgumentException("talukaId is required");
            Taluka taluka = talukaRepository.findById(talukaId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid talukaId"));

            Village village = new Village();
            applyBoundaryFields(village, request);
            village.setTaluka(taluka);
            village = villageRepository.save(village);

            District district = taluka.getDistrict();
            Long districtId = district == null ? null : district.getId();
            Long stateId = (district == null || district.getState() == null) ? null : district.getState().getId();
            Long divisionId = (district == null || district.getDivision() == null) ? null : district.getDivision().getId();
            Long subdistrictId = taluka.getSubdistrict() == null ? null : taluka.getSubdistrict().getId();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(village, stateId, divisionId, districtId, subdistrictId, taluka.getId()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/villages")
    public ResponseEntity<?> listVillages(@RequestParam(name = "talukaId", required = false) Long talukaId) {
        List<BoundaryMasterResponse> items = villageRepository.findAll().stream()
                .filter(v -> talukaId == null || (v.getTaluka() != null && talukaId.equals(v.getTaluka().getId())))
                .map(v -> {
                    Taluka t = v.getTaluka();
                    District d = t == null ? null : t.getDistrict();
                    Long districtId = d == null ? null : d.getId();
                    Long stateId = (d == null || d.getState() == null) ? null : d.getState().getId();
                    Long divisionId = (d == null || d.getDivision() == null) ? null : d.getDivision().getId();
                    Long subdistrictId = (t == null || t.getSubdistrict() == null) ? null : t.getSubdistrict().getId();
                    Long tId = t == null ? null : t.getId();
                    return toResponse(v, stateId, divisionId, districtId, subdistrictId, tId);
                })
                .toList();
        return ResponseEntity.ok(items);
    }

    private static void applyBoundaryFields(com.maharashtra.rccms.model.master.BoundaryNamedLgdBase entity,
                                           BoundaryMasterCreateRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
        entity.setName(request.getName().trim());
        entity.setLocalName(request.getLocalName());
        entity.setLgdCode(request.getLgdCode());
    }

    private static BoundaryMasterResponse toResponse(
            com.maharashtra.rccms.model.master.BoundaryNamedLgdBase entity,
            Long stateId,
            Long divisionId,
            Long districtId,
            Long subdistrictId,
            Long talukaId
    ) {
        return new BoundaryMasterResponse(
                entity.getId(),
                entity.getName(),
                entity.getLocalName(),
                entity.getLgdCode(),
                stateId,
                divisionId,
                districtId,
                subdistrictId,
                talukaId
        );
    }

    private static void applyDepartmentFields(Department department, String name, String localName, String lgdCode) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
        department.setName(name.trim());
        department.setLocalName(localName);
        department.setLgdCode(lgdCode);
    }

    private static DepartmentResponse toDepartmentResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getLocalName(),
                department.getLgdCode(),
                department.getState() == null ? null : department.getState().getId()
        );
    }

    private static void applyActFields(Act act, String actCode, String actName, String actNameLocal) {
        if (actCode == null || actCode.trim().isEmpty()) throw new IllegalArgumentException("actCode is required");
        if (actName == null || actName.trim().isEmpty()) throw new IllegalArgumentException("actName is required");
        act.setActCode(actCode.trim());
        act.setActName(actName.trim());
        act.setActNameLocal(actNameLocal);
    }

    private static ActResponse toActResponse(Act act) {
        return new ActResponse(act.getId(), act.getActCode(), act.getActName(), act.getActNameLocal());
    }

    private static void applySectionFields(Section section, String sectionCode, String sectionName, String sectionNameLocal) {
        if (sectionCode == null || sectionCode.trim().isEmpty()) throw new IllegalArgumentException("sectionCode is required");
        if (sectionName == null || sectionName.trim().isEmpty()) throw new IllegalArgumentException("sectionName is required");
        section.setSectionCode(sectionCode.trim());
        section.setSectionName(sectionName.trim());
        section.setSectionNameLocal(sectionNameLocal);
    }

    private static SectionResponse toSectionResponse(Section section) {
        Act act = section.getAct();
        Long actId = act == null ? null : act.getId();
        String actCode = act == null ? null : act.getActCode();
        String actName = act == null ? null : act.getActName();
        String actNameLocal = act == null ? null : act.getActNameLocal();
        return new SectionResponse(
                section.getId(),
                actId,
                actCode,
                actName,
                actNameLocal,
                section.getSectionCode(),
                section.getSectionName(),
                section.getSectionNameLocal()
        );
    }

    private static void applySubjectFields(Subject subject,
                                          Department department,
                                          String subjectCode,
                                          String subjectName,
                                          String subjectNameLocal) {
        if (subjectCode == null || subjectCode.trim().isEmpty()) throw new IllegalArgumentException("subjectCode is required");
        if (subjectName == null || subjectName.trim().isEmpty()) throw new IllegalArgumentException("subjectName is required");
        subject.setDepartment(department);
        subject.setSubjectCode(subjectCode.trim());
        subject.setSubjectName(subjectName.trim());
        subject.setSubjectNameLocal(subjectNameLocal);
    }

    private static SubjectResponse toSubjectResponse(Subject subject) {
        Department department = subject.getDepartment();
        Long departmentId = department == null ? null : department.getId();
        String departmentName = department == null ? null : department.getName();
        String departmentLocalName = department == null ? null : department.getLocalName();
        return new SubjectResponse(
                subject.getId(),
                departmentId,
                departmentName,
                departmentLocalName,
                subject.getSubjectCode(),
                subject.getSubjectName(),
                subject.getSubjectNameLocal()
        );
    }

    private static void applyOfficeFields(Office office,
                                          String level,
                                          Long locationId,
                                          String name,
                                          String localName,
                                          String shortName,
                                          String shortNameLocal) {
        if (level == null || level.trim().isEmpty()) throw new IllegalArgumentException("level is required");
        if (locationId == null) throw new IllegalArgumentException("locationId is required");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name is required");
        office.setLevel(level.trim().toUpperCase());
        office.setLocationId(locationId);
        office.setName(name.trim());
        office.setLocalName(localName);
        office.setShortName(shortName);
        office.setShortNameLocal(shortNameLocal);
    }

    private OfficeResponse toOfficeResponse(Office office) {
        Department department = office.getDepartment();
        Long departmentId = department == null ? null : department.getId();
        String departmentName = department == null ? null : department.getName();
        String departmentLocalName = department == null ? null : department.getLocalName();

        OfficeType officeType = office.getOfficeType();
        Long officeTypeId = officeType == null ? null : officeType.getId();
        String officeTypeName = officeType == null ? null : officeType.getName();
        String officeTypeLocalName = officeType == null ? null : officeType.getLocalName();

        return new OfficeResponse(
                office.getId(),
                departmentId,
                departmentName,
                departmentLocalName,
                officeTypeId,
                officeTypeName,
                officeTypeLocalName,
                office.getLevel(),
                office.getLocationId(),
                office.getName(),
                office.getLocalName(),
                office.getShortName(),
                office.getShortNameLocal()
        );
    }

    private Department requireDepartment(Long departmentId) {
        if (departmentId == null) throw new IllegalArgumentException("departmentId is required");
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid departmentId"));
    }

    private OfficeType requireOfficeType(Long officeTypeId) {
        if (officeTypeId == null) throw new IllegalArgumentException("officeTypeId is required");
        return officeTypeRepository.findById(officeTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid officeTypeId"));
    }

    private void validateLocation(String levelRaw, Long locationId) {
        if (levelRaw == null || levelRaw.trim().isEmpty()) throw new IllegalArgumentException("level is required");
        if (locationId == null) throw new IllegalArgumentException("locationId is required");
        String level = levelRaw.trim().toUpperCase();

        boolean exists = switch (level) {
            case "STATE" -> stateRepository.existsById(locationId);
            case "DIVISION" -> divisionRepository.existsById(locationId);
            case "DISTRICT" -> districtRepository.existsById(locationId);
            case "SUBDISTRICT" -> subdistrictRepository.existsById(locationId);
            case "TALUKA" -> talukaRepository.existsById(locationId);
            case "VILLAGE" -> villageRepository.existsById(locationId);
            default -> throw new IllegalArgumentException("Invalid level");
        };

        if (!exists) throw new IllegalArgumentException("Invalid locationId for level " + level);
    }

    private static void applyOfficeTypeFields(OfficeType officeType,
                                              String name,
                                              String localName,
                                              String shortName,
                                              String shortNameLocal) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name is required");
        officeType.setName(name.trim());
        officeType.setLocalName(localName);
        officeType.setShortName(shortName);
        officeType.setShortNameLocal(shortNameLocal);
    }

    private static OfficeTypeResponse toOfficeTypeResponse(OfficeType officeType) {
        Department department = officeType.getDepartment();
        Long departmentId = department == null ? null : department.getId();
        String departmentName = department == null ? null : department.getName();
        String departmentLocalName = department == null ? null : department.getLocalName();
        return new OfficeTypeResponse(
                officeType.getId(),
                departmentId,
                departmentName,
                departmentLocalName,
                officeType.getName(),
                officeType.getLocalName(),
                officeType.getShortName(),
                officeType.getShortNameLocal()
        );
    }
}

