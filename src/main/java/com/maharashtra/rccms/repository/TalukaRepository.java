package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.Taluka;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalukaRepository extends JpaRepository<Taluka, Long> {
    List<Taluka> findByDistrictIdOrderByNameAsc(Long districtId);
    List<Taluka> findByDistrictIdAndSubdistrictIdOrderByNameAsc(Long districtId, Long subdistrictId);
}

