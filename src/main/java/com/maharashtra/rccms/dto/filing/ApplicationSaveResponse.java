package com.maharashtra.rccms.dto.filing;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationSaveResponse {

    private long applicationId;
    private String applicationNo;
    private String clientApplicationRef;
    private String status;
    private Map<String, Long> applicantIdByClientRowKey = new LinkedHashMap<>();
    private Map<String, Long> respondentIdByClientRowKey = new LinkedHashMap<>();
    private String message;

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public String getClientApplicationRef() {
        return clientApplicationRef;
    }

    public void setClientApplicationRef(String clientApplicationRef) {
        this.clientApplicationRef = clientApplicationRef;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Long> getApplicantIdByClientRowKey() {
        return applicantIdByClientRowKey;
    }

    public void setApplicantIdByClientRowKey(Map<String, Long> applicantIdByClientRowKey) {
        this.applicantIdByClientRowKey = applicantIdByClientRowKey;
    }

    public Map<String, Long> getRespondentIdByClientRowKey() {
        return respondentIdByClientRowKey;
    }

    public void setRespondentIdByClientRowKey(Map<String, Long> respondentIdByClientRowKey) {
        this.respondentIdByClientRowKey = respondentIdByClientRowKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
