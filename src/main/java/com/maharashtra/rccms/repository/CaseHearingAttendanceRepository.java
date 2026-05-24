package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.caseflow.CaseHearingAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CaseHearingAttendanceRepository extends JpaRepository<CaseHearingAttendance, Long> {

    List<CaseHearingAttendance> findByCaseHearingIdOrderByPartyTypeAscLineNoAscIdAsc(Long hearingId);

    Optional<CaseHearingAttendance> findByCaseHearingIdAndPartySlotKey(Long hearingId, String partySlotKey);

    void deleteByCaseHearingIdAndPartyType(Long hearingId, com.maharashtra.rccms.model.caseflow.HearingPartyType partyType);
}
