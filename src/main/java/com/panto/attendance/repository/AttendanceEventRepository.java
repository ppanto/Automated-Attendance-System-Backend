package com.panto.attendance.repository;

import com.panto.attendance.model.AttendanceEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceEventRepository extends JpaRepository<AttendanceEvent, Long> {
}
