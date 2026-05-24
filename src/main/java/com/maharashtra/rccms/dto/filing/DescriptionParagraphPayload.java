package com.maharashtra.rccms.dto.filing;

public class DescriptionParagraphPayload {

    private Integer paraNo;
    private String text;
    private String textMr;

    public Integer getParaNo() {
        return paraNo;
    }

    public void setParaNo(Integer paraNo) {
        this.paraNo = paraNo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextMr() {
        return textMr;
    }

    public void setTextMr(String textMr) {
        this.textMr = textMr;
    }
}
