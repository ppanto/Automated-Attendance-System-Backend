package com.panto.attendance.repository;

import com.panto.attendance.model.AttendanceAction;
import com.panto.attendance.model.MyDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface MyDateRepository extends JpaRepository<MyDate, Date> {
    List<MyDate> findByIsHoliday(boolean isHoliday);
    MyDate findByFullDate(Date date);
    @Query(value = "SELECT * FROM my_date as d WHERE d.full_date >= :startDate AND d.full_date <= :endDate ORDER BY full_date DESC", nativeQuery = true)
    List<MyDate> findDatesInRange(@Param("startDate")Date startDate, @Param("endDate")Date endDate);
}
