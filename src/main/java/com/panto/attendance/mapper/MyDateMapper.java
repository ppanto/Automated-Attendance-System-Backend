package com.panto.attendance.mapper;

import com.panto.attendance.dto.MyDateSimpleResponse;
import com.panto.attendance.dto.reporting.ChartReportResponse;
import com.panto.attendance.model.MyDate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MyDateMapper {
    MyDateSimpleResponse mapToSimpleResponse(MyDate myDate);
    ChartReportResponse mapDateToChartResponse(MyDate myDate);
}
