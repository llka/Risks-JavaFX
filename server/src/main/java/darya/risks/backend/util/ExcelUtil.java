package darya.risks.backend.util;

import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.Contact;
import darya.risks.entity.Project;
import darya.risks.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExcelUtil {
    private static final Logger logger = LogManager.getLogger(ExcelUtil.class);
    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd";
    private static String[] columns = {"Contact First Name", "Contact LastName", "Contact Email",
            "Project ID", "Project Title", "Project Start Date", "Project End Date",
            "Employer ID", "Employer First Name", "Employer Last Name", "Employer Company Name"};


    public static void generateExcelProjectsReport(List<Contact> contacts, String sheetTitle) throws ApplicationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        int rowIndex = 0;
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet(sheetTitle);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.DARK_RED.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // header
            Row headerRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            for (Contact contact : contacts) {
                for (Project project : contact.getProjects()) {
                    int cellIndex = 0;
                    Row row = sheet.createRow(rowIndex++);

                    row.createCell(cellIndex++).setCellValue(contact.getFirstName());
                    row.createCell(cellIndex++).setCellValue(contact.getLastName());
                    row.createCell(cellIndex++).setCellValue(contact.getEmail());

                    row.createCell(cellIndex++).setCellValue(project.getId());
                    row.createCell(cellIndex++).setCellValue(project.getTitle());
                    row.createCell(cellIndex++).setCellValue(dateFormat.format(project.getStartDate()));
                    row.createCell(cellIndex++).setCellValue(dateFormat.format(project.getEndDate()));

                    row.createCell(cellIndex++).setCellValue(project.getEmployer().getId());
                    row.createCell(cellIndex++).setCellValue(project.getEmployer().getFirstName());
                    row.createCell(cellIndex++).setCellValue(project.getEmployer().getLastName());
                    row.createCell(cellIndex++).setCellValue(project.getEmployer().getCompanyName());
                }
            }
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream("projects_report.xlsx");
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();
        } catch (IOException e) {
            throw new ApplicationException("Cannot generate excel report!", ResponseStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
