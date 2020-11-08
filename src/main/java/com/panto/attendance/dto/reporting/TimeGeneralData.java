package com.panto.attendance.dto.reporting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeGeneralData {
    private Long totalTimeWorked = 0L; //milliseconds
    private Long totalTimeBreaks = 0L; //milliseconds
    private Long totalTimeOfficial = 0L; //milliseconds
    private Long totalRegularTimeWorked = 0L; //milliseconds
    private Long totalWeekendTimeWorked = 0L; //milliseconds
    private Long totalHolidayTimeWorked = 0L; //milliseconds
}
