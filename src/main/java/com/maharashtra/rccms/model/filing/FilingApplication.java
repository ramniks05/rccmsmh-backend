package com.maharashtra.rccms.model.filing;

import com.maharashtra.rccms.model.AdvocateRegistration;
import com.maharashtra.rccms.model.PartyInPersonRegistration;
import com.maharashtra.rccms.model.master.Act;
import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.model.master.District;
import com.maharashtra.rccms.model.master.Office;
import com.maharashtra.rccms.model.master.Section;
import com.maharashtra.rccms.model.master.Subdistrict;
import com.maharashtra.rccms.model.master.Subject;
import com.maharashtra.rccms.model.master.Taluka;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root for a filing wizard instance (e.g. Category 1 objection).
 * Formal {@code Case} registry and numbering are created when an officer accepts — not here.
 */
@Entity
@Table(
        name = "filing_application",
        uniqueConstraints = @UniqueConstraint(name = "uk_filing_app_client_ref", columnNames = "client_application_ref")
)
@SuppressWarnings("null")
public class FilingApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_application_ref")
    private UUID clientApplicationRef;

    @Column(name = "application_no", length = 64, unique = true)
    private String applicationNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_category_id", nullable = false)
    private CaseCategory caseCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filed_by_advocate_id")
    private AdvocateRegistration filedByAdvocate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filed_by_party_registration_id")
    private PartyInPersonRegistration filedByParty;

    /** Reserved: link to persisted Case when officer registers / accepts */
    @Column(name = "registered_case_id")
    private Long registeredCaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "application_description", columnDefinition = "TEXT")
    private String applicationDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subdistrict_id")
    private Subdistrict subdistrict;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taluka_id")
    private Taluka taluka;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private Office office;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_id")
    private Act act;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    @Column(name = "section_custom_text", length = 1024)
    private String sectionCustomText;

    @Column(name = "mutation_year")
    private Integer mutationYear;

    @Column(name = "mutation_type_filter", length = 255)
    private String mutationTypeFilter;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "approved_by_officer_login_id", length = 150)
    private String approvedByOfficerLoginId;

    @Column(name = "is_forwarded_to_po")
    private Boolean forwardedToPo = false;

    @Column(name = "is_sent_back_to_clerk")
    private Boolean sentBackToClerk = false;

    @Column(name = "is_po_approved")
    private Boolean poApproved = false;

    @Column(name = "is_po_rejected")
    private Boolean poRejected = false;

    @Column(name = "clerk_remarks", columnDefinition = "TEXT")
    private String clerkRemarks;

    @Column(name = "po_remarks", columnDefinition = "TEXT")
    private String poRemarks;

    @Column(name = "last_action_by_role", length = 32)
    private String lastActionByRole;

    @Column(name = "last_action_at")
    private Instant lastActionAt;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApplicationDisputedOrder disputedOrder;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationApplicant> applicants = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationRespondent> respondents = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationVakalatnamaGroup> vakalatnamaGroups = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationDisputedLand> disputedLands = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationAttachment> attachments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == ApplicationStatus.SUBMITTED && this.submittedAt == null) {
            this.submittedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
        if (this.status == ApplicationStatus.SUBMITTED && this.submittedAt == null) {
            this.submittedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public UUID getClientApplicationRef() {
        return clientApplicationRef;
    }

    public void setClientApplicationRef(UUID clientApplicationRef) {
        this.clientApplicationRef = clientApplicationRef;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public CaseCategory getCaseCategory() {
        return caseCategory;
    }

    public void setCaseCategory(CaseCategory caseCategory) {
        this.caseCategory = caseCategory;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public AdvocateRegistration getFiledByAdvocate() {
        return filedByAdvocate;
    }

    public void setFiledByAdvocate(AdvocateRegistration filedByAdvocate) {
        this.filedByAdvocate = filedByAdvocate;
    }

    public PartyInPersonRegistration getFiledByParty() {
        return filedByParty;
    }

    public void setFiledByParty(PartyInPersonRegistration filedByParty) {
        this.filedByParty = filedByParty;
    }

    public Long getRegisteredCaseId() {
        return registeredCaseId;
    }

    public void setRegisteredCaseId(Long registeredCaseId) {
        this.registeredCaseId = registeredCaseId;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public void setApplicationDescription(String applicationDescription) {
        this.applicationDescription = applicationDescription;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Subdistrict getSubdistrict() {
        return subdistrict;
    }

    public void setSubdistrict(Subdistrict subdistrict) {
        this.subdistrict = subdistrict;
    }

    public Taluka getTaluka() {
        return taluka;
    }

    public void setTaluka(Taluka taluka) {
        this.taluka = taluka;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public Act getAct() {
        return act;
    }

    public void setAct(Act act) {
        this.act = act;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getApprovedByOfficerLoginId() {
        return approvedByOfficerLoginId;
    }

    public void setApprovedByOfficerLoginId(String approvedByOfficerLoginId) {
        this.approvedByOfficerLoginId = approvedByOfficerLoginId;
    }

    public Boolean getForwardedToPo() {
        return forwardedToPo;
    }

    public void setForwardedToPo(Boolean forwardedToPo) {
        this.forwardedToPo = forwardedToPo;
    }

    public Boolean getSentBackToClerk() {
        return sentBackToClerk;
    }

    public void setSentBackToClerk(Boolean sentBackToClerk) {
        this.sentBackToClerk = sentBackToClerk;
    }

    public Boolean getPoApproved() {
        return poApproved;
    }

    public void setPoApproved(Boolean poApproved) {
        this.poApproved = poApproved;
    }

    public Boolean getPoRejected() {
        return poRejected;
    }

    public void setPoRejected(Boolean poRejected) {
        this.poRejected = poRejected;
    }

    public String getClerkRemarks() {
        return clerkRemarks;
    }

    public void setClerkRemarks(String clerkRemarks) {
        this.clerkRemarks = clerkRemarks;
    }

    public String getPoRemarks() {
        return poRemarks;
    }

    public void setPoRemarks(String poRemarks) {
        this.poRemarks = poRemarks;
    }

    public String getLastActionByRole() {
        return lastActionByRole;
    }

    public void setLastActionByRole(String lastActionByRole) {
        this.lastActionByRole = lastActionByRole;
    }

    public Instant getLastActionAt() {
        return lastActionAt;
    }

    public void setLastActionAt(Instant lastActionAt) {
        this.lastActionAt = lastActionAt;
    }

    public ApplicationDisputedOrder getDisputedOrder() {
        return disputedOrder;
    }

    public void setDisputedOrder(ApplicationDisputedOrder disputedOrder) {
        this.disputedOrder = disputedOrder;
    }

    public List<ApplicationApplicant> getApplicants() {
        return applicants;
    }

    public List<ApplicationRespondent> getRespondents() {
        return respondents;
    }

    public List<ApplicationVakalatnamaGroup> getVakalatnamaGroups() {
        return vakalatnamaGroups;
    }

    public List<ApplicationDisputedLand> getDisputedLands() {
        return disputedLands;
    }

    public List<ApplicationAttachment> getAttachments() {
        return attachments;
    }
}
