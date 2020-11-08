package com.panto.attendance.service;

import com.panto.attendance.dto.reporting.TimeReportPerPersonnelResponse;
import com.panto.attendance.helpers.ExcelForOnePersonnelReport;
import com.panto.attendance.helpers.ExcelPerPersonnelReport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ExcelExportService {
    private AttendanceReportService attendanceReportService;

    public ExcelPerPersonnelReport getExcelPerPersonnel(Date dateStart, Date dateEnd){
        return new ExcelPerPersonnelReport(
                attendanceReportService.getTimeReport(dateStart, dateEnd)
        );
    }

    public ExcelForOnePersonnelReport getExcelForOnePersonnel(Long personnelId, Date dateStart, Date dateEnd) throws Exception {
        Optional<TimeReportPerPersonnelResponse> data = attendanceReportService.getTimeReport(dateStart, dateEnd)
                .getPersonnelTimesList().stream()
                .filter(d -> d.getPersonnelId() == personnelId)
                .findFirst();
        if(data.isEmpty()) throw new Exception("No personnel with id.");
        return new ExcelForOnePersonnelReport(data.get());
    }
}
