package com.panto.attendance.repository;

import com.panto.attendance.model.PersonnelShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonnelShiftRepository extends JpaRepository<PersonnelShift, Long> {
    List<PersonnelShift> findByPersonnelId(Long personnelId);
    void deleteByPersonnelId(Long personnelId);
}
