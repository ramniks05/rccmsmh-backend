package com.maharashtra.rccms.model.filing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "application_disputed_order")
@SuppressWarnings("null")
public class ApplicationDisputedOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private FilingApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_mode", length = 32)
    private DisputedOrderSearchMode searchMode;

    @Column(name = "search_value", length = 512)
    private String searchValue;

    @Column(name = "mutation_found")
    private Boolean mutationFound;

    @Column(name = "mutation_searched")
    private Boolean mutationSearched;

    @Column(name = "inward_number", length = 255)
    private String inwardNumber;

    @Column(name = "inward_date")
    private LocalDate inwardDate;

    @Column(name = "mutation_type", length = 255)
    private String mutationType;

    @Column(name = "applicant_name", length = 512)
    private String applicantName;

    @Column(name = "village", length = 512)
    private String village;

    @Column(name = "order_status", length = 255)
    private String orderStatus;

    @Column(name = "attach_file_url", length = 2048)
    private String attachFileUrl;

    @Column(name = "notice9_url_found", length = 2048)
    private String notice9UrlResolved;

    @Column(name = "manual_inward_number", length = 255)
    private String manualInwardNumber;

    @Column(name = "manual_inward_date")
    private LocalDate manualInwardDate;

    @Column(name = "manual_mutation_type", length = 255)
    private String manualMutationType;

    @Column(name = "manual_applicant_name", length = 512)
    private String manualApplicantName;

    @Column(name = "manual_village", length = 512)
    private String manualVillage;

    @Column(name = "manual_status", length = 255)
    private String manualStatus;

    @Column(name = "notice9_available")
    private Boolean notice9Available;

    @Enumerated(EnumType.STRING)
    @Column(name = "notice9_source_kind", length = 16)
    private Notice9SourceKind notice9SourceKind;

    @Column(name = "notice9_url", length = 2048)
    private String notice9Url;

    @Column(name = "notice9_preview_kind", length = 16)
    private String notice9PreviewKind;

    public Long getId() {
        return id;
    }

    public FilingApplication getApplication() {
        return application;
    }

    public void setApplication(FilingApplication application) {
        this.application = application;
    }

    public DisputedOrderSearchMode getSearchMode() {
        return searchMode;
    }

    public void setSearchMode(DisputedOrderSearchMode searchMode) {
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

    public String getInwardNumber() {
        return inwardNumber;
    }

    public void setInwardNumber(String inwardNumber) {
        this.inwardNumber = inwardNumber;
    }

    public LocalDate getInwardDate() {
        return inwardDate;
    }

    public void setInwardDate(LocalDate inwardDate) {
        this.inwardDate = inwardDate;
    }

    public String getMutationType() {
        return mutationType;
    }

    public void setMutationType(String mutationType) {
        this.mutationType = mutationType;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAttachFileUrl() {
        return attachFileUrl;
    }

    public void setAttachFileUrl(String attachFileUrl) {
        this.attachFileUrl = attachFileUrl;
    }

    public String getNotice9UrlResolved() {
        return notice9UrlResolved;
    }

    public void setNotice9UrlResolved(String notice9UrlResolved) {
        this.notice9UrlResolved = notice9UrlResolved;
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

    public Boolean getNotice9Available() {
        return notice9Available;
    }

    public void setNotice9Available(Boolean notice9Available) {
        this.notice9Available = notice9Available;
    }

    public Notice9SourceKind getNotice9SourceKind() {
        return notice9SourceKind;
    }

    public void setNotice9SourceKind(Notice9SourceKind notice9SourceKind) {
        this.notice9SourceKind = notice9SourceKind;
    }

    public String getNotice9Url() {
        return notice9Url;
    }

    public void setNotice9Url(String notice9Url) {
        this.notice9Url = notice9Url;
    }

    public String getNotice9PreviewKind() {
        return notice9PreviewKind;
    }

    public void setNotice9PreviewKind(String notice9PreviewKind) {
        this.notice9PreviewKind = notice9PreviewKind;
    }
}
