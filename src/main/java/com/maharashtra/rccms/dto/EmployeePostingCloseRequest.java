package com.maharashtra.rccms.dto;

import java.time.LocalDate;

public class EmployeePostingCloseRequest {
    private LocalDate toDate;

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}

