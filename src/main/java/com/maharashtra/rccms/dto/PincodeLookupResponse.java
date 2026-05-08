package com.maharashtra.rccms.dto;

import java.util.List;

public class PincodeLookupResponse {
    private final String pincode;
    private final String status;
    private final String message;
    private final List<PincodePostOfficeOption> postOffices;
    private final List<String> talukas;
    private final List<String> districts;
    private final List<String> states;

    public PincodeLookupResponse(
            String pincode,
            String status,
            String message,
            List<PincodePostOfficeOption> postOffices,
            List<String> talukas,
            List<String> districts,
            List<String> states
    ) {
        this.pincode = pincode;
        this.status = status;
        this.message = message;
        this.postOffices = postOffices;
        this.talukas = talukas;
        this.districts = districts;
        this.states = states;
    }

    public String getPincode() {
        return pincode;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<PincodePostOfficeOption> getPostOffices() {
        return postOffices;
    }

    public List<String> getTalukas() {
        return talukas;
    }

    public List<String> getDistricts() {
        return districts;
    }

    public List<String> getStates() {
        return states;
    }
}
