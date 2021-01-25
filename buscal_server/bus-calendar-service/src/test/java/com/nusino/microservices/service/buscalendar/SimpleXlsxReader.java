/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;


import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SimpleXlsxReader {
    private Workbook workbook;
    private Sheet currentSheet;
    private Map<CellRangeAddress, Object> regionsMap;

    public SimpleXlsxReader() {
    }

    public SimpleXlsxReader(Workbook workbook) {
        super();
        this.workbook = workbook;
    }

    public SimpleXlsxReader(InputStream in) throws IOException {
        this.workbook = new XSSFWorkbook(in);

    }

    public SimpleXlsxReader(Workbook workbook, Sheet currentSheet) {
        super();
        this.workbook = workbook;
        this.currentSheet = currentSheet;
        findMerged();
    }

    public Workbook importWorkbook(InputStream in) throws IOException {
        workbook = new XSSFWorkbook(in);
        CellStyle wrapCellStyle = workbook.createCellStyle();
        wrapCellStyle.setWrapText(true);
        return workbook;
    }

    public static Workbook newWorkbook() {
        return new XSSFWorkbook();
    }

    public Sheet requestSheet(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }
        if (sheet == null) {
            sheet = workbook.getSheetAt(0);
        }
        currentSheet = sheet;
        findMerged();
        return currentSheet;
    }

    public Pair<Integer, Map<Integer, Object>> readRow(int rowNum) {
        Row row = currentSheet.getRow(rowNum);
        if (row == null) {
            return null;
        }
        Map<Integer, Object> map = new LinkedHashMap<>();
        int maxCol = row.getPhysicalNumberOfCells() + 50;
        int pickMax = 0;
        for (int i = 0; i < maxCol; i++) {
            Object value = XlsxUtil.readAt(row, i);
            if (value != null) {
                map.put(i, value);
                pickMax = Math.max(pickMax, i);
            }
        }
        return Pair.of(pickMax, map);

    }

    public Map<Integer, Object> readRow(int rowNum, int maxColCount) {
        Row row = currentSheet.getRow(rowNum);
        if (row == null) {
            return null;
        }
        Map<Integer, Object> map = new LinkedHashMap<>();
        maxColCount = Math.max(row.getPhysicalNumberOfCells(), maxColCount);
        for (int i = 0; i < maxColCount; i++) {
            Object value = XlsxUtil.readAt(row, i);
            map.put(i, value);
        }
        return map;
    }


    public List<Map<Integer, Object>> readAllRows(int startRowNum, int maxColCount) {
        List<Map<Integer, Object>> all = new ArrayList<>();
        //
        for (int i = startRowNum; i < currentSheet.getPhysicalNumberOfRows(); i++) {
            Row row = currentSheet.getRow(i);
            if (row == null) {
                break;
            }
            boolean hasData = false;
            Map<Integer, Object> map = new LinkedHashMap<>();
            int colCount = maxColCount;
            for (int j = 0; j <= colCount; j++) {
                Object value = XlsxUtil.readAt(row, j);
                map.put(j, value);
                if (value != null && !value.toString().trim().isEmpty()) {
                    hasData = true;
                }
            }
            if (hasData == true) {
                all.add(map);
            }
        }
        return all;
    }

    public void findMerged() {
        regionsMap = new HashMap<>();
        for (int i = 0; i < currentSheet.getNumMergedRegions(); i++) {
            regionsMap.put(currentSheet.getMergedRegion(i), readValue(currentSheet.getMergedRegion(i)));
        }

    }

    public CellRangeAddress findMergedCell(Cell cell) {
        if (regionsMap == null) {
            return null;
        }
        for (CellRangeAddress temp : regionsMap.keySet()) {
            if (temp.isInRange(cell)) {
                return temp;
            }
        }
        return null;
    }

    public Object readValue(CellRangeAddress mergedCell) {
        int r0 = mergedCell.getFirstRow();
        int r1 = mergedCell.getLastRow();
        int c0 = mergedCell.getFirstColumn();
        int c1 = mergedCell.getLastColumn();
        Object value = null;
        outer:
        for (int r = r0; r <= r1; r++) {
            for (int c = c0; c <= c1; c++) {
                Object temp = XlsxUtil.readAt(currentSheet.getRow(r), c);
                if (temp != null) {
                    temp = temp.toString().trim();
                    if (value == null) {
                        value = temp;
                    }
                    if (!temp.toString().isEmpty()) {
                        value = temp;
                        break outer;
                    }
                }
            }

        }
        return value;
    }
}
