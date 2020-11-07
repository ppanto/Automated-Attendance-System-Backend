package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveInsertRequest {
    @NotNull
    private Long personnelId;
    @NotNull
    private Long leaveTypeId;
    @NotNull
    private Date startDate;
    private Date endDate;
    private boolean approved = true;
    private String description;
}
