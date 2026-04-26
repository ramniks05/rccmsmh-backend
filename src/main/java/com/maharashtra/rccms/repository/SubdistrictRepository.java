package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.Subdistrict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubdistrictRepository extends JpaRepository<Subdistrict, Long> {
    List<Subdistrict> findByDistrictIdOrderByNameAsc(Long districtId);
}

