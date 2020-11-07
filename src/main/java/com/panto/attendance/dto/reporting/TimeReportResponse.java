package com.panto.attendance.dto.reporting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeReportResponse {
    private Long totalTimeWorked = 0L; //milliseconds
    private Long totalTimeBreaks = 0L; //milliseconds
    private Long totalTimeOfficial = 0L; //milliseconds
    private Long totalRegularTimeWorked = 0L; //milliseconds
    private Long totalWeekendTimeWorked = 0L; //milliseconds
    private Long totalHolidayTimeWorked = 0L; //milliseconds
    private int totalLeaves = 0; //leaves instances
    private int totalDaysWorked = 0; //days
    private List<TimeReportPerPersonnelResponse> personnelTimesList;
}
