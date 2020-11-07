package com.panto.attendance.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceActionLeavePartialResponse {
    private Long id;
    private String description;
    private String leaveType;
    private Long leaveTypeId;
    private boolean isLeaveApproved;
}
