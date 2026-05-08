package com.maharashtra.rccms.model.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "master_occupation")
public class Occupation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "local_name", length = 512)
    private String localName;

    @Column(name = "short_name", length = 64)
    private String shortName;

    @Column(name = "short_name_local", length = 128)
    private String shortNameLocal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortNameLocal() {
        return shortNameLocal;
    }

    public void setShortNameLocal(String shortNameLocal) {
        this.shortNameLocal = shortNameLocal;
    }
}
