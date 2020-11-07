package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelShiftUpsertRequest {
    private Long id;
    private Long shiftTypeId;
    private Long personnelId;
    private Date startDate;
    private Date endDate;
}
