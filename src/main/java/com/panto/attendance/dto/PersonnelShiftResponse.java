package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelShiftResponse {
    private Long id;
    private Date startDate;
    private Date endDate;
    private Long personnelId;
    private String personnelFullName;
    private Time startTime;
    private Time endTime;
    private String shiftTypeName;
    private Long shiftTypeId;
}
