package com.panto.attendance.repository;

import com.panto.attendance.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByPersonnelId(Long personnelId);
    List<Leave> findByDate(Date date);
    List<Leave> findByPersonnelIdAndDate(Long personnelId, Date date);
}
