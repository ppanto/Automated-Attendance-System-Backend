package com.panto.attendance.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceActionDateResponse {
    private boolean isHoliday = false;
    private boolean isWeekend;
    private Date date;
    private List<AttendanceActionDatePersonnelResponse> attendanceActionDatePersonnelResponses;
}
