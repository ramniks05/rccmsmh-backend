package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.Village;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VillageRepository extends JpaRepository<Village, Long> {
    List<Village> findByTalukaIdOrderByNameAsc(Long talukaId);

    List<Village> findByTaluka_DistrictIdOrderByNameAsc(Long districtId);

    List<Village> findByTalukaLgdCodeOrderByNameAsc(String talukaLgdCode);
}
