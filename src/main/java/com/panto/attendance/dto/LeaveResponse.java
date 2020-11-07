package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveResponse {
    private Long id;
    private Long personnelId;
    private String personnelFullName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private Date date;
    private boolean approved;
    private String description;
}
