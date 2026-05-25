package com.automation.utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public final class ExcelUtils {

    private ExcelUtils() { /* no instantiation */ }

    /**
     * Read a sheet and return list of row-maps.
     *
     * @param file the .xlsx file (absolute or relative)
     * @param sheetName sheet to read
     * @return list of rows as Map<header, String>
     * @throws Exception on IO or parse errors
     */
    public static List<Map<String, String>> readSheet(File file, String sheetName) throws Exception {
        if (file == null) {
            throw new IllegalArgumentException("Excel file parameter is null");
        }
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Excel file not found: " + file.getAbsolutePath());
        }
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Get the sheet by name
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }

            // Iterate through rows, first row is header
            Iterator<Row> rowIter = sheet.iterator();
            if (!rowIter.hasNext()) {
                return Collections.emptyList();
            }

            // Specify Read header row
            Row headerRow = rowIter.next();
            List<String> headers = new ArrayList<>();
            for (Cell c : headerRow) {
                headers.add(cellToString(c));
            }

            // Map the rows
            List<Map<String, String>> rows = new ArrayList<>();
            while (rowIter.hasNext()) {
                Row r = rowIter.next();
                if (isRowEmpty(r)) continue;
                Map<String, String> map = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = r.getCell(i);
                    String val = cell == null ? "" : cellToString(cell);
                    map.put(headers.get(i), val);
                }
                rows.add(map);
            }
            return rows;
        }
    }

    // Checks if row is empty (all cells are blank or null)
    private static boolean isRowEmpty(Row r) {
        if (r == null) return true;
        for (Cell c : r) {
            if (c != null && c.getCellType() != CellType.BLANK && !cellToString(c).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // Convert the cell values to strings
    private static String cellToString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case BOOLEAN: return Boolean.toString(cell.getBooleanCellValue());
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // remove .0 for integer-like numbers
                    double d = cell.getNumericCellValue();
                    if (d == (long) d) return Long.toString((long) d);
                    return Double.toString(d);
                }
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            case _NONE:
            case ERROR:
            default:
                return "";
        }
    }
}