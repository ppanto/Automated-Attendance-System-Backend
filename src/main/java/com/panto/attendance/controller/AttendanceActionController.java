package com.panto.attendance.controller;

import com.panto.attendance.service.AttendanceActionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

@RestController
@RequestMapping("/api/attendance-action")
@AllArgsConstructor
public class AttendanceActionController {
    private final AttendanceActionService attendanceActionService;

    @GetMapping
    public ResponseEntity<?> getByDate(@RequestParam Date date){
        return ResponseEntity.ok().body(attendanceActionService.getByDate(date));
    }

    @GetMapping("by-action")
    public ResponseEntity<?> getByDateRange(@RequestParam Date dateStart, @RequestParam Date dateEnd){
        return ResponseEntity.ok().body(attendanceActionService.getSimpleByDateRange(dateStart,dateEnd));
    }

}
