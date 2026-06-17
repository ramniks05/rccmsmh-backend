package com.maharashtra.rccms.model.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Root of the administrative boundary chain (state → division → district → taluka → village).
 * Not linked to {@link Department}; department is a separate master.
 */
@Entity
@Table(name = "master_state")
public class State extends BoundaryNamedLgdBase {

    /** Whether this row is a State or Union Territory (e.g. "State", "UT"). */
    @Column(name = "state_or_ut", length = 16)
    private String stateOrUT;

    public String getStateOrUT() {
        return stateOrUT;
    }

    public void setStateOrUT(String stateOrUT) {
        this.stateOrUT = stateOrUT;
    }
}
