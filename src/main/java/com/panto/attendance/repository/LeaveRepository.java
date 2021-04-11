package com.panto.attendance.repository;

import com.panto.attendance.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByPersonnelId(Long personnelId);
    List<Leave> findByDate(Date date);
    List<Leave> findByPersonnelIdAndDate(Long personnelId, Date date);
    @Query(value = "SELECT * FROM leave as l WHERE l.date >= :startDate AND l.date <= :endDate", nativeQuery = true)
    List<Leave> findByDateRange(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

    void deleteByPersonnelId(Long id);
}
