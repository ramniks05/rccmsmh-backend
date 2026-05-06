package com.maharashtra.rccms.model.filing;

import com.maharashtra.rccms.model.AdvocateRegistration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "application_vakalatnama_co_advocate")
@SuppressWarnings("null")
public class ApplicationVakalatnamaCoAdvocate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vakalatnama_group_id", nullable = false)
    private ApplicationVakalatnamaGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advocate_registration_id")
    private AdvocateRegistration advocateRegistration;

    @Column(name = "snapshot_full_name", length = 512)
    private String snapshotFullName;

    @Column(name = "snapshot_email", length = 190)
    private String snapshotEmail;

    @Column(name = "snapshot_mobile", length = 32)
    private String snapshotMobile;

    @Column(name = "snapshot_address", columnDefinition = "TEXT")
    private String snapshotAddress;

    @Column(name = "snapshot_bar_council_number", length = 80)
    private String snapshotBarCouncilNumber;

    @Column(name = "snapshot_enrollment_number", length = 80)
    private String snapshotEnrollmentNumber;

    @Column(name = "snapshot_law_firm_name", length = 160)
    private String snapshotLawFirmName;

    public Long getId() {
        return id;
    }

    public ApplicationVakalatnamaGroup getGroup() {
        return group;
    }

    public void setGroup(ApplicationVakalatnamaGroup group) {
        this.group = group;
    }

    public AdvocateRegistration getAdvocateRegistration() {
        return advocateRegistration;
    }

    public void setAdvocateRegistration(AdvocateRegistration advocateRegistration) {
        this.advocateRegistration = advocateRegistration;
    }

    public String getSnapshotFullName() {
        return snapshotFullName;
    }

    public void setSnapshotFullName(String snapshotFullName) {
        this.snapshotFullName = snapshotFullName;
    }

    public String getSnapshotEmail() {
        return snapshotEmail;
    }

    public void setSnapshotEmail(String snapshotEmail) {
        this.snapshotEmail = snapshotEmail;
    }

    public String getSnapshotMobile() {
        return snapshotMobile;
    }

    public void setSnapshotMobile(String snapshotMobile) {
        this.snapshotMobile = snapshotMobile;
    }

    public String getSnapshotAddress() {
        return snapshotAddress;
    }

    public void setSnapshotAddress(String snapshotAddress) {
        this.snapshotAddress = snapshotAddress;
    }

    public String getSnapshotBarCouncilNumber() {
        return snapshotBarCouncilNumber;
    }

    public void setSnapshotBarCouncilNumber(String snapshotBarCouncilNumber) {
        this.snapshotBarCouncilNumber = snapshotBarCouncilNumber;
    }

    public String getSnapshotEnrollmentNumber() {
        return snapshotEnrollmentNumber;
    }

    public void setSnapshotEnrollmentNumber(String snapshotEnrollmentNumber) {
        this.snapshotEnrollmentNumber = snapshotEnrollmentNumber;
    }

    public String getSnapshotLawFirmName() {
        return snapshotLawFirmName;
    }

    public void setSnapshotLawFirmName(String snapshotLawFirmName) {
        this.snapshotLawFirmName = snapshotLawFirmName;
    }
}
