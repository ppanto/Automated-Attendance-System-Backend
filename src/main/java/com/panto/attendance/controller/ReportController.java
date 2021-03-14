package com.panto.attendance.controller;

//import com.panto.attendance.helpers.ExcelForOnePersonnelReport;
//import com.panto.attendance.helpers.ExcelPerPersonnelReport;
import com.panto.attendance.service.AttendanceActionService;
import com.panto.attendance.service.AttendanceReportService;
//import com.panto.attendance.service.ExcelExportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
import java.sql.Date;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/report")
@AllArgsConstructor
public class ReportController {
    private final AttendanceActionService attendanceActionService;
    private final AttendanceReportService attendanceReportService;
    //private final ExcelExportService excelExportService;

    @GetMapping("daily")
    public ResponseEntity<?> getDaily(@RequestParam Date date){
        return ResponseEntity.ok().body(attendanceActionService.getDailyReport(date));
    }

    @GetMapping("time")
    public ResponseEntity<?> getTime(@RequestParam Date dateStart, @RequestParam Date dateEnd){
        return ResponseEntity.ok().body(attendanceReportService.getTimeReport(dateStart, dateEnd));
    }

    @GetMapping("chart")
    public ResponseEntity<?> getChart(@RequestParam Date dateStart, @RequestParam Date dateEnd){
        return ResponseEntity.ok().body(attendanceReportService.getChartReport(dateStart, dateEnd));
    }

//    @GetMapping("download/by-personnel")
//    public void downloadByPersonnel(@RequestParam Date dateStart, @RequestParam Date dateEnd, HttpServletResponse response) throws IOException {
//        response.setContentType("application/octet-stream");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new java.util.Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=full_report_" + currentDateTime + ".xlsx";
//        response.setHeader(headerKey, headerValue);
//
//        ExcelPerPersonnelReport excelExporter = excelExportService.getExcelPerPersonnel(dateStart, dateEnd);
//
//        excelExporter.export(response);
//    }

//    @GetMapping("download/personnel/by-date")
//    public void downloadPersonnelByDate(@RequestParam Long personnelId, @RequestParam Date dateStart, @RequestParam Date dateEnd, HttpServletResponse response) throws Exception {
//        response.setContentType("application/octet-stream");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new java.util.Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=personnel_report_" + currentDateTime + ".xlsx";
//        response.setHeader(headerKey, headerValue);
//
//        ExcelForOnePersonnelReport excelExporter = excelExportService.getExcelForOnePersonnel(personnelId, dateStart, dateEnd);
//
//        excelExporter.export(response);
//    }
}
