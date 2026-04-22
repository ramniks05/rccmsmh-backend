package com.maharashtra.rccms.model.master;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Root of the administrative boundary chain (state → division → district → taluka → village).
 * Not linked to {@link Department}; department is a separate master.
 */
@Entity
@Table(name = "master_state")
public class State extends BoundaryNamedLgdBase {
}
