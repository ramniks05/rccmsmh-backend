package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Category 1 objection (and compatible) wizard save/submit envelope.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("null")
public class ApplicationSavePayload {

    private Long applicationId;
    private String clientApplicationRef;
    private Long caseCategoryId;
    private String status;

    private ApplicationFormNestedPayload form;

    private ApplicationDisputedOrderPayload disputedOrder;

    /** Top-level echoes from Angular; merged into {@link #disputedOrder} when fragments are omitted */
    private Boolean mutationFound;
    private Boolean searchedMutation;
    private MutationDetailsPayload mutationDetails;
    private Notice9ResolvedPayload notice9Resolved;

    private List<VakalatnamaGroupPayload> vakalatnamaAssignments;
    private List<DisputedLandPayload> disputedLands;
    private List<ApplicationAttachmentPayload> attachments;

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getClientApplicationRef() {
        return clientApplicationRef;
    }

    public void setClientApplicationRef(String clientApplicationRef) {
        this.clientApplicationRef = clientApplicationRef;
    }

    public Long getCaseCategoryId() {
        return caseCategoryId;
    }

    public void setCaseCategoryId(Long caseCategoryId) {
        this.caseCategoryId = caseCategoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ApplicationFormNestedPayload getForm() {
        return form;
    }

    public void setForm(ApplicationFormNestedPayload form) {
        this.form = form;
    }

    public ApplicationDisputedOrderPayload getDisputedOrder() {
        return disputedOrder;
    }

    public void setDisputedOrder(ApplicationDisputedOrderPayload disputedOrder) {
        this.disputedOrder = disputedOrder;
    }

    public Boolean getMutationFound() {
        return mutationFound;
    }

    public void setMutationFound(Boolean mutationFound) {
        this.mutationFound = mutationFound;
    }

    public Boolean getSearchedMutation() {
        return searchedMutation;
    }

    public void setSearchedMutation(Boolean searchedMutation) {
        this.searchedMutation = searchedMutation;
    }

    public MutationDetailsPayload getMutationDetails() {
        return mutationDetails;
    }

    public void setMutationDetails(MutationDetailsPayload mutationDetails) {
        this.mutationDetails = mutationDetails;
    }

    public Notice9ResolvedPayload getNotice9Resolved() {
        return notice9Resolved;
    }

    public void setNotice9Resolved(Notice9ResolvedPayload notice9Resolved) {
        this.notice9Resolved = notice9Resolved;
    }

    public List<VakalatnamaGroupPayload> getVakalatnamaAssignments() {
        return vakalatnamaAssignments;
    }

    public void setVakalatnamaAssignments(List<VakalatnamaGroupPayload> vakalatnamaAssignments) {
        this.vakalatnamaAssignments = vakalatnamaAssignments;
    }

    public List<DisputedLandPayload> getDisputedLands() {
        return disputedLands;
    }

    public void setDisputedLands(List<DisputedLandPayload> disputedLands) {
        this.disputedLands = disputedLands;
    }

    public List<ApplicationAttachmentPayload> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ApplicationAttachmentPayload> attachments) {
        this.attachments = attachments;
    }
}
