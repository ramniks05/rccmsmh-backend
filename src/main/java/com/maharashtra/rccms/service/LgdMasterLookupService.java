package com.maharashtra.rccms.service;

import com.maharashtra.rccms.model.master.District;
import com.maharashtra.rccms.model.master.State;
import com.maharashtra.rccms.repository.DistrictRepository;
import com.maharashtra.rccms.repository.StateRepository;
import com.maharashtra.rccms.util.AdvocateRegistrationSupport;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LgdMasterLookupService {

    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;

    public LgdMasterLookupService(StateRepository stateRepository, DistrictRepository districtRepository) {
        this.stateRepository = stateRepository;
        this.districtRepository = districtRepository;
    }

    public State requireStateByLgdCode(String rawLgdCode, String fieldLabel) {
        String code = normalizeLgdCode(rawLgdCode, fieldLabel);
        return stateRepository.findFirstByLgdCode(code)
                .orElseThrow(() -> new IllegalArgumentException(
                        fieldLabel + ": invalid or missing LGD code. Select a value from GET /api/lookups/states."
                ));
    }

    public District requireDistrictByLgdCode(String rawDistrictLgd, String rawStateLgd, String fieldLabel) {
        String districtCode = normalizeLgdCode(rawDistrictLgd, fieldLabel);
        String stateCode = normalizeLgdCode(rawStateLgd, "placeOfPracticeState");
        return districtRepository.findFirstByLgdCodeAndState_LgdCode(districtCode, stateCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        fieldLabel + ": invalid LGD code or district does not belong to placeOfPracticeState. "
                                + "Use GET /api/lookups/districts?stateId=... and send each row's lgdCode."
                ));
    }

    public Optional<State> findStateByLgdCode(String lgdCode) {
        String code = AdvocateRegistrationSupport.trimToNull(lgdCode);
        if (code == null) {
            return Optional.empty();
        }
        return stateRepository.findFirstByLgdCode(code);
    }

    public Optional<District> findDistrictByLgdCode(String lgdCode) {
        String code = AdvocateRegistrationSupport.trimToNull(lgdCode);
        if (code == null) {
            return Optional.empty();
        }
        return districtRepository.findFirstByLgdCode(code);
    }

    private static String normalizeLgdCode(String raw, String fieldLabel) {
        String code = AdvocateRegistrationSupport.trimToNull(raw);
        if (code == null) {
            throw new IllegalArgumentException(fieldLabel + " LGD code is required.");
        }
        return code;
    }
}
