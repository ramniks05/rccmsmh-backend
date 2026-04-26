package com.maharashtra.rccms.dto;

import java.time.LocalDate;

public class EmployeePostingCreateRequest {
    private Long officeId;
    private Long officeBranchId; // optional
    private Long designationId;
    private LocalDate fromDate;

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public Long getOfficeBranchId() {
        return officeBranchId;
    }

    public void setOfficeBranchId(Long officeBranchId) {
        this.officeBranchId = officeBranchId;
    }

    public Long getDesignationId() {
        return designationId;
    }

    public void setDesignationId(Long designationId) {
        this.designationId = designationId;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }
}

