package com.panto.attendance.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceActionDatePersonnelResponse {
    private Long personnelId;
    private String personnelFullName;
    private List<AttendanceActionSingularResponse> attendanceActionSingularResponseList;
    private AttendanceActionLeavePartialResponse attendanceActionLeavePartialResponse;
    private AttendanceActionShiftPartialResponse attendanceActionShiftPartialResponse;
    private boolean isStartTimeRegular = true;
    private boolean isEndTimeRegular = true;
}
