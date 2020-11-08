package com.panto.attendance.helpers;

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

public class ExcelPerPersonnelReport {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private TimeReportResponse data;

    public ExcelPerPersonnelReport(TimeReportResponse data) {
        this.data = data;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Full Report");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "User ID", style);
        createCell(row, 1, "Full Name", style);
        createCell(row, 2, "Total Time Worked (Hours)", style);
        createCell(row, 3, "Total Days Worked", style);
        createCell(row, 4, "Total Absences", style);
        createCell(row, 5, "Total Time At Break (Hours)", style);
        createCell(row, 6, "Total Time At Official Absence (Hours)", style);
        createCell(row, 7, "Total Regular Time Worked (Hours)", style);
        createCell(row, 8, "Total Weekend Time Worked (Hours)", style);
        createCell(row, 9, "Total Holiday Time Worked (Hours)", style);
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

        for (TimeReportPerPersonnelResponse personnel : data.getPersonnelTimesList()) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, personnel.getPersonnelId(), style);
            createCell(row, columnCount++, personnel.getPersonnelFullName(), style);
            createCell(row, columnCount++, ((personnel.getTotalTimeWorked() / (Double)((double)1000)) / 60) / 60, style);
            createCell(row, columnCount++, personnel.getTotalDaysWorked(), style);
            createCell(row, columnCount++, personnel.getTotalLeaves(), style);
            createCell(row, columnCount++, ((personnel.getTotalTimeBreaks() / (Double)((double)1000)) / 60) / 60, style);
            createCell(row, columnCount++, ((personnel.getTotalTimeOfficial() / (Double)((double)1000)) / 60) / 60, style);
            createCell(row, columnCount++, ((personnel.getTotalRegularTimeWorked() / (Double)((double)1000)) / 60) / 60, style);
            createCell(row, columnCount++, ((personnel.getTotalWeekendTimeWorked() / (Double)((double)1000)) / 60) / 60, style);
            createCell(row, columnCount++, ((personnel.getTotalHolidayTimeWorked() / (Double)((double)1000)) / 60) / 60, style);
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
