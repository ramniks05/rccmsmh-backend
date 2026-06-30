package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.ActCreateRequest;
import com.maharashtra.rccms.dto.ActResponse;
import com.maharashtra.rccms.dto.ActUpdateRequest;
import com.maharashtra.rccms.dto.CaseCategoryCreateRequest;
import com.maharashtra.rccms.dto.CaseCategoryResponse;
import com.maharashtra.rccms.dto.CaseCategoryUpdateRequest;
import com.maharashtra.rccms.dto.BoundaryMasterCreateRequest;
import com.maharashtra.rccms.dto.BoundaryNamedCreateRequest;
import com.maharashtra.rccms.dto.BoundaryMasterResponse;
import com.maharashtra.rccms.dto.DepartmentCreateRequest;
import com.maharashtra.rccms.dto.DepartmentResponse;
import com.maharashtra.rccms.dto.DepartmentUpdateRequest;
import com.maharashtra.rccms.dto.DocumentTypeCreateRequest;
import com.maharashtra.rccms.dto.DocumentTypeResponse;
import com.maharashtra.rccms.dto.DocumentTypeUpdateRequest;
import com.maharashtra.rccms.dto.DesignationCreateRequest;
import com.maharashtra.rccms.dto.DesignationResponse;
import com.maharashtra.rccms.dto.DesignationUpdateRequest;
import com.maharashtra.rccms.dto.DistrictCreateRequest;
import com.maharashtra.rccms.dto.DivisionCreateRequest;
import com.maharashtra.rccms.dto.TalukaCreateRequest;
import com.maharashtra.rccms.dto.StateCreateRequest;
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
import com.maharashtra.rccms.dto.OccupationCreateRequest;
import com.maharashtra.rccms.dto.OccupationResponse;
import com.maharashtra.rccms.dto.OccupationUpdateRequest;
import com.maharashtra.rccms.dto.VillageCreateRequest;
import com.maharashtra.rccms.model.master.Act;
import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.model.master.Department;
import com.maharashtra.rccms.model.master.Designation;
import com.maharashtra.rccms.model.master.DocumentType;
import com.maharashtra.rccms.model.master.Section;
import com.maharashtra.rccms.model.master.Subject;
import com.maharashtra.rccms.model.master.Office;
import com.maharashtra.rccms.model.master.BoundaryLevel;
import com.maharashtra.rccms.model.master.OfficeType;
import com.maharashtra.rccms.model.master.Occupation;
import com.maharashtra.rccms.model.master.District;
import com.maharashtra.rccms.model.master.Division;
import com.maharashtra.rccms.model.master.BoundaryNamedBase;
import com.maharashtra.rccms.model.master.BoundaryNamedLgdBase;
import com.maharashtra.rccms.model.master.State;
import com.maharashtra.rccms.model.master.Taluka;
import com.maharashtra.rccms.model.master.Village;
import com.maharashtra.rccms.repository.ActRepository;
import com.maharashtra.rccms.repository.CaseCategoryRepository;
import com.maharashtra.rccms.repository.DepartmentRepository;
import com.maharashtra.rccms.repository.DesignationRepository;
import com.maharashtra.rccms.repository.DocumentTypeMappingRepository;
import com.maharashtra.rccms.repository.DocumentTypeRepository;
import com.maharashtra.rccms.repository.DistrictRepository;
import com.maharashtra.rccms.repository.DivisionRepository;
import com.maharashtra.rccms.repository.OfficeRepository;
import com.maharashtra.rccms.repository.OfficeTypeRepository;
import com.maharashtra.rccms.repository.OccupationRepository;
import com.maharashtra.rccms.repository.SectionRepository;
import com.maharashtra.rccms.repository.StateRepository;
import com.maharashtra.rccms.repository.SubjectRepository;
import com.maharashtra.rccms.repository.TalukaRepository;
import com.maharashtra.rccms.repository.VillageRepository;
import com.maharashtra.rccms.service.CoveredStateService;
import com.maharashtra.rccms.service.OfficeLookupService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/masters")
public class AdminMastersController {

    private final ActRepository actRepository;
    private final CaseCategoryRepository caseCategoryRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeMappingRepository documentTypeMappingRepository;
    private final OfficeRepository officeRepository;
    private final OfficeTypeRepository officeTypeRepository;
    private final OccupationRepository occupationRepository;
    private final StateRepository stateRepository;
    private final DivisionRepository divisionRepository;
    private final DistrictRepository districtRepository;
    private final TalukaRepository talukaRepository;
    private final VillageRepository villageRepository;
    private final CoveredStateService coveredStateService;
    private final OfficeLookupService officeLookupService;

    public AdminMastersController(
            ActRepository actRepository,
            CaseCategoryRepository caseCategoryRepository,
            SectionRepository sectionRepository,
            SubjectRepository subjectRepository,
            DepartmentRepository departmentRepository,
            DesignationRepository designationRepository,
            DocumentTypeRepository documentTypeRepository,
            DocumentTypeMappingRepository documentTypeMappingRepository,
            OfficeRepository officeRepository,
            OfficeTypeRepository officeTypeRepository,
            OccupationRepository occupationRepository,
            StateRepository stateRepository,
            DivisionRepository divisionRepository,
            DistrictRepository districtRepository,
            TalukaRepository talukaRepository,
            VillageRepository villageRepository,
            CoveredStateService coveredStateService,
            OfficeLookupService officeLookupService
    ) {
        this.actRepository = actRepository;
        this.caseCategoryRepository = caseCategoryRepository;
        this.sectionRepository = sectionRepository;
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.designationRepository = designationRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.documentTypeMappingRepository = documentTypeMappingRepository;
        this.officeRepository = officeRepository;
        this.officeTypeRepository = officeTypeRepository;
        this.occupationRepository = occupationRepository;
        this.stateRepository = stateRepository;
        this.divisionRepository = divisionRepository;
        this.districtRepository = districtRepository;
        this.talukaRepository = talukaRepository;
        this.villageRepository = villageRepository;
        this.coveredStateService = coveredStateService;
        this.officeLookupService = officeLookupService;
    }

    @PostMapping("/office-types")
    public ResponseEntity<?> createOfficeType(@RequestBody OfficeTypeCreateRequest request) {
        try {
            Long departmentId = request.getDepartmentId();
            if (departmentId == null) throw new IllegalArgumentException("departmentId is required");
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid departmentId"));

            OfficeType officeType = new OfficeType();
            applyOfficeTypeFields(
                    officeType,
                    request.getBoundaryLevel(),
                    request.getName(),
                    request.getLocalName(),
                    request.getShortName(),
                    request.getShortNameLocal()
            );
            officeType.setDepartment(department);
            officeType = officeTypeRepository.save(officeType);
            return ResponseEntity.status(HttpStatus.CREATED).body(toOfficeTypeResponse(officeType));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/designations")
    public ResponseEntity<?> createDesignation(@RequestBody DesignationCreateRequest request) {
        try {
            Department department = requireDepartment(request.getDepartmentId());

            Designation designation = new Designation();
            designation.setDepartment(department);
            applyDesignationFields(designation, request.getName(), request.getLocalName(), request.getShortName(), request.getShortNameLocal());
            designation = designationRepository.save(designation);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDesignationResponse(designation));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/designations")
    public ResponseEntity<?> listDesignations(@RequestParam(name = "departmentId", required = false) Long departmentId) {
        List<DesignationResponse> items = designationRepository.findAll().stream()
                .filter(d -> departmentId == null || (d.getDepartment() != null && departmentId.equals(d.getDepartment().getId())))
                .map(AdminMastersController::toDesignationResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/designations/{id}")
    public ResponseEntity<?> updateDesignation(@PathVariable("id") Long id, @RequestBody DesignationUpdateRequest request) {
        try {
            Long designationId = id;
            Designation designation = designationRepository.findById(designationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid designation id"));

            Department department = requireDepartment(request.getDepartmentId());
            designation.setDepartment(department);
            applyDesignationFields(designation, request.getName(), request.getLocalName(), request.getShortName(), request.getShortNameLocal());
            designation = designationRepository.save(designation);
            return ResponseEntity.ok(toDesignationResponse(designation));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/designations/{id}")
    public ResponseEntity<?> deleteDesignation(@PathVariable("id") Long id) {
        Long designationId = id;
        if (!designationRepository.existsById(designationId)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Invalid designation id");
            return ResponseEntity.badRequest().body(body);
        }
        designationRepository.deleteById(designationId);
        Map<String, Object> body = new HashMap<>();
        body.put("deleted", true);
        body.put("id", designationId);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/occupations")
    public ResponseEntity<?> createOccupation(@RequestBody OccupationCreateRequest request) {
        try {
            Occupation occupation = new Occupation();
            applyOccupationFields(occupation, request.getName(), request.getLocalName(), request.getShortName(), request.getShortNameLocal());
            occupation = occupationRepository.save(occupation);
            return ResponseEntity.status(HttpStatus.CREATED).body(toOccupationResponse(occupation));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/occupations")
    public ResponseEntity<?> listOccupations() {
        List<OccupationResponse> items = occupationRepository.findAll().stream()
                .sorted((a, b) -> {
                    String an = a.getName() == null ? "" : a.getName();
                    String bn = b.getName() == null ? "" : b.getName();
                    return an.compareToIgnoreCase(bn);
                })
                .map(AdminMastersController::toOccupationResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/occupations/{id}")
    public ResponseEntity<?> updateOccupation(@PathVariable("id") Long id, @RequestBody OccupationUpdateRequest request) {
        try {
            Occupation occupation = occupationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid occupation id"));
            applyOccupationFields(occupation, request.getName(), request.getLocalName(), request.getShortName(), request.getShortNameLocal());
            occupation = occupationRepository.save(occupation);
            return ResponseEntity.ok(toOccupationResponse(occupation));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/occupations/{id}")
    public ResponseEntity<?> deleteOccupation(@PathVariable("id") Long id) {
        if (!occupationRepository.existsById(id)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Invalid occupation id");
            return ResponseEntity.badRequest().body(body);
        }
        occupationRepository.deleteById(id);
        Map<String, Object> body = new HashMap<>();
        body.put("deleted", true);
        body.put("id", id);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/case-categories")
    public ResponseEntity<?> createCaseCategory(@RequestBody CaseCategoryCreateRequest request) {
        try {
            OfficeType hearingOfficeType = requireOfficeType(request.getHearingOfficeTypeId());
            CaseCategory nextCategory = resolveOptionalNextCategory(null, request.getNextCaseCategoryId());

            CaseCategory category = new CaseCategory();
            applyCaseCategoryFields(category, request.getCode(), request.getName(), request.getLocalName(), request.getSequenceNo());
            category.setHearingOfficeType(hearingOfficeType);
            category.setNextCategory(nextCategory);
            category = caseCategoryRepository.save(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(toCaseCategoryResponse(category));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/case-categories")
    public ResponseEntity<?> listCaseCategories(
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "hearingOfficeTypeId", required = false) Long hearingOfficeTypeId
    ) {
        List<CaseCategoryResponse> items = caseCategoryRepository.findAll().stream()
                .filter(c -> hearingOfficeTypeId == null
                        || (c.getHearingOfficeType() != null && hearingOfficeTypeId.equals(c.getHearingOfficeType().getId())))
                .filter(c -> departmentId == null
                        || (c.getHearingOfficeType() != null
                        && c.getHearingOfficeType().getDepartment() != null
                        && departmentId.equals(c.getHearingOfficeType().getDepartment().getId())))
                .sorted((a, b) -> {
                    Integer sa = a.getSequenceNo() == null ? Integer.MAX_VALUE : a.getSequenceNo();
                    Integer sb = b.getSequenceNo() == null ? Integer.MAX_VALUE : b.getSequenceNo();
                    int cmp = sa.compareTo(sb);
                    if (cmp != 0) return cmp;
                    Long ida = a.getId() == null ? Long.MAX_VALUE : a.getId();
                    Long idb = b.getId() == null ? Long.MAX_VALUE : b.getId();
                    return ida.compareTo(idb);
                })
                .map(AdminMastersController::toCaseCategoryResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/case-categories/{id}")
    public ResponseEntity<?> updateCaseCategory(@PathVariable("id") Long id, @RequestBody CaseCategoryUpdateRequest request) {
        try {
            Long categoryId = id;
            CaseCategory category = caseCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid case category id"));

            OfficeType hearingOfficeType = requireOfficeType(request.getHearingOfficeTypeId());
            CaseCategory nextCategory = resolveOptionalNextCategory(categoryId, request.getNextCaseCategoryId());

            applyCaseCategoryFields(category, request.getCode(), request.getName(), request.getLocalName(), request.getSequenceNo());
            category.setHearingOfficeType(hearingOfficeType);
            category.setNextCategory(nextCategory);
            category = caseCategoryRepository.save(category);
            return ResponseEntity.ok(toCaseCategoryResponse(category));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/case-categories/{id}")
    public ResponseEntity<?> deleteCaseCategory(@PathVariable("id") Long id) {
        Long categoryId = id;
        if (!caseCategoryRepository.existsById(categoryId)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Invalid case category id");
            return ResponseEntity.badRequest().body(body);
        }
        for (CaseCategory inbound : caseCategoryRepository.findByNextCategory_Id(categoryId)) {
            inbound.setNextCategory(null);
            caseCategoryRepository.save(inbound);
        }
        caseCategoryRepository.deleteById(categoryId);
        Map<String, Object> body = new HashMap<>();
        body.put("deleted", true);
        body.put("id", categoryId);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/document-types")
    public ResponseEntity<?> createDocumentType(@RequestBody DocumentTypeCreateRequest request) {
        try {
            DocumentType doc = new DocumentType();
            applyDocumentTypeFields(
                    doc,
                    request.getCode(),
                    request.getName(),
                    request.getLocalName(),
                    request.getValidForPhotoId(),
                    request.getValidForAddress(),
                    request.getSourceUrl()
            );
            doc = documentTypeRepository.save(doc);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDocumentTypeResponse(doc));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/document-types")
    public ResponseEntity<?> listDocumentTypes(
            @RequestParam(name = "validForPhotoId", required = false) Boolean validForPhotoId,
            @RequestParam(name = "validForAddress", required = false) Boolean validForAddress
    ) {
        List<DocumentTypeResponse> items = documentTypeRepository.findAll().stream()
                .filter(d -> validForPhotoId == null || validForPhotoId == d.isValidForPhotoId())
                .filter(d -> validForAddress == null || validForAddress == d.isValidForAddress())
                .sorted((a, b) -> {
                    String ca = a.getCode() == null ? "" : a.getCode();
                    String cb = b.getCode() == null ? "" : b.getCode();
                    return ca.compareToIgnoreCase(cb);
                })
                .map(AdminMastersController::toDocumentTypeResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/document-types/{id}")
    public ResponseEntity<?> getDocumentType(@PathVariable("id") Long id) {
        Long docId = id;
        Optional<DocumentType> doc = documentTypeRepository.findById(docId);
        if (doc.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid document type id"));
        }
        return ResponseEntity.ok(toDocumentTypeResponse(doc.get()));
    }

    @PutMapping("/document-types/{id}")
    public ResponseEntity<?> updateDocumentType(@PathVariable("id") Long id, @RequestBody DocumentTypeUpdateRequest request) {
        try {
            Long docId = id;
            DocumentType doc = documentTypeRepository.findById(docId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid document type id"));
            applyDocumentTypeFields(
                    doc,
                    request.getCode(),
                    request.getName(),
                    request.getLocalName(),
                    request.getValidForPhotoId(),
                    request.getValidForAddress(),
                    request.getSourceUrl()
            );
            doc = documentTypeRepository.save(doc);
            return ResponseEntity.ok(toDocumentTypeResponse(doc));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/document-types/{id}")
    public ResponseEntity<?> deleteDocumentType(@PathVariable("id") Long id) {
        Long docId = id;
        if (!documentTypeRepository.existsById(docId)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Invalid document type id");
            return ResponseEntity.badRequest().body(body);
        }
        if (documentTypeMappingRepository.countByDocumentTypeId(docId) > 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error",
                    "Document type is mapped to one or more case category / subject combinations. Remove mappings first."
            ));
        }
        documentTypeRepository.deleteById(docId);
        Map<String, Object> body = new HashMap<>();
        body.put("deleted", true);
        body.put("id", docId);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/offices")
    public ResponseEntity<?> createOffice(@RequestBody OfficeCreateRequest request) {
        try {
            Department department = requireDepartment(request.getDepartmentId());
            OfficeType officeType = requireOfficeType(request.getOfficeTypeId());
            assertOfficeTypeBelongsToDepartment(officeType, department);
            requireOfficeTypeBoundaryLevel(officeType);

            Office office = new Office();
            office.setDepartment(department);
            office.setOfficeType(officeType);
            applyOfficeFields(office, request.getName(), request.getOfficeCode(), request.getLocalName(),
                    request.getShortName(), request.getShortNameLocal(),
                    request.getOfficeAddress(), request.getOfficeAddressLocal(),
                    request.getEmail(), request.getOfficeContactNo());
            applyOfficeBoundaryRefs(office, request.getStateId(), request.getDivisionId(), request.getDistrictId(),
                    request.getTalukaId(), request.getStateLgdCode(), request.getDistrictLgdCode(),
                    request.getTalukaLgdCode());
            office = officeRepository.save(office);
            return ResponseEntity.status(HttpStatus.CREATED).body(OfficeResponse.from(office));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/offices")
    public ResponseEntity<?> listOffices(
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "officeTypeId", required = false) Long officeTypeId,
            @RequestParam(name = "boundaryLevel", required = false) String boundaryLevel,
            @RequestParam(name = "stateId", required = false) Long stateId,
            @RequestParam(name = "divisionId", required = false) Long divisionId,
            @RequestParam(name = "divisionCode", required = false) String divisionCode,
            @RequestParam(name = "districtId", required = false) Long districtId,
            @RequestParam(name = "talukaId", required = false) Long talukaId
    ) {
        try {
            List<OfficeResponse> items = officeLookupService.listOfficeResponses(
                    departmentId, officeTypeId, boundaryLevel,
                    stateId, divisionId, divisionCode, districtId, talukaId
            );
            return ResponseEntity.ok(items);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/offices/{id}")
    public ResponseEntity<?> updateOffice(@PathVariable("id") Long id, @RequestBody OfficeUpdateRequest request) {
        try {
            Long officeId = id;
            Office office = officeRepository.findById(officeId).orElseThrow(() -> new IllegalArgumentException("Invalid office id"));

            Department department = requireDepartment(request.getDepartmentId());
            OfficeType officeType = requireOfficeType(request.getOfficeTypeId());
            assertOfficeTypeBelongsToDepartment(officeType, department);
            requireOfficeTypeBoundaryLevel(officeType);

            office.setDepartment(department);
            office.setOfficeType(officeType);
            applyOfficeFields(office, request.getName(), request.getOfficeCode(), request.getLocalName(),
                    request.getShortName(), request.getShortNameLocal(),
                    request.getOfficeAddress(), request.getOfficeAddressLocal(),
                    request.getEmail(), request.getOfficeContactNo());
            applyOfficeBoundaryRefs(office, request.getStateId(), request.getDivisionId(), request.getDistrictId(),
                    request.getTalukaId(), request.getStateLgdCode(), request.getDistrictLgdCode(),
                    request.getTalukaLgdCode());
            office = officeRepository.save(office);
            return ResponseEntity.ok(OfficeResponse.from(office));
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

            applyOfficeTypeFields(
                    officeType,
                    request.getBoundaryLevel(),
                    request.getName(),
                    request.getLocalName(),
                    request.getShortName(),
                    request.getShortNameLocal()
            );
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
            State state = coveredStateService.requireCoveredStateById(stateId);

            Department department = new Department();
            applyDepartmentFields(department, request.getName(), request.getLocalName());
            department.setState(state);
            department = departmentRepository.save(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDepartmentResponse(department));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/departments")
    public ResponseEntity<?> listDepartments() {
        List<Department> departments = listDepartmentsForAdmin();
        List<DepartmentResponse> items = departments.stream()
                .map(AdminMastersController::toDepartmentResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    private List<Department> listDepartmentsForAdmin() {
        Long coveredStateId = coveredStateService.coveredStateIdOrNull();
        if (coveredStateId != null) {
            List<Department> scoped = departmentRepository.findByStateIdOrderByNameAsc(coveredStateId);
            if (!scoped.isEmpty()) {
                return scoped;
            }
        }
        List<Department> filtered = departmentRepository.findAll().stream()
                .filter(d -> d.getState() != null && coveredStateService.isCoveredStateEntity(d.getState()))
                .toList();
        if (!filtered.isEmpty()) {
            return filtered;
        }
        return departmentRepository.findAll();
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable("id") Long id, @RequestBody DepartmentUpdateRequest request) {
        try {
            Long departmentId = id;
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department id"));

            Long stateId = request.getStateId();
            if (stateId == null) throw new IllegalArgumentException("stateId is required");
            State state = coveredStateService.requireCoveredStateById(stateId);

            applyDepartmentFields(department, request.getName(), request.getLocalName());
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
    public ResponseEntity<?> createState(@RequestBody StateCreateRequest request) {
        try {
            coveredStateService.requireCoveredStateLgdOnCreate(request.getLgdCode());
            State state = new State();
            applyBoundaryFields(state, request);
            state.setStateOrUT(request.getStateOrUT());
            state = stateRepository.save(state);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(state, state.getId(), null, null, null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/states")
    public ResponseEntity<?> listStates() {
        List<BoundaryMasterResponse> items = coveredStateService.listStatesForDropdown().stream()
                .map(state -> toResponse(state, state.getId(), null, null, null))
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/divisions")
    public ResponseEntity<?> createDivision(@RequestBody DivisionCreateRequest request) {
        try {
            Long stateId = request.getStateId();
            if (stateId == null) throw new IllegalArgumentException("stateId is required");
            State state = coveredStateService.requireCoveredStateById(stateId);

            Division newDivision = new Division();
            applyNamedBoundaryFields(newDivision, request);
            newDivision.setDivisionCode(trimToNull(request.getDivisionCode()));
            newDivision.setState(state);
            newDivision = divisionRepository.save(newDivision);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(newDivision, state.getId(), null, null, null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/divisions")
    public ResponseEntity<?> listDivisions(@RequestParam(name = "stateId", required = false) Long stateId) {
        List<BoundaryMasterResponse> items = divisionRepository.findAll().stream()
                .filter(d -> coveredStateService.matchesOptionalStateFilter(stateId, d.getState()))
                .map(d -> toResponse(d, d.getState() == null ? null : d.getState().getId(), null, null, null))
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/districts")
    public ResponseEntity<?> createDistrict(@RequestBody DistrictCreateRequest request) {
        try {
            Long stateId = request.getStateId();
            if (stateId == null) throw new IllegalArgumentException("stateId is required");
            State state = coveredStateService.requireCoveredStateById(stateId);

            String divisionCode = trimToNull(request.getDivisionCode());
            if (divisionCode != null) {
                divisionRepository.findFirstByStateIdAndDivisionCode(stateId, divisionCode)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid divisionCode for selected state"));
            }

            District district = new District();
            applyBoundaryFields(district, request);
            district.setState(state);
            district.setDivisionCode(divisionCode);
            district = districtRepository.save(district);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(district, state.getId(), divisionCode, null, null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/districts")
    public ResponseEntity<?> listDistricts(
            @RequestParam(name = "stateId", required = false) Long stateId,
            @RequestParam(name = "divisionCode", required = false) String divisionCode
    ) {
        String normalizedDivisionCode = trimToNull(divisionCode);
        List<BoundaryMasterResponse> items = districtRepository.findAll().stream()
                .filter(d -> coveredStateService.matchesOptionalStateFilter(stateId, d.getState()))
                .filter(d -> normalizedDivisionCode == null || normalizedDivisionCode.equals(d.getDivisionCode()))
                .map(d -> toResponse(
                        d,
                        d.getState() == null ? null : d.getState().getId(),
                        d.getDivisionCode(),
                        null,
                        null
                ))
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

            Taluka taluka = new Taluka();
            applyBoundaryFields(taluka, request);
            applyTalukaDistrictRef(taluka, district, request.getDistrictLgdCode());
            taluka = talukaRepository.save(taluka);

            Long stateId = district.getState() == null ? null : district.getState().getId();
            String divisionCode = district.getDivisionCode();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(taluka, stateId, divisionCode, district.getId(), null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/talukas")
    public ResponseEntity<?> listTalukas(@RequestParam(name = "districtId", required = false) Long districtId) {
        List<BoundaryMasterResponse> items = talukaRepository.findAll().stream()
                .filter(t -> districtId == null || (t.getDistrict() != null && districtId.equals(t.getDistrict().getId())))
                .filter(t -> {
                    District d = t.getDistrict();
                    return d != null && d.getState() != null && coveredStateService.isCoveredStateEntity(d.getState());
                })
                .map(t -> {
                    District d = t.getDistrict();
                    Long stateId = (d == null || d.getState() == null) ? null : d.getState().getId();
                    String divisionCode = d == null ? null : d.getDivisionCode();
                    Long dId = d == null ? null : d.getId();
                    return toResponse(t, stateId, divisionCode, dId, t.getId());
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
            applyVillageTalukaRef(village, taluka, request.getTalukaLgdCode());
            village = villageRepository.save(village);

            District district = taluka.getDistrict();
            Long districtId = district == null ? null : district.getId();
            Long stateId = (district == null || district.getState() == null) ? null : district.getState().getId();
            String divisionCode = district == null ? null : district.getDivisionCode();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(village, stateId, divisionCode, districtId, taluka.getId()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/villages")
    public ResponseEntity<?> listVillages(
            @RequestParam(name = "talukaId", required = false) Long talukaId,
            @RequestParam(name = "districtId", required = false) Long districtId,
            @RequestParam(name = "stateId", required = false) Long stateId,
            @RequestParam(name = "divisionCode", required = false) String divisionCode,
            @RequestParam(name = "talukaLgdCode", required = false) String talukaLgdCode
    ) {
        String normalizedDivisionCode = trimToNull(divisionCode);
        String normalizedTalukaLgdCode = trimToNull(talukaLgdCode);
        List<Village> villages = loadVillagesForFilter(talukaId, districtId, normalizedTalukaLgdCode);
        List<BoundaryMasterResponse> items = villages.stream()
                .filter(v -> matchesVillageHierarchyFilter(v, stateId, normalizedDivisionCode))
                .map(this::toVillageMasterResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    private List<Village> loadVillagesForFilter(Long talukaId, Long districtId, String talukaLgdCode) {
        if (talukaId != null) {
            return villageRepository.findByTalukaIdOrderByNameAsc(talukaId);
        }
        if (districtId != null) {
            return villageRepository.findByTaluka_DistrictIdOrderByNameAsc(districtId);
        }
        if (talukaLgdCode != null) {
            return villageRepository.findByTalukaLgdCodeOrderByNameAsc(talukaLgdCode);
        }
        return villageRepository.findAll();
    }

    private boolean matchesVillageHierarchyFilter(Village village, Long stateId, String divisionCode) {
        Taluka taluka = village.getTaluka();
        District district = taluka == null ? null : taluka.getDistrict();
        State state = district == null ? null : district.getState();
        if (!coveredStateService.isCoveredStateEntity(state)) {
            return false;
        }
        if (stateId != null && (state == null || !stateId.equals(state.getId()))) {
            return false;
        }
        if (divisionCode != null && (district == null || !divisionCode.equals(district.getDivisionCode()))) {
            return false;
        }
        return true;
    }

    private BoundaryMasterResponse toVillageMasterResponse(Village village) {
        Taluka taluka = village.getTaluka();
        District district = taluka == null ? null : taluka.getDistrict();
        Long districtId = district == null ? null : district.getId();
        Long stateId = (district == null || district.getState() == null) ? null : district.getState().getId();
        String divisionCode = district == null ? null : district.getDivisionCode();
        Long talukaId = taluka == null ? null : taluka.getId();
        return toResponse(village, stateId, divisionCode, districtId, talukaId);
    }

    private static void applyNamedBoundaryFields(BoundaryNamedBase entity, BoundaryNamedCreateRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
        entity.setName(request.getName().trim());
        entity.setLocalName(request.getLocalName());
    }

    private static void applyBoundaryFields(BoundaryNamedLgdBase entity, BoundaryMasterCreateRequest request) {
        applyNamedBoundaryFields(entity, request);
        entity.setLgdCode(request.getLgdCode());
    }

    private BoundaryMasterResponse toResponse(
            BoundaryNamedBase entity,
            Long stateId,
            String parentDivisionCode,
            Long districtId,
            Long talukaId
    ) {
        String lgdCode = null;
        if (entity instanceof BoundaryNamedLgdBase) {
            lgdCode = ((BoundaryNamedLgdBase) entity).getLgdCode();
        }
        String stateOrUT = null;
        if (entity instanceof State) {
            stateOrUT = ((State) entity).getStateOrUT();
        }
        String divisionCode = parentDivisionCode;
        Long divisionId = null;
        if (entity instanceof Division division) {
            divisionCode = division.getDivisionCode();
            divisionId = division.getId();
        } else if (entity instanceof District district) {
            divisionCode = district.getDivisionCode();
            divisionId = resolveDivisionId(stateId, divisionCode);
        } else {
            divisionId = resolveDivisionId(stateId, divisionCode);
        }
        String districtLgdCode = null;
        if (entity instanceof Taluka taluka) {
            districtLgdCode = taluka.getDistrictLgdCode();
        }
        String talukaLgdCode = null;
        if (entity instanceof Village village) {
            talukaLgdCode = village.getTalukaLgdCode();
        }
        return new BoundaryMasterResponse(
                entity.getId(),
                entity.getName(),
                entity.getLocalName(),
                lgdCode,
                stateOrUT,
                divisionCode,
                stateId,
                divisionId,
                districtId,
                districtLgdCode,
                talukaId,
                talukaLgdCode
        );
    }

    private Long resolveDivisionId(Long stateId, String divisionCode) {
        if (stateId == null || divisionCode == null || divisionCode.isBlank()) {
            return null;
        }
        return divisionRepository.findFirstByStateIdAndDivisionCode(stateId, divisionCode.trim())
                .map(Division::getId)
                .orElse(null);
    }

    private static void applyVillageTalukaRef(Village village, Taluka taluka, String rawTalukaLgdCode) {
        village.setTaluka(taluka);
        village.setTalukaLgdCode(
                resolveParentLgdCode(rawTalukaLgdCode, taluka.getLgdCode(), "talukaLgdCode")
        );
    }

    private static void applyTalukaDistrictRef(Taluka taluka, District district, String rawDistrictLgdCode) {
        taluka.setDistrict(district);
        taluka.setDistrictLgdCode(
                resolveParentLgdCode(rawDistrictLgdCode, district.getLgdCode(), "districtLgdCode")
        );
    }

    private static String resolveParentLgdCode(String rawProvided, String fromMaster, String fieldLabel) {
        String provided = rawProvided == null ? null : rawProvided.trim();
        if (provided != null && provided.isEmpty()) {
            provided = null;
        }
        if (provided != null && fromMaster != null && !provided.equals(fromMaster)) {
            throw new IllegalArgumentException(fieldLabel + " does not match selected parent");
        }
        return provided != null ? provided : fromMaster;
    }

    private static void applyDepartmentFields(Department department, String name, String localName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
        department.setName(name.trim());
        department.setLocalName(localName);
    }

    private static DepartmentResponse toDepartmentResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getLocalName(),
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

    private static void applyDesignationFields(Designation designation,
                                               String name,
                                               String localName,
                                               String shortName,
                                               String shortNameLocal) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name is required");
        designation.setName(name.trim());
        designation.setLocalName(localName);
        designation.setShortName(shortName);
        designation.setShortNameLocal(shortNameLocal);
    }

    private static DesignationResponse toDesignationResponse(Designation designation) {
        Department department = designation.getDepartment();
        Long departmentId = department == null ? null : department.getId();
        String departmentName = department == null ? null : department.getName();
        String departmentLocalName = department == null ? null : department.getLocalName();
        return new DesignationResponse(
                designation.getId(),
                departmentId,
                departmentName,
                departmentLocalName,
                designation.getName(),
                designation.getLocalName(),
                designation.getShortName(),
                designation.getShortNameLocal()
        );
    }

    private static void applyOccupationFields(Occupation occupation,
                                              String name,
                                              String localName,
                                              String shortName,
                                              String shortNameLocal) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name is required");
        occupation.setName(name.trim());
        occupation.setLocalName(localName);
        occupation.setShortName(shortName);
        occupation.setShortNameLocal(shortNameLocal);
    }

    private static OccupationResponse toOccupationResponse(Occupation occupation) {
        return new OccupationResponse(
                occupation.getId(),
                occupation.getName(),
                occupation.getLocalName(),
                occupation.getShortName(),
                occupation.getShortNameLocal()
        );
    }

    private static void applyOfficeFields(Office office,
                                          String name,
                                          String officeCode,
                                          String localName,
                                          String shortName,
                                          String shortNameLocal,
                                          String officeAddress,
                                          String officeAddressLocal,
                                          String email,
                                          String officeContactNo) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name is required");
        office.setName(name.trim());
        office.setOfficeCode(trimToNull(officeCode));
        office.setLocalName(localName);
        office.setShortName(shortName);
        office.setShortNameLocal(shortNameLocal);
        office.setOfficeAddress(trimToNull(officeAddress));
        office.setOfficeAddressLocal(trimToNull(officeAddressLocal));
        office.setEmail(trimToNull(email));
        office.setOfficeContactNo(trimToNull(officeContactNo));
    }

    private void applyOfficeBoundaryRefs(Office office,
                                         Long stateId,
                                         Long divisionId,
                                         Long districtId,
                                         Long talukaId,
                                         String stateLgdCode,
                                         String districtLgdCode,
                                         String talukaLgdCode) {
        State state = resolveOptionalBoundary(stateId, stateRepository, "stateId");
        if (state != null) {
            coveredStateService.requireCoveredStateById(state.getId());
        }
        District district = resolveOptionalBoundary(districtId, districtRepository, "districtId");
        Taluka taluka = resolveOptionalBoundary(talukaId, talukaRepository, "talukaId");
        office.setState(state);
        office.setDivision(resolveOptionalBoundary(divisionId, divisionRepository, "divisionId"));
        office.setDistrict(district);
        office.setTaluka(taluka);
        office.setStateLgdCode(
                resolveParentLgdCode(stateLgdCode, state == null ? null : state.getLgdCode(), "stateLgdCode")
        );
        office.setDistrictLgdCode(
                resolveParentLgdCode(districtLgdCode, district == null ? null : district.getLgdCode(), "districtLgdCode")
        );
        office.setTalukaLgdCode(
                resolveParentLgdCode(talukaLgdCode, taluka == null ? null : taluka.getLgdCode(), "talukaLgdCode")
        );
    }

    private static <T> T resolveOptionalBoundary(Long id,
                                                 org.springframework.data.repository.CrudRepository<T, Long> repository,
                                                 String fieldName) {
        if (id == null) {
            return null;
        }
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid " + fieldName));
    }

    private static String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private CaseCategory resolveOptionalNextCategory(Long categoryId, Long nextCaseCategoryId) {
        if (nextCaseCategoryId == null) {
            return null;
        }
        CaseCategory next = caseCategoryRepository.findById(nextCaseCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid nextCaseCategoryId"));
        assertNoNextCategoryCycle(categoryId, nextCaseCategoryId);
        return next;
    }

    private void assertNoNextCategoryCycle(Long categoryId, Long nextCategoryId) {
        if (nextCategoryId == null) {
            return;
        }
        if (categoryId != null && categoryId.equals(nextCategoryId)) {
            throw new IllegalArgumentException("nextCaseCategoryId cannot reference the same category");
        }

        Long current = nextCategoryId;
        Set<Long> visited = new HashSet<>();
        while (current != null) {
            if (categoryId != null && categoryId.equals(current)) {
                throw new IllegalArgumentException("nextCaseCategoryId would create a cycle");
            }
            if (!visited.add(current)) {
                throw new IllegalArgumentException("nextCaseCategoryId chain contains a cycle");
            }
            CaseCategory node = caseCategoryRepository.findById(current)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid nextCaseCategoryId"));
            CaseCategory next = node.getNextCategory();
            current = next == null ? null : next.getId();
        }
    }

    private static void applyCaseCategoryFields(CaseCategory category,
                                                String code,
                                                String name,
                                                String localName,
                                                Integer sequenceNo) {
        if (code == null || code.trim().isEmpty()) throw new IllegalArgumentException("code is required");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name is required");
        if (sequenceNo == null) throw new IllegalArgumentException("sequenceNo is required");
        category.setCode(code.trim());
        category.setName(name.trim());
        category.setLocalName(localName);
        category.setSequenceNo(sequenceNo);
    }

    private static CaseCategoryResponse toCaseCategoryResponse(CaseCategory category) {
        OfficeType hearingOfficeType = category.getHearingOfficeType();
        Long hearingOfficeTypeId = hearingOfficeType == null ? null : hearingOfficeType.getId();
        String hearingOfficeTypeName = hearingOfficeType == null ? null : hearingOfficeType.getName();
        String hearingOfficeTypeLocalName = hearingOfficeType == null ? null : hearingOfficeType.getLocalName();

        CaseCategory next = category.getNextCategory();
        Long nextId = next == null ? null : next.getId();
        String nextCode = next == null ? null : next.getCode();
        String nextName = next == null ? null : next.getName();

        return new CaseCategoryResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getLocalName(),
                category.getSequenceNo(),
                hearingOfficeTypeId,
                hearingOfficeTypeName,
                hearingOfficeTypeLocalName,
                nextId,
                nextCode,
                nextName
        );
    }

    private static void applyDocumentTypeFields(DocumentType doc,
                                                String code,
                                                String name,
                                                String localName,
                                                Boolean validForPhotoId,
                                                Boolean validForAddress,
                                                String sourceUrl) {
        if (code == null || code.trim().isEmpty()) throw new IllegalArgumentException("code is required");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name is required");
        String url = sourceUrl == null ? null : sourceUrl.trim();
        if (url != null && url.isEmpty()) {
            url = null;
        }
        if (url != null && url.length() > 2048) {
            throw new IllegalArgumentException("sourceUrl exceeds maximum length (2048)");
        }
        doc.setCode(code.trim());
        doc.setName(name.trim());
        doc.setLocalName(localName);
        doc.setValidForPhotoId(Boolean.TRUE.equals(validForPhotoId));
        doc.setValidForAddress(Boolean.TRUE.equals(validForAddress));
        doc.setSourceUrl(url);
    }

    private static DocumentTypeResponse toDocumentTypeResponse(DocumentType doc) {
        return new DocumentTypeResponse(
                doc.getId(),
                doc.getCode(),
                doc.getName(),
                doc.getLocalName(),
                doc.isValidForPhotoId(),
                doc.isValidForAddress(),
                doc.getSourceUrl()
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

    private static void assertOfficeTypeBelongsToDepartment(OfficeType officeType, Department department) {
        if (officeType.getDepartment() == null
                || department.getId() == null
                || !department.getId().equals(officeType.getDepartment().getId())) {
            throw new IllegalArgumentException("officeTypeId does not belong to departmentId");
        }
    }

    private static String requireOfficeTypeBoundaryLevel(OfficeType officeType) {
        if (officeType.getBoundaryLevel() == null || officeType.getBoundaryLevel().isBlank()) {
            throw new IllegalArgumentException("Selected office type has no boundaryLevel configured");
        }
        return BoundaryLevel.normalize(officeType.getBoundaryLevel());
    }

    private static void applyOfficeTypeFields(OfficeType officeType,
                                              String boundaryLevel,
                                              String name,
                                              String localName,
                                              String shortName,
                                              String shortNameLocal) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name is required");
        officeType.setBoundaryLevel(BoundaryLevel.normalize(boundaryLevel));
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
                officeType.getBoundaryLevel(),
                officeType.getName(),
                officeType.getLocalName(),
                officeType.getShortName(),
                officeType.getShortNameLocal()
        );
    }
}

