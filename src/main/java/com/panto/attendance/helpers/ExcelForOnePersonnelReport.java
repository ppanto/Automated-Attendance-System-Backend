package com.panto.attendance.helpers;

import com.panto.attendance.dto.reporting.TimeReportPerPersonnelForDateResponse;
import com.panto.attendance.dto.reporting.TimeReportPerPersonnelResponse;
import com.panto.attendance.dto.reporting.TimeReportResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExcelForOnePersonnelReport {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private TimeReportPerPersonnelResponse data;

    public ExcelForOnePersonnelReport(TimeReportPerPersonnelResponse data) {
        this.data = data;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Personnel (" + data.getPersonnelFullName() + ") Report");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Date", style);
        createCell(row, 1, "Total Time Worked (Hours)", style);
        createCell(row, 2, "Total Time At Break (Hours)", style);
        createCell(row, 3, "Total Time At Official Absence (Hours)", style);
        createCell(row, 4, "Total Regular Time Worked (Hours)", style);
        createCell(row, 5, "Total Weekend Time Worked (Hours)", style);
        createCell(row, 6, "Total Holiday Time Worked (Hours)", style);
        createCell(row, 7, "Is Day Holiday", style);
        createCell(row, 8, "Is Day Weekend", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
        else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (TimeReportPerPersonnelForDateResponse date : data.getTimeReportPerPersonnelForDateResponseList()) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, date.getFullDate().toString(), style);
            createCell(row, columnCount++, ((date.getTotalTimeWorked() / (Double)((double)1000)) / 60) / 60, style);
            createCell(row, columnCount++, ((date.getTotalTimeBreaks() / (Double)((double)1000)) / 60) / 60, style);
            createCell(row, columnCount++, ((date.getTotalTimeOfficial() / (Double)((double)1000)) / 60) / 60, style);
            createCell(row, columnCount++, ((date.getTotalRegularTimeWorked() / (Double)((double)1000)) / 60) / 60, style);
            createCell(row, columnCount++, ((date.getTotalWeekendTimeWorked() / (Double)((double)1000)) / 60) / 60, style);
            createCell(row, columnCount++, ((date.getTotalHolidayTimeWorked() / (Double)((double)1000)) / 60) / 60, style);
            createCell(row, columnCount++, date.isHoliday(), style);
            createCell(row, columnCount++, date.isWeekend(), style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
