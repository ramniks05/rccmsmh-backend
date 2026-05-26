package com.maharashtra.rccms.dto.caseflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseJudgmentSignPublishRequest {
    private String summary;
    private String draftSummary;
    private String judgmentSummary;
    private String content;
    private String digitalSignatureRef;
    private String remarks;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDraftSummary() {
        return draftSummary;
    }

    public void setDraftSummary(String draftSummary) {
        this.draftSummary = draftSummary;
    }

    public String getJudgmentSummary() {
        return judgmentSummary;
    }

    public void setJudgmentSummary(String judgmentSummary) {
        this.judgmentSummary = judgmentSummary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDigitalSignatureRef() {
        return digitalSignatureRef;
    }

    public void setDigitalSignatureRef(String digitalSignatureRef) {
        this.digitalSignatureRef = digitalSignatureRef;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String resolveSummary() {
        String value = firstNonBlank(summary, draftSummary, judgmentSummary, content);
        return value != null ? value.trim() : null;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
