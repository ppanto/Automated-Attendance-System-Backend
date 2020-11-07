package com.panto.attendance.repository;

import com.panto.attendance.model.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftTypeRepository extends JpaRepository<ShiftType, Long> {
}
