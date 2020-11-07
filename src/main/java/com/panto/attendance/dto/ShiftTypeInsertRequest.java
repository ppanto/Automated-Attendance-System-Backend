package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftTypeInsertRequest {
    @NotBlank
    private String name;
    @NotNull
    private Timestamp startTime;
    @NotNull
    private Timestamp endTime;
}
