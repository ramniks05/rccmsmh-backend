package com.maharashtra.rccms.dto;

public class PincodePostOfficeOption {
    private final String name;
    private final String block;
    private final String district;
    private final String state;
    private final String value;

    public PincodePostOfficeOption(String name, String block, String district, String state, String value) {
        this.name = name;
        this.block = block;
        this.district = district;
        this.state = state;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getBlock() {
        return block;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }

    public String getValue() {
        return value;
    }
}
