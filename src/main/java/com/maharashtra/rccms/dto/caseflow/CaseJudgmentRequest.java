package com.maharashtra.rccms.dto.caseflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseJudgmentRequest {
    private String judgmentSummary;

    public String getJudgmentSummary() {
        return judgmentSummary;
    }

    public void setJudgmentSummary(String judgmentSummary) {
        this.judgmentSummary = judgmentSummary;
    }
}
