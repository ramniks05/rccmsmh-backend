package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDisputedOrderPayload {

    private String searchMode;
    private String searchValue;
    private Boolean mutationFound;
    /** UI: searchedMutation */
    private Boolean mutationSearched;
    private MutationDetailsPayload mutationDetails;
    private String manualInwardNumber;
    private LocalDate manualInwardDate;
    private String manualMutationType;
    private String manualApplicantName;
    private String manualVillage;
    private String manualStatus;
    private Notice9ResolvedPayload notice9Resolved;

    public String getSearchMode() {
        return searchMode;
    }

    public void setSearchMode(String searchMode) {
        this.searchMode = searchMode;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public Boolean getMutationFound() {
        return mutationFound;
    }

    public void setMutationFound(Boolean mutationFound) {
        this.mutationFound = mutationFound;
    }

    public Boolean getMutationSearched() {
        return mutationSearched;
    }

    public void setMutationSearched(Boolean mutationSearched) {
        this.mutationSearched = mutationSearched;
    }

    public MutationDetailsPayload getMutationDetails() {
        return mutationDetails;
    }

    public void setMutationDetails(MutationDetailsPayload mutationDetails) {
        this.mutationDetails = mutationDetails;
    }

    public String getManualInwardNumber() {
        return manualInwardNumber;
    }

    public void setManualInwardNumber(String manualInwardNumber) {
        this.manualInwardNumber = manualInwardNumber;
    }

    public LocalDate getManualInwardDate() {
        return manualInwardDate;
    }

    public void setManualInwardDate(LocalDate manualInwardDate) {
        this.manualInwardDate = manualInwardDate;
    }

    public String getManualMutationType() {
        return manualMutationType;
    }

    public void setManualMutationType(String manualMutationType) {
        this.manualMutationType = manualMutationType;
    }

    public String getManualApplicantName() {
        return manualApplicantName;
    }

    public void setManualApplicantName(String manualApplicantName) {
        this.manualApplicantName = manualApplicantName;
    }

    public String getManualVillage() {
        return manualVillage;
    }

    public void setManualVillage(String manualVillage) {
        this.manualVillage = manualVillage;
    }

    public String getManualStatus() {
        return manualStatus;
    }

    public void setManualStatus(String manualStatus) {
        this.manualStatus = manualStatus;
    }

    public Notice9ResolvedPayload getNotice9Resolved() {
        return notice9Resolved;
    }

    public void setNotice9Resolved(Notice9ResolvedPayload notice9Resolved) {
        this.notice9Resolved = notice9Resolved;
    }
}
