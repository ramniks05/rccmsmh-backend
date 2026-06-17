package com.maharashtra.rccms.model.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Revenue / administrative division under a state (parallel to district path where applicable).
 * Divisions do not have LGD codes.
 */
@Entity
@Table(name = "master_division")
public class Division extends BoundaryNamedBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "division_code", length = 64)
    private String divisionCode;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }
}
