package com.maharashtra.rccms.dto;

import com.maharashtra.rccms.model.master.Department;
import com.maharashtra.rccms.model.master.NamedBoundary;
import com.maharashtra.rccms.model.master.Office;
import com.maharashtra.rccms.model.master.OfficeType;

public class OfficeResponse {
    private final Long id;

    private final Long departmentId;
    private final String departmentName;
    private final String departmentLocalName;

    private final Long officeTypeId;
    private final String officeTypeName;
    private final String officeTypeLocalName;
    /** Derived from office type; read-only. */
    private final String boundaryLevel;

    private final Long stateId;
    private final String stateName;
    private final String stateLocalName;

    private final Long divisionId;
    private final String divisionName;
    private final String divisionLocalName;

    private final Long districtId;
    private final String districtName;
    private final String districtLocalName;

    private final Long talukaId;
    private final String talukaName;
    private final String talukaLocalName;

    private final String name;
    private final String officeCode;
    private final String localName;
    private final String shortName;
    private final String shortNameLocal;
    private final String officeAddress;
    private final String officeAddressLocal;
    private final String email;
    private final String officeContactNo;
    private final String stateLgdCode;
    private final String districtLgdCode;
    private final String talukaLgdCode;

    public OfficeResponse(
            Long id,
            Long departmentId,
            String departmentName,
            String departmentLocalName,
            Long officeTypeId,
            String officeTypeName,
            String officeTypeLocalName,
            String boundaryLevel,
            Long stateId,
            String stateName,
            String stateLocalName,
            Long divisionId,
            String divisionName,
            String divisionLocalName,
            Long districtId,
            String districtName,
            String districtLocalName,
            Long talukaId,
            String talukaName,
            String talukaLocalName,
            String name,
            String officeCode,
            String localName,
            String shortName,
            String shortNameLocal,
            String officeAddress,
            String officeAddressLocal,
            String email,
            String officeContactNo,
            String stateLgdCode,
            String districtLgdCode,
            String talukaLgdCode
    ) {
        this.id = id;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentLocalName = departmentLocalName;
        this.officeTypeId = officeTypeId;
        this.officeTypeName = officeTypeName;
        this.officeTypeLocalName = officeTypeLocalName;
        this.boundaryLevel = boundaryLevel;
        this.stateId = stateId;
        this.stateName = stateName;
        this.stateLocalName = stateLocalName;
        this.divisionId = divisionId;
        this.divisionName = divisionName;
        this.divisionLocalName = divisionLocalName;
        this.districtId = districtId;
        this.districtName = districtName;
        this.districtLocalName = districtLocalName;
        this.talukaId = talukaId;
        this.talukaName = talukaName;
        this.talukaLocalName = talukaLocalName;
        this.name = name;
        this.officeCode = officeCode;
        this.localName = localName;
        this.shortName = shortName;
        this.shortNameLocal = shortNameLocal;
        this.officeAddress = officeAddress;
        this.officeAddressLocal = officeAddressLocal;
        this.email = email;
        this.officeContactNo = officeContactNo;
        this.stateLgdCode = stateLgdCode;
        this.districtLgdCode = districtLgdCode;
        this.talukaLgdCode = talukaLgdCode;
    }

    public static OfficeResponse from(Office office) {
        Department department = office.getDepartment();
        OfficeType officeType = office.getOfficeType();
        return new OfficeResponse(
                office.getId(),
                department == null ? null : department.getId(),
                department == null ? null : department.getName(),
                department == null ? null : department.getLocalName(),
                officeType == null ? null : officeType.getId(),
                officeType == null ? null : officeType.getName(),
                officeType == null ? null : officeType.getLocalName(),
                officeType == null ? null : officeType.getBoundaryLevel(),
                boundaryId(office.getState()),
                boundaryName(office.getState()),
                boundaryLocalName(office.getState()),
                boundaryId(office.getDivision()),
                boundaryName(office.getDivision()),
                boundaryLocalName(office.getDivision()),
                boundaryId(office.getDistrict()),
                boundaryName(office.getDistrict()),
                boundaryLocalName(office.getDistrict()),
                boundaryId(office.getTaluka()),
                boundaryName(office.getTaluka()),
                boundaryLocalName(office.getTaluka()),
                office.getName(),
                office.getOfficeCode(),
                office.getLocalName(),
                office.getShortName(),
                office.getShortNameLocal(),
                office.getOfficeAddress(),
                office.getOfficeAddressLocal(),
                office.getEmail(),
                office.getOfficeContactNo(),
                office.getStateLgdCode(),
                office.getDistrictLgdCode(),
                office.getTalukaLgdCode()
        );
    }

    private static Long boundaryId(NamedBoundary boundary) {
        return boundary == null ? null : boundary.getId();
    }

    private static String boundaryName(NamedBoundary boundary) {
        return boundary == null ? null : boundary.getName();
    }

    private static String boundaryLocalName(NamedBoundary boundary) {
        return boundary == null ? null : boundary.getLocalName();
    }

    public Long getId() {
        return id;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getDepartmentLocalName() {
        return departmentLocalName;
    }

    public Long getOfficeTypeId() {
        return officeTypeId;
    }

    public String getOfficeTypeName() {
        return officeTypeName;
    }

    public String getOfficeTypeLocalName() {
        return officeTypeLocalName;
    }

    public String getBoundaryLevel() {
        return boundaryLevel;
    }

    public Long getStateId() {
        return stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public String getStateLocalName() {
        return stateLocalName;
    }

    public Long getDivisionId() {
        return divisionId;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public String getDivisionLocalName() {
        return divisionLocalName;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public String getDistrictLocalName() {
        return districtLocalName;
    }

    public Long getTalukaId() {
        return talukaId;
    }

    public String getTalukaName() {
        return talukaName;
    }

    public String getTalukaLocalName() {
        return talukaLocalName;
    }

    public String getName() {
        return name;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public String getLocalName() {
        return localName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getShortNameLocal() {
        return shortNameLocal;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public String getOfficeAddressLocal() {
        return officeAddressLocal;
    }

    public String getEmail() {
        return email;
    }

    public String getOfficeContactNo() {
        return officeContactNo;
    }

    public String getStateLgdCode() {
        return stateLgdCode;
    }

    public String getDistrictLgdCode() {
        return districtLgdCode;
    }

    public String getTalukaLgdCode() {
        return talukaLgdCode;
    }
}
