package com.panto.attendance.repository;

import com.panto.attendance.model.AttendanceAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface AttendanceActionRepository extends JpaRepository<AttendanceAction, Long> {
    @Query(value = "SELECT * FROM attendance_action as aa WHERE aa.event_date = :date ORDER BY aa.date_time ASC", nativeQuery = true)
    List<AttendanceAction> findByDateOrdered(@Param("date")Date date);

    List<AttendanceAction> findByDate(Date date);
    @Query(value = "SELECT * FROM attendance_action as aa WHERE aa.event_date >= :startDate AND aa.event_date <= :endDate", nativeQuery = true)
    List<AttendanceAction> findByBetweenDates(@Param("startDate")Date startDate, @Param("endDate")Date endDate);
    @Query(value = "SELECT * FROM attendance_action as aa WHERE aa.event_date >= :startDate AND aa.event_date <= :endDate ORDER BY date_time DESC", nativeQuery = true)
    List<AttendanceAction> findByBetweenDatesOrdered(@Param("startDate")Date startDate, @Param("endDate")Date endDate);
    @Query(value="SELECT * FROM attendance_action as aa WHERE personnel_id = :personnelId ORDER BY date_time DESC LIMIT :lastN OFFSET :offset",nativeQuery = true)
    List<AttendanceAction> findLastNByPersonnel(@Param("lastN") int lastN, @Param("offset") int offset, @Param("personnelId") Long personnelId);

    void deleteByPersonnelId(Long id);
}
