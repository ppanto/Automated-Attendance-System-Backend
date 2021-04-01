package com.panto.attendance.controller;

import com.panto.attendance.service.AttendanceActionService;
import com.panto.attendance.service.AttendanceReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

@RestController
@RequestMapping("/api/report")
@AllArgsConstructor
public class ReportController {
    private final AttendanceActionService attendanceActionService;
    private final AttendanceReportService attendanceReportService;

    @GetMapping("daily")
    public ResponseEntity<?> getDaily(@RequestParam Date date){
        return ResponseEntity.ok().body(attendanceActionService.getDailyReport(date));
    }

    @GetMapping("time")
    public ResponseEntity<?> getTime(@RequestParam Date dateStart, @RequestParam Date dateEnd){
        return ResponseEntity.ok().body(attendanceReportService.getTimeReport(dateStart, dateEnd));
    }

    @GetMapping("chart")
    public ResponseEntity<?> getChart(@RequestParam Date dateStart, @RequestParam Date dateEnd){
        return ResponseEntity.ok().body(attendanceReportService.getChartReport(dateStart, dateEnd));
    }
}
