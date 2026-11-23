package com.example.is009.thongke.service;

import com.example.is009.common.ThoiGianVN;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExcelWorkbookStyler {

    private static final String FONT_FAMILY = "Times New Roman";

    public final XSSFWorkbook wb;
    public final XSSFCellStyle companyTitleStyle;
    public final XSSFCellStyle companySubStyle;
    public final XSSFCellStyle mottoTitleStyle;
    public final XSSFCellStyle mottoSubStyle;
    public final XSSFCellStyle reportTitleStyle;
    public final XSSFCellStyle reportSubtitleStyle;
    public final XSSFCellStyle tableHeaderStyle;
    public final XSSFCellStyle textStyle;
    public final XSSFCellStyle textAltStyle;
    public final XSSFCellStyle centerStyle;
    public final XSSFCellStyle centerAltStyle;
    public final XSSFCellStyle intStyle;
    public final XSSFCellStyle intAltStyle;
    public final XSSFCellStyle currencyStyle;
    public final XSSFCellStyle currencyAltStyle;
    public final XSSFCellStyle decimalStyle;
    public final XSSFCellStyle decimalAltStyle;
    public final XSSFCellStyle totalLabelStyle;
    public final XSSFCellStyle totalIntStyle;
    public final XSSFCellStyle totalCurrencyStyle;
    public final XSSFCellStyle totalDecimalStyle;
    public final XSSFCellStyle signTitleStyle;
    public final XSSFCellStyle signNoteStyle;

    public ExcelWorkbookStyler(XSSFWorkbook wb) {
        this.wb = wb;
        DataFormat fmt = wb.createDataFormat();
        XSSFColor navyBg = rgb(31, 78, 120);
        XSSFColor altRowBg = rgb(244, 248, 252);
        XSSFColor totalBg = rgb(255, 242, 204);
        XSSFColor borderGray = rgb(166, 166, 166);

        companyTitleStyle = createBaseStyle(font(11, true, false, rgb(31, 78, 120)), HorizontalAlignment.LEFT, null, null);
        companySubStyle = createBaseStyle(font(10, false, true, null), HorizontalAlignment.LEFT, null, null);
        mottoTitleStyle = createBaseStyle(font(11, true, false, null), HorizontalAlignment.CENTER, null, null);
        mottoSubStyle = createBaseStyle(font(11, true, true, null), HorizontalAlignment.CENTER, null, null);
        reportTitleStyle = createBaseStyle(font(15, true, false, rgb(11, 94, 215)), HorizontalAlignment.CENTER, null, null);
        reportSubtitleStyle = createBaseStyle(font(11, false, true, null), HorizontalAlignment.CENTER, null, null);

        tableHeaderStyle = createBaseStyle(font(11, true, false, rgb(255, 255, 255)), HorizontalAlignment.CENTER, navyBg, borderGray);
        tableHeaderStyle.setWrapText(true);

        textStyle = createBaseStyle(font(11, false, false, null), HorizontalAlignment.LEFT, null, borderGray);
        textAltStyle = createBaseStyle(font(11, false, false, null), HorizontalAlignment.LEFT, altRowBg, borderGray);

        centerStyle = createBaseStyle(font(11, false, false, null), HorizontalAlignment.CENTER, null, borderGray);
        centerAltStyle = createBaseStyle(font(11, false, false, null), HorizontalAlignment.CENTER, altRowBg, borderGray);

        intStyle = createBaseStyle(font(11, false, false, null), HorizontalAlignment.RIGHT, null, borderGray);
        intStyle.setDataFormat(fmt.getFormat("#,##0"));
        intAltStyle = createBaseStyle(font(11, false, false, null), HorizontalAlignment.RIGHT, altRowBg, borderGray);
        intAltStyle.setDataFormat(fmt.getFormat("#,##0"));

        currencyStyle = createBaseStyle(font(11, false, false, null), HorizontalAlignment.RIGHT, null, borderGray);
        currencyStyle.setDataFormat(fmt.getFormat("#,##0 \"₫\""));
        currencyAltStyle = createBaseStyle(font(11, false, false, null), HorizontalAlignment.RIGHT, altRowBg, borderGray);
        currencyAltStyle.setDataFormat(fmt.getFormat("#,##0 \"₫\""));

        decimalStyle = createBaseStyle(font(11, false, false, null), HorizontalAlignment.RIGHT, null, borderGray);
        decimalStyle.setDataFormat(fmt.getFormat("#,##0.00"));
        decimalAltStyle = createBaseStyle(font(11, false, false, null), HorizontalAlignment.RIGHT, altRowBg, borderGray);
        decimalAltStyle.setDataFormat(fmt.getFormat("#,##0.00"));

        totalLabelStyle = createBaseStyle(font(11, true, false, null), HorizontalAlignment.CENTER, totalBg, borderGray);
        totalIntStyle = createBaseStyle(font(11, true, false, null), HorizontalAlignment.RIGHT, totalBg, borderGray);
        totalIntStyle.setDataFormat(fmt.getFormat("#,##0"));
        totalCurrencyStyle = createBaseStyle(font(11, true, false, rgb(192, 0, 0)), HorizontalAlignment.RIGHT, totalBg, borderGray);
        totalCurrencyStyle.setDataFormat(fmt.getFormat("#,##0 \"₫\""));
        totalDecimalStyle = createBaseStyle(font(11, true, false, null), HorizontalAlignment.RIGHT, totalBg, borderGray);
        totalDecimalStyle.setDataFormat(fmt.getFormat("#,##0.00"));

        signTitleStyle = createBaseStyle(font(11, true, false, null), HorizontalAlignment.CENTER, null, null);
        signNoteStyle = createBaseStyle(font(10, false, true, null), HorizontalAlignment.CENTER, null, null);
    }

    public int writeReportBanner(Sheet sheet, int colCount, String reportTitle, String periodSubtitle, String[] headers) {
        sheet.setDisplayGridlines(true);
        int midCol = Math.max(2, colCount / 2);

        Row r0 = sheet.createRow(0);
        r0.setHeightInPoints(19);
        setCell(r0, 0, "HỆ THỐNG CHUỖI NHÀ THUỐC GPP – IS009", companyTitleStyle);
        setCell(r0, midCol, "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", mottoTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, midCol - 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, midCol, colCount - 1));

        Row r1 = sheet.createRow(1);
        r1.setHeightInPoints(18);
        setCell(r1, 0, "Phòng Kế hoạch - Cung ứng & Quản trị Dược phẩm", companySubStyle);
        setCell(r1, midCol, "Độc lập - Tự do - Hạnh phúc", mottoSubStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, midCol - 1));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, midCol, colCount - 1));

        Row r3 = sheet.createRow(3);
        r3.setHeightInPoints(26);
        setCell(r3, 0, reportTitle, reportTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, colCount - 1));

        Row r4 = sheet.createRow(4);
        r4.setHeightInPoints(19);
        String nowStr = ThoiGianVN.dinhDangNgayGio(java.time.Instant.now());
        setCell(r4, 0, periodSubtitle + "  |  Thời điểm kết xuất: " + nowStr, reportSubtitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, colCount - 1));

        Row rHeader = sheet.createRow(6);
        rHeader.setHeightInPoints(28);
        for (int c = 0; c < headers.length; c++) {
            setCell(rHeader, c, headers[c], tableHeaderStyle);
        }
        sheet.setAutoFilter(new CellRangeAddress(6, 6, 0, colCount - 1));
        sheet.createFreezePane(0, 7);
        return 7;
    }

    public void writeSignatureFooter(Sheet sheet, int startRow, int colCount, int[] minColWidths) {
        LocalDate today = ThoiGianVN.homNay();
        int dateRowIdx = startRow + 1;
        Row rDate = sheet.createRow(dateRowIdx);
        rDate.setHeightInPoints(18);
        int rightStart = Math.max(0, colCount - 3);
        String dateText = String.format("TP. Hồ Chí Minh, ngày %02d tháng %02d năm %d",
                today.getDayOfMonth(), today.getMonthValue(), today.getYear());
        setCell(rDate, rightStart, dateText, signNoteStyle);
        if (colCount - 1 > rightStart) {
            sheet.addMergedRegion(new CellRangeAddress(dateRowIdx, dateRowIdx, rightStart, colCount - 1));
        }

        Row rSign = sheet.createRow(dateRowIdx + 1);
        rSign.setHeightInPoints(20);
        Row rNote = sheet.createRow(dateRowIdx + 2);
        rNote.setHeightInPoints(17);

        int seg = Math.max(1, colCount / 3);
        int c1End = Math.min(colCount - 1, seg - 1);
        int c2Start = Math.min(colCount - 1, c1End + 1);
        int c2End = Math.min(colCount - 1, c2Start + seg - 1);
        int c3Start = Math.min(colCount - 1, c2End + 1);

        setCell(rSign, 0, "NGƯỜI LẬP BIỂU", signTitleStyle);
        setCell(rNote, 0, "(Ký, ghi rõ họ tên)", signNoteStyle);
        if (c1End > 0) {
            sheet.addMergedRegion(new CellRangeAddress(dateRowIdx + 1, dateRowIdx + 1, 0, c1End));
            sheet.addMergedRegion(new CellRangeAddress(dateRowIdx + 2, dateRowIdx + 2, 0, c1End));
        }

        if (c2Start < colCount) {
            setCell(rSign, c2Start, "PHỤ TRÁCH KHO / DƯỢC SĨ", signTitleStyle);
            setCell(rNote, c2Start, "(Ký, kiểm tra đối chiếu)", signNoteStyle);
            if (c2End > c2Start) {
                sheet.addMergedRegion(new CellRangeAddress(dateRowIdx + 1, dateRowIdx + 1, c2Start, c2End));
                sheet.addMergedRegion(new CellRangeAddress(dateRowIdx + 2, dateRowIdx + 2, c2Start, c2End));
            }
        }

        if (c3Start < colCount) {
            setCell(rSign, c3Start, "BAN GIÁM ĐỐC / QUẢN LÝ", signTitleStyle);
            setCell(rNote, c3Start, "(Ký duyệt, đóng dấu)", signNoteStyle);
            if (colCount - 1 > c3Start) {
                sheet.addMergedRegion(new CellRangeAddress(dateRowIdx + 1, dateRowIdx + 1, c3Start, colCount - 1));
                sheet.addMergedRegion(new CellRangeAddress(dateRowIdx + 2, dateRowIdx + 2, c3Start, colCount - 1));
            }
        }

        for (int c = 0; c < colCount; c++) {
            int widthChars = (minColWidths != null && c < minColWidths.length) ? minColWidths[c] : 16;
            sheet.setColumnWidth(c, widthChars * 256);
        }
    }

    public void setCell(Row row, int col, String val, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(val != null ? val : "");
        cell.setCellStyle(style);
    }

    public void setInt(Row row, int col, long val, boolean alt) {
        Cell cell = row.createCell(col);
        cell.setCellValue(val);
        cell.setCellStyle(alt ? intAltStyle : intStyle);
    }

    public void setMoney(Row row, int col, BigDecimal val, boolean alt) {
        Cell cell = row.createCell(col);
        cell.setCellValue(val != null ? val.doubleValue() : 0d);
        cell.setCellStyle(alt ? currencyAltStyle : currencyStyle);
    }

    public void setDec(Row row, int col, BigDecimal val, boolean alt) {
        Cell cell = row.createCell(col);
        cell.setCellValue(val != null ? val.doubleValue() : 0d);
        cell.setCellStyle(alt ? decimalAltStyle : decimalStyle);
    }

    private XSSFFont font(int pt, boolean bold, boolean italic, XSSFColor color) {
        XSSFFont f = wb.createFont();
        f.setFontName(FONT_FAMILY);
        f.setFontHeightInPoints((short) pt);
        f.setBold(bold);
        f.setItalic(italic);
        if (color != null) {
            f.setColor(color);
        }
        return f;
    }

    private XSSFCellStyle createBaseStyle(XSSFFont font, HorizontalAlignment align, XSSFColor bg, XSSFColor borderCol) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFont(font);
        s.setAlignment(align);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        if (bg != null) {
            s.setFillForegroundColor(bg);
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (borderCol != null) {
            s.setBorderTop(BorderStyle.THIN);
            s.setBorderBottom(BorderStyle.THIN);
            s.setBorderLeft(BorderStyle.THIN);
            s.setBorderRight(BorderStyle.THIN);
            s.setTopBorderColor(borderCol);
            s.setBottomBorderColor(borderCol);
            s.setLeftBorderColor(borderCol);
            s.setRightBorderColor(borderCol);
        }
        return s;
    }

    private static XSSFColor rgb(int r, int g, int b) {
        return new XSSFColor(new Color(r, g, b), new DefaultIndexedColorMap());
    }
}
