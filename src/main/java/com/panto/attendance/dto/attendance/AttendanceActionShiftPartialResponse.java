package com.panto.attendance.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceActionShiftPartialResponse {
    private Time shiftStartTime;
    private Time shiftEndTime;
    private String shiftName;
}
