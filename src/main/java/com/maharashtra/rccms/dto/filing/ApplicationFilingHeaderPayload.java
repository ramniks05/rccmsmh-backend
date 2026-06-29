package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationFilingHeaderPayload {

    private Long subjectId;
    private String applicationDescription;
    private Long districtId;
    private Long talukaId;
    private Long officeId;
    private String officeCode;
    private String primaryOfficeCode;
    private Long actId;
    private String actCode;
    private Long sectionId;
    private String sectionCode;
    private String sectionCustomText;
    private Integer mutationYear;
    private String mutationTypeFilter;

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public void setApplicationDescription(String applicationDescription) {
        this.applicationDescription = applicationDescription;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getTalukaId() {
        return talukaId;
    }

    public void setTalukaId(Long talukaId) {
        this.talukaId = talukaId;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getPrimaryOfficeCode() {
        return primaryOfficeCode;
    }

    public void setPrimaryOfficeCode(String primaryOfficeCode) {
        this.primaryOfficeCode = primaryOfficeCode;
    }

    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }

    public String getActCode() {
        return actCode;
    }

    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public String getSectionCustomText() {
        return sectionCustomText;
    }

    public void setSectionCustomText(String sectionCustomText) {
        this.sectionCustomText = sectionCustomText;
    }

    public Integer getMutationYear() {
        return mutationYear;
    }

    public void setMutationYear(Integer mutationYear) {
        this.mutationYear = mutationYear;
    }

    public String getMutationTypeFilter() {
        return mutationTypeFilter;
    }

    public void setMutationTypeFilter(String mutationTypeFilter) {
        this.mutationTypeFilter = mutationTypeFilter;
    }
}
