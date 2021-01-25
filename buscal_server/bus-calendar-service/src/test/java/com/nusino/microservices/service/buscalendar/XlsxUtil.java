/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;


public class XlsxUtil {

    public static void writeAt(Row row, int colNum, Object value, int rowSpan, int colSpan) {
        if (value != null) {
            writeAt(row, colNum, value);
            if (rowSpan > 1 || colSpan > 1) {
                CellRangeAddress merged = new CellRangeAddress(row.getRowNum(), (row.getRowNum() + rowSpan - 1), colNum, (colNum + colSpan - 1));
                row.getSheet().addMergedRegion(merged);
                setBordersToMergedCells(row.getSheet(), merged);
            }
        }
    }

    public static void writeAt(Row row, int colNum, Object value) {
        if (value != null) {
            if (value instanceof String) {
                writeAt(row, colNum, (String) value);

            } else if (value instanceof Long) {
                writeAt(row, colNum, (Long) value);

            } else if (value instanceof Integer) {
                writeAt(row, colNum, (Integer) value);

            } else if (value instanceof Boolean) {
                writeAt(row, colNum, (Boolean) value);

            } else {
                writeAt(row, colNum, value.toString());
            }
        }
    }

    public static void writeAt(Row row, int colNum, String value) {
        if (value != null) {
            Cell cell = row.createCell(colNum);
            cell.setCellValue(value);
        }
    }

    public static void writeAt(Row row, int colNum, Long value) {
        if (value != null) {
            Cell cell = row.createCell(colNum);
            cell.setCellValue(value);
        }
    }

    public static void writeAt(Row row, int colNum, Integer value) {
        if (value != null) {
            Cell cell = row.createCell(colNum);
            cell.setCellValue(value);
        }
    }

    public static void writeAt(Row row, int colNum, String value, Font font) {
        if (value != null) {
            Cell cell = row.createCell(colNum);
            XSSFRichTextString myRichTextString = new XSSFRichTextString(value);
            myRichTextString.applyFont(font);
            cell.setCellValue(myRichTextString);
        }
    }

    public static void writeAt(Row row, int colNum, Boolean value) {
        if (value != null) {
            Cell cell = row.createCell(colNum);
            cell.setCellValue(value);
        }

    }


    public static void writeAt(Row row, int colNum, RichText... richTexts) {
        if (richTexts != null && richTexts.length > 0) {
            Cell cell = row.createCell(colNum);
            String text = "";
            for (RichText richText : richTexts) {
                text += richText.getContent();
            }

            XSSFRichTextString myRichTextString = new XSSFRichTextString(text);
            int start = 0;
            for (RichText richText : richTexts) {
                if (richText.getContent() != null) {
                    myRichTextString.applyFont(start, start + richText.getContent().length(), richText.font);
                    start = start + richText.getContent().length();
                }
            }

            cell.setCellValue(myRichTextString);
        }
    }

    public static void writeAt(Row row, int colNum, RichText richText) {
        if (richText != null) {
            Cell cell = row.createCell(colNum);
            if (richText.content != null) {
                XSSFRichTextString myRichTextString = new XSSFRichTextString(richText.content);
                myRichTextString.applyFont(richText.font);
                cell.setCellValue(myRichTextString);
            }
        }
    }

    protected static void setBordersToMergedCells(Sheet sheet, CellRangeAddress rangeAddress) {
        RegionUtil.setBorderTop(BorderStyle.THIN, rangeAddress, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, rangeAddress, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, rangeAddress, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, rangeAddress, sheet);
    }

    public static Object readAt(Row row, int colNum) {
        Cell cell = row.getCell(colNum);
        if (cell != null) {
            CellType cellType = cell.getCellType();
            if (cellType == CellType.STRING) {
                return cell.getStringCellValue();
            } else if (cellType == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cellType == CellType.BOOLEAN) {
                return cell.getBooleanCellValue();
            }
        }
        return null;
    }

    public enum FontStyle {
        BOLD, ITALIC, NORMAL
    }

    public enum FontDecoration {
        Strikethrough, Underline
    }

    public static class RichText {
        private String content;
        private final Font font;

        public RichText(String content, Font font) {
            this.content = content;
            this.font = font;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
