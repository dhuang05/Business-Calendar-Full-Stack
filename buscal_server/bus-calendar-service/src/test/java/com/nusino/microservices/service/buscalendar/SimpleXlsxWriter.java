/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.BiFunction;

public class SimpleXlsxWriter {
    private Workbook workbook;
    private Sheet currentSheet;

    public SimpleXlsxWriter() {

    }

    public SimpleXlsxWriter(Workbook workbook) {
        super();
        this.workbook = workbook;
    }

    public SimpleXlsxWriter(Workbook workbook, Sheet currentSheet) {
        super();
        this.workbook = workbook;
        this.currentSheet = currentSheet;
    }


    public void resetSheet(Sheet currentSheet) {
        this.currentSheet = currentSheet;
    }

    public Workbook createWorkbook() {
        workbook = new XSSFWorkbook();
        CellStyle wrapCellStyle = workbook.createCellStyle();
        wrapCellStyle.setWrapText(true);
        return workbook;
    }

    public static Workbook newWorkbook() {
        return new XSSFWorkbook();
    }

    public Sheet createSheet() {
        currentSheet = workbook.createSheet();
        return currentSheet;
    }

    public Sheet requestSheet(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }
        currentSheet = sheet;
        return currentSheet;
    }


    public void writeCell(int rowNum, int colNum, Object value) {
        if (workbook == null) {
            createWorkbook();
        }
        if (currentSheet == null) {
            createSheet();
        }
        Row row = currentSheet.getRow(rowNum);
        if (row == null) {
            row = currentSheet.createRow(rowNum);
        }
        if (value instanceof XlsxUtil.RichText) {
            XlsxUtil.writeAt(row, colNum, (XlsxUtil.RichText) value);
        } else {
            XlsxUtil.writeAt(row, colNum, value);
        }
    }


    public <T> Integer writeRow(int rowNum, T t, BiFunction<Row, T, Void> rowWriter) {
        if (workbook == null) {
            createWorkbook();
        }
        if (currentSheet == null) {
            createSheet();
        }
        Row row = currentSheet.getRow(rowNum);
        if (row == null) {
            row = currentSheet.createRow(rowNum);
        }
        rowWriter.apply(row, t);
        //write one row
        return 1;
    }

    public void writeCell(int rowNum, int colNum, Object value, int rowSpan, int colSpan) {
        if (workbook == null) {
            createWorkbook();
        }
        if (currentSheet == null) {
            createSheet();
        }
        Row row = currentSheet.getRow(rowNum);
        if (row == null) {
            row = currentSheet.createRow(rowNum);
        }
        XlsxUtil.writeAt(row, colNum, value, rowSpan, colSpan);
    }

    public void writeCell(int rowNum, int colNum, XlsxUtil.RichText... richTexts) {
        if (workbook == null) {
            createWorkbook();
        }
        if (currentSheet == null) {
            createSheet();
        }
        Row row = currentSheet.getRow(rowNum);
        if (row == null) {
            row = currentSheet.createRow(rowNum);
        }
        XlsxUtil.writeAt(row, colNum, richTexts);
    }


    public Font createFont(Short color, XlsxUtil.FontStyle fontStyle, XlsxUtil.FontDecoration fontDecoration) {
        if (workbook == null) {
            createWorkbook();
        }
        Font font = workbook.createFont();
        if (color != null) {
            font.setColor(color);
        }
        if (fontStyle != null) {
            if (fontStyle == XlsxUtil.FontStyle.BOLD) {
                font.setBold(true);
            } else if (fontStyle == XlsxUtil.FontStyle.ITALIC) {
                font.setItalic(true);
            }
        }

        if (fontDecoration != null) {
            if (fontDecoration == XlsxUtil.FontDecoration.Strikethrough) {
                font.setStrikeout(true);
            } else if (fontDecoration == XlsxUtil.FontDecoration.Underline) {
                //font.setUnderline(true);
            }
        }
        return font;
    }


    public void autoSizeColumn(int column) {
        currentSheet.autoSizeColumn(column);
    }

    public int getLastRowNum() {
        return currentSheet.getLastRowNum();
    }

    public void write(OutputStream out) throws IOException {
        workbook.write(out);
        out.flush();
    }
}
