package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VakalatnamaGroupPayload {

    private Integer groupNo;
    private Long primaryAdvocateId;
    private AdvocateSnapshotPayload advocate;
    private List<VakCoAdvocatePayload> coAdvocates;
    @JsonAlias({"applicantIds"})
    private List<String> applicantClientRowKeys;

    public Integer getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(Integer groupNo) {
        this.groupNo = groupNo;
    }

    public Long getPrimaryAdvocateId() {
        return primaryAdvocateId;
    }

    public void setPrimaryAdvocateId(Long primaryAdvocateId) {
        this.primaryAdvocateId = primaryAdvocateId;
    }

    public AdvocateSnapshotPayload getAdvocate() {
        return advocate;
    }

    public void setAdvocate(AdvocateSnapshotPayload advocate) {
        this.advocate = advocate;
    }

    public List<VakCoAdvocatePayload> getCoAdvocates() {
        return coAdvocates;
    }

    public void setCoAdvocates(List<VakCoAdvocatePayload> coAdvocates) {
        this.coAdvocates = coAdvocates;
    }

    public List<String> getApplicantClientRowKeys() {
        return applicantClientRowKeys;
    }

    public void setApplicantClientRowKeys(List<String> applicantClientRowKeys) {
        this.applicantClientRowKeys = applicantClientRowKeys;
    }
}
