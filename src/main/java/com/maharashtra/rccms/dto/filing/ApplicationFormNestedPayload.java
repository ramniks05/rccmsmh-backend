package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

/**
 * Mirrors the reactive <code>form</code> object from Category 1 wizard submit/draft payloads.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationFormNestedPayload {

    private Long subjectId;
    private String applicationDescription;

    private Long districtId;
    /** UI may send 0 for "unset" */
    private Long subdistrictId;
    private Long talukaId;
    private Long officeId;
    private String officeCode;

    private Long actId;
    private String actCode;
    private Long sectionId;
    private String sectionCode;
    @JsonAlias({"customSectionName"})
    private String sectionCustomText;

    private Integer mutationYear;
    private String mutationTypeFilter;

    private List<ApplicantRowPayload> applicants;
    private List<RespondentRowPayload> respondents;

    @JsonAlias({"vakaltnamaAssignments"})
    private List<VakalatnamaGroupPayload> vakalatnamaAssignments;

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

    public Long getSubdistrictId() {
        return subdistrictId;
    }

    public void setSubdistrictId(Long subdistrictId) {
        this.subdistrictId = subdistrictId;
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

    @JsonSetter("mutationYear")
    public void setMutationYear(Object mutationYear) {
        if (mutationYear == null) {
            this.mutationYear = null;
            return;
        }
        if (mutationYear instanceof Number n) {
            this.mutationYear = n.intValue();
            return;
        }
        String text = mutationYear.toString().trim();
        if (text.isEmpty()) {
            this.mutationYear = null;
            return;
        }
        this.mutationYear = Integer.parseInt(text);
    }

    public String getMutationTypeFilter() {
        return mutationTypeFilter;
    }

    public void setMutationTypeFilter(String mutationTypeFilter) {
        this.mutationTypeFilter = mutationTypeFilter;
    }

    public List<ApplicantRowPayload> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<ApplicantRowPayload> applicants) {
        this.applicants = applicants;
    }

    public List<RespondentRowPayload> getRespondents() {
        return respondents;
    }

    public void setRespondents(List<RespondentRowPayload> respondents) {
        this.respondents = respondents;
    }

    public List<VakalatnamaGroupPayload> getVakalatnamaAssignments() {
        return vakalatnamaAssignments;
    }

    public void setVakalatnamaAssignments(List<VakalatnamaGroupPayload> vakalatnamaAssignments) {
        this.vakalatnamaAssignments = vakalatnamaAssignments;
    }
}
