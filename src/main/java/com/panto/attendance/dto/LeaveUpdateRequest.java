package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveUpdateRequest {
    @NotNull
    private Long id;
    @NotNull
    private Long leaveTypeId;
    @NotNull
    private Date startDate;
    private boolean approved = true;
    private String description;
}
