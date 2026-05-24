package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Map;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDisputedOrderPayload {

    private String landChannel;
    private String searchCriteriaCode;
    private OrderSearchLocationPayload location;
    private Map<String, Object> criteriaValues;
    private String resolvedInwardNumber;
    private Object mutationSnapshot;
    private Map<String, Object> externalRefs;
    @JsonAlias({"notice9", "notice9Resolved"})
    private Notice9ResolvedPayload notice9;

    private String searchMode;
    private String searchValue;
    private String searchDisplayText;
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

    public String getLandChannel() {
        return landChannel;
    }

    public void setLandChannel(String landChannel) {
        this.landChannel = landChannel;
    }

    public String getSearchCriteriaCode() {
        return searchCriteriaCode;
    }

    public void setSearchCriteriaCode(String searchCriteriaCode) {
        this.searchCriteriaCode = searchCriteriaCode;
    }

    public OrderSearchLocationPayload getLocation() {
        return location;
    }

    public void setLocation(OrderSearchLocationPayload location) {
        this.location = location;
    }

    public Map<String, Object> getCriteriaValues() {
        return criteriaValues;
    }

    public void setCriteriaValues(Map<String, Object> criteriaValues) {
        this.criteriaValues = criteriaValues;
    }

    public String getResolvedInwardNumber() {
        return resolvedInwardNumber;
    }

    public void setResolvedInwardNumber(String resolvedInwardNumber) {
        this.resolvedInwardNumber = resolvedInwardNumber;
    }

    public Object getMutationSnapshot() {
        return mutationSnapshot;
    }

    public void setMutationSnapshot(Object mutationSnapshot) {
        this.mutationSnapshot = mutationSnapshot;
    }

    public Map<String, Object> getExternalRefs() {
        return externalRefs;
    }

    public void setExternalRefs(Map<String, Object> externalRefs) {
        this.externalRefs = externalRefs;
    }

    public Notice9ResolvedPayload getNotice9() {
        return notice9 != null ? notice9 : notice9Resolved;
    }

    public void setNotice9(Notice9ResolvedPayload notice9) {
        this.notice9 = notice9;
    }

    public String getSearchDisplayText() {
        return searchDisplayText;
    }

    public void setSearchDisplayText(String searchDisplayText) {
        this.searchDisplayText = searchDisplayText;
    }

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

    @JsonSetter("mutationSearched")
    public void setMutationSearched(Object mutationSearched) {
        if (mutationSearched == null) {
            this.mutationSearched = null;
            return;
        }
        if (mutationSearched instanceof Boolean b) {
            this.mutationSearched = b;
            return;
        }
        // Some UIs send the searched mutation object itself.
        this.mutationSearched = true;
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

    @JsonSetter("manualInwardDate")
    public void setManualInwardDate(Object manualInwardDate) {
        this.manualInwardDate = parseFlexibleDate(manualInwardDate);
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

    private static LocalDate parseFlexibleDate(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDate d) return d;

        String text = value.toString().trim();
        if (text.isEmpty()) return null;

        try {
            return LocalDate.parse(text);
        } catch (Exception ignore) {
            // try dd/MM/yyyy
        }
        try {
            return LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd or dd/MM/yyyy.");
        }
    }
}
