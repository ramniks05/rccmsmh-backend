package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDescriptionPayload {

    private List<String> paragraphs = new ArrayList<>();
    private String affidavitText;
    private String prayerText;

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<String> paragraphs) {
        this.paragraphs = paragraphs != null ? paragraphs : new ArrayList<>();
    }

    public String getAffidavitText() {
        return affidavitText;
    }

    public void setAffidavitText(String affidavitText) {
        this.affidavitText = affidavitText;
    }

    public String getPrayerText() {
        return prayerText;
    }

    public void setPrayerText(String prayerText) {
        this.prayerText = prayerText;
    }
}
