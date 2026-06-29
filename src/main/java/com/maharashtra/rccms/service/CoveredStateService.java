package com.maharashtra.rccms.service;

import com.maharashtra.rccms.model.master.State;
import com.maharashtra.rccms.repository.StateRepository;
import com.maharashtra.rccms.util.AdvocateRegistrationSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * RCCMS operates only in Maharashtra. Boundary dropdowns and validations use the
 * configured LGD state code (default {@code 27}).
 */
@Service
public class CoveredStateService {

    private final String coveredStateLgdCode;
    private final StateRepository stateRepository;

    public CoveredStateService(
            @Value("${rccms.boundary.covered-state-lgd-code:27}") String coveredStateLgdCode,
            StateRepository stateRepository
    ) {
        this.coveredStateLgdCode = coveredStateLgdCode.trim();
        this.stateRepository = stateRepository;
    }

    public String coveredStateLgdCode() {
        return coveredStateLgdCode;
    }

    public List<State> listStatesForDropdown() {
        return resolveCoveredState()
                .stream()
                .sorted(Comparator.comparing(State::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public State requireCoveredState() {
        return resolveCoveredState()
                .orElseThrow(() -> new IllegalStateException(
                        "Covered state with LGD code " + coveredStateLgdCode + " is not configured in master_state."
                ));
    }

    public State requireCoveredStateById(Long stateId) {
        State state = stateRepository.findById(stateId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid stateId"));
        if (!isCoveredStateEntity(state)) {
            throw new IllegalArgumentException(
                    "Only Maharashtra (LGD code " + coveredStateLgdCode + ") is supported."
            );
        }
        return state;
    }

    public void requireCoveredLgdCode(String rawLgdCode, String fieldLabel) {
        String code = AdvocateRegistrationSupport.trimToNull(rawLgdCode);
        if (code == null) {
            throw new IllegalArgumentException(fieldLabel + " LGD code is required.");
        }
        if (!coveredStateLgdCode.equals(code)) {
            throw new IllegalArgumentException(
                    fieldLabel + ": only Maharashtra (LGD code " + coveredStateLgdCode + ") is supported."
            );
        }
        if (stateRepository.findFirstByLgdCode(code).isEmpty()) {
            throw new IllegalArgumentException(
                    fieldLabel + ": invalid or missing LGD code. Select a value from GET /api/lookups/states."
            );
        }
    }

    public boolean isCoveredStateEntity(State state) {
        if (state == null) {
            return false;
        }
        String lgdCode = AdvocateRegistrationSupport.trimToNull(state.getLgdCode());
        if (coveredStateLgdCode.equals(lgdCode)) {
            return true;
        }
        Long coveredStateId = coveredStateIdOrNull();
        return coveredStateId != null && coveredStateId.equals(state.getId());
    }

    public boolean isCoveredStateId(Long stateId) {
        if (stateId == null) {
            return false;
        }
        return stateRepository.findById(stateId)
                .map(this::isCoveredStateEntity)
                .orElse(false);
    }

    public boolean matchesOptionalStateFilter(Long requestedStateId, State entityState) {
        if (requestedStateId != null && (entityState == null || !requestedStateId.equals(entityState.getId()))) {
            return false;
        }
        return isCoveredStateEntity(entityState);
    }

    public boolean matchesCoveredStateLgdCode(String stateLgdCode) {
        return stateLgdCode != null && coveredStateLgdCode.equals(stateLgdCode);
    }

    public void requireCoveredStateLgdOnCreate(String lgdCode) {
        String code = AdvocateRegistrationSupport.trimToNull(lgdCode);
        if (code == null) {
            throw new IllegalArgumentException("lgdCode is required");
        }
        if (!coveredStateLgdCode.equals(code)) {
            throw new IllegalArgumentException(
                    "Only Maharashtra (LGD code " + coveredStateLgdCode + ") can be configured."
            );
        }
    }

    public Long coveredStateIdOrNull() {
        return resolveCoveredState().map(State::getId).orElse(null);
    }

    private java.util.Optional<State> resolveCoveredState() {
        java.util.Optional<State> byLgd = stateRepository.findFirstByLgdCode(coveredStateLgdCode);
        if (byLgd.isPresent()) {
            return byLgd;
        }
        return stateRepository.findFirstByNameIgnoreCase("Maharashtra");
    }

    public boolean sameCoveredState(Long stateId, State other) {
        return stateId != null
                && other != null
                && Objects.equals(stateId, other.getId())
                && isCoveredStateEntity(other);
    }
}
