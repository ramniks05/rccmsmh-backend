package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MutationDetailsPayload {

    private String inwardNumber;
    private LocalDate inwardDate;
    private String mutationType;
    private String applicantName;
    private String village;
    private String status;
    private String attachFileUrl;
    private String notice9Url;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttachFileUrl() {
        return attachFileUrl;
    }

    public void setAttachFileUrl(String attachFileUrl) {
        this.attachFileUrl = attachFileUrl;
    }

    public String getNotice9Url() {
        return notice9Url;
    }

    public void setNotice9Url(String notice9Url) {
        this.notice9Url = notice9Url;
    }
}
