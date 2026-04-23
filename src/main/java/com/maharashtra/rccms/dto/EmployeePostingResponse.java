package com.maharashtra.rccms.dto;

import java.time.LocalDate;

public class EmployeePostingResponse {
    private final Long id;
    private final Long employeeId;

    private final Long officeId;
    private final String officeName;

    private final Long designationId;
    private final String designationName;

    private final LocalDate fromDate;
    private final LocalDate toDate;

    public EmployeePostingResponse(
            Long id,
            Long employeeId,
            Long officeId,
            String officeName,
            Long designationId,
            String designationName,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        this.id = id;
        this.employeeId = employeeId;
        this.officeId = officeId;
        this.officeName = officeName;
        this.designationId = designationId;
        this.designationName = designationName;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public Long getDesignationId() {
        return designationId;
    }

    public String getDesignationName() {
        return designationName;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }
}

