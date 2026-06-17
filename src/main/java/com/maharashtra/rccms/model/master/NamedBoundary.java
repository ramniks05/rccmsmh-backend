package com.maharashtra.rccms.model.master;

/**
 * Common read/write surface for boundary rows identified by name (with optional local name).
 */
public interface NamedBoundary {

    Long getId();

    String getName();

    void setName(String name);

    String getLocalName();

    void setLocalName(String localName);
}
