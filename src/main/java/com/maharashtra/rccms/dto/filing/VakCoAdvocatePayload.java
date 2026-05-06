package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VakCoAdvocatePayload {

    private Long advocateId;
    private AdvocateSnapshotPayload advocate;

    public Long getAdvocateId() {
        return advocateId;
    }

    public void setAdvocateId(Long advocateId) {
        this.advocateId = advocateId;
    }

    public AdvocateSnapshotPayload getAdvocate() {
        return advocate;
    }

    public void setAdvocate(AdvocateSnapshotPayload advocate) {
        this.advocate = advocate;
    }
}
