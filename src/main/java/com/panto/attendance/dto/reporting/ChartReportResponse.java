package com.panto.attendance.dto.reporting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChartReportResponse {
    private Date fullDate;
    private boolean isHoliday;
    private boolean isWeekend;
    private Long totalTimeWorked = 0L; //milliseconds
    private Long totalTimeBreaks = 0L; //milliseconds
    private Long totalTimeOfficial = 0L; //milliseconds
    private int personnelAtWork = 0;
}
