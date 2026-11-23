package com.example.is009.thongke.service;

import com.example.is009.common.ThoiGianVN;
import com.example.is009.thongke.dto.DeXuatNhapHangDTO;
import com.example.is009.thongke.dto.HieuSuatNhaThuocDTO;
import com.example.is009.thongke.dto.NhuCauKhuVucDTO;
import com.example.is009.thongke.dto.TopThuocDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CsvExportService {

    public byte[] xuatCsvNhuCauKhuVuc(List<NhuCauKhuVucDTO> rows) {
        return xuatExcelNhuCauKhuVuc(rows, null, null);
    }

    public byte[] xuatExcelNhuCauKhuVuc(List<NhuCauKhuVucDTO> rows, LocalDate tuNgay, LocalDate denNgay) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ExcelWorkbookStyler st = new ExcelWorkbookStyler(wb);
            Sheet sheet = wb.createSheet("Nhu cau duoc pham khu vuc");
            String[] headers = {
                "STT", "Cấp hành chính", "Tên khu vực địa lý", "Quy mô dân số (người)",
                "Mã nhóm", "Nhóm dược lý / điều trị", "Tổng SL tiêu thụ",
                "Tổng doanh thu (VND)", "Số lượt khách mua", "Cường độ tiêu thụ (SL / 1.000 dân)"
            };
            int[] widths = {7, 16, 26, 20, 12, 28, 18, 22, 18, 26};
            int rowIdx = st.writeReportBanner(sheet, headers.length,
                    "BÁO CÁO PHÂN TÍCH NHU CẦU DƯỢC PHẨM THEO KHU VỰC ĐỊA LÝ",
                    moTaKyBaoCao(tuNgay, denNgay), headers);

            long sumDanSo = 0;
            long sumSoLuong = 0;
            long sumKhach = 0;
            BigDecimal sumDoanhThu = BigDecimal.ZERO;

            for (int i = 0; i < rows.size(); i++) {
                NhuCauKhuVucDTO r = rows.get(i);
                boolean alt = (i % 2 == 1);
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);
                st.setCell(row, 0, String.valueOf(i + 1), alt ? st.centerAltStyle : st.centerStyle);
                st.setCell(row, 1, r.getTenCapHanhChinh(), alt ? st.centerAltStyle : st.centerStyle);
                st.setCell(row, 2, r.getTenKhuVuc(), alt ? st.textAltStyle : st.textStyle);
                long danSo = r.getDanSo() != null ? r.getDanSo() : 0L;
                st.setInt(row, 3, danSo, alt);
                st.setCell(row, 4, "NH-" + (r.getMaNhom() != null ? r.getMaNhom() : ""), alt ? st.centerAltStyle : st.centerStyle);
                st.setCell(row, 5, r.getTenNhom(), alt ? st.textAltStyle : st.textStyle);
                st.setInt(row, 6, r.getTongSoLuong(), alt);
                st.setMoney(row, 7, r.getTongDoanhThu(), alt);
                st.setInt(row, 8, r.getSoGiaoDich(), alt);
                st.setDec(row, 9, r.getSoLuongTren1000Dan(), alt);

                sumDanSo += danSo;
                sumSoLuong += r.getTongSoLuong();
                sumKhach += r.getSoGiaoDich();
                if (r.getTongDoanhThu() != null) sumDoanhThu = sumDoanhThu.add(r.getTongDoanhThu());
            }

            Row totalRow = sheet.createRow(rowIdx++);
            totalRow.setHeightInPoints(23);
            for (int c = 0; c < 6; c++) st.setCell(totalRow, c, c == 0 ? "TỔNG CỘNG TOÀN BÁO CÁO" : "", st.totalLabelStyle);
            sheet.addMergedRegion(new CellRangeAddress(totalRow.getRowNum(), totalRow.getRowNum(), 0, 5));
            setTotalInt(totalRow, 6, sumSoLuong, st);
            setTotalMoney(totalRow, 7, sumDoanhThu, st);
            setTotalInt(totalRow, 8, sumKhach, st);
            BigDecimal avg1000 = sumDanSo > 0
                    ? BigDecimal.valueOf(sumSoLuong * 1000.0 / sumDanSo)
                    : BigDecimal.ZERO;
            setTotalDec(totalRow, 9, avg1000, st);

            st.writeSignatureFooter(sheet, rowIdx, headers.length, widths);
            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Không thể khởi tạo file Excel báo cáo nhu cầu khu vực", e);
        }
    }

    public byte[] xuatCsvHieuSuatNhaThuoc(List<HieuSuatNhaThuocDTO> rows) {
        return xuatExcelHieuSuatNhaThuoc(rows, null, null);
    }

    public byte[] xuatExcelHieuSuatNhaThuoc(List<HieuSuatNhaThuocDTO> rows, LocalDate tuNgay, LocalDate denNgay) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ExcelWorkbookStyler st = new ExcelWorkbookStyler(wb);
            Sheet sheet = wb.createSheet("Hieu suat nha thuoc");
            String[] headers = {
                "STT", "Mã cơ sở", "Tên nhà thuốc thành viên", "Khu vực địa lý",
                "Số hóa đơn bán", "Tổng doanh thu thuần (VND)", "Tổng giá vốn hàng bán (VND)",
                "Lợi nhuận gộp (VND)", "Biên LN gộp (%)", "Giá trị giỏ hàng TB (VND)",
                "Tỷ lệ bán theo đơn (%)", "Vòng quay tồn kho"
            };
            int[] widths = {7, 13, 30, 24, 16, 23, 23, 22, 16, 22, 20, 18};
            int rowIdx = st.writeReportBanner(sheet, headers.length,
                    "BÁO CÁO ĐÁNH GIÁ HIỆU SUẤT KINH DOANH HỆ THỐNG NHÀ THUỐC",
                    moTaKyBaoCao(tuNgay, denNgay), headers);

            long sumGd = 0;
            BigDecimal sumDt = BigDecimal.ZERO;
            BigDecimal sumGv = BigDecimal.ZERO;
            BigDecimal sumLn = BigDecimal.ZERO;

            for (int i = 0; i < rows.size(); i++) {
                HieuSuatNhaThuocDTO r = rows.get(i);
                boolean alt = (i % 2 == 1);
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);
                st.setCell(row, 0, String.valueOf(i + 1), alt ? st.centerAltStyle : st.centerStyle);
                st.setCell(row, 1, r.getMaNhaThuoc(), alt ? st.centerAltStyle : st.centerStyle);
                st.setCell(row, 2, r.getTenNhaThuoc(), alt ? st.textAltStyle : st.textStyle);
                st.setCell(row, 3, r.getTenKhuVuc(), alt ? st.textAltStyle : st.textStyle);
                st.setInt(row, 4, r.getSoGiaoDich(), alt);
                st.setMoney(row, 5, r.getTongDoanhThu(), alt);
                st.setMoney(row, 6, r.getTongGiaVon(), alt);
                st.setMoney(row, 7, r.getLoiNhuanGop(), alt);
                st.setDec(row, 8, r.getTySuatLoiNhuanPct(), alt);
                st.setMoney(row, 9, r.getGiaTriGioTb(), alt);
                st.setDec(row, 10, r.getTyLeBanTheoDonPct(), alt);
                st.setDec(row, 11, r.getVongQuayTon(), alt);

                sumGd += r.getSoGiaoDich();
                if (r.getTongDoanhThu() != null) sumDt = sumDt.add(r.getTongDoanhThu());
                if (r.getTongGiaVon() != null) sumGv = sumGv.add(r.getTongGiaVon());
                if (r.getLoiNhuanGop() != null) sumLn = sumLn.add(r.getLoiNhuanGop());
            }

            Row totalRow = sheet.createRow(rowIdx++);
            totalRow.setHeightInPoints(23);
            for (int c = 0; c < 4; c++) st.setCell(totalRow, c, c == 0 ? "TỔNG CỘNG TOÀN CHUỖI" : "", st.totalLabelStyle);
            sheet.addMergedRegion(new CellRangeAddress(totalRow.getRowNum(), totalRow.getRowNum(), 0, 3));
            setTotalInt(totalRow, 4, sumGd, st);
            setTotalMoney(totalRow, 5, sumDt, st);
            setTotalMoney(totalRow, 6, sumGv, st);
            setTotalMoney(totalRow, 7, sumLn, st);
            BigDecimal avgMargin = sumDt.compareTo(BigDecimal.ZERO) > 0
                    ? sumLn.multiply(BigDecimal.valueOf(100)).divide(sumDt, 2, java.math.RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            setTotalDec(totalRow, 8, avgMargin, st);
            BigDecimal avgBasket = sumGd > 0
                    ? sumDt.divide(BigDecimal.valueOf(sumGd), 0, java.math.RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            setTotalMoney(totalRow, 9, avgBasket, st);
            st.setCell(totalRow, 10, "-", st.totalLabelStyle);
            st.setCell(totalRow, 11, "-", st.totalLabelStyle);

            st.writeSignatureFooter(sheet, rowIdx, headers.length, widths);
            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Không thể khởi tạo file Excel báo cáo hiệu suất nhà thuốc", e);
        }
    }

    public byte[] xuatCsvDeXuatNhapHang(List<DeXuatNhapHangDTO> rows) {
        return xuatExcelDeXuatNhapHang(rows);
    }

    public byte[] xuatExcelDeXuatNhapHang(List<DeXuatNhapHangDTO> rows) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ExcelWorkbookStyler st = new ExcelWorkbookStyler(wb);
            Sheet sheet = wb.createSheet("De xuat nhap hang GPP");
            String[] headers = {
                "STT", "Nhà thuốc nhận hàng", "Mã thuốc", "Tên thương mại dược phẩm",
                "ĐVT", "Tồn khả dụng hiện tại", "Tốc độ bán TB/ngày",
                "Thời gian chờ giao hàng (ngày)", "Số lượng đề xuất đặt nhập",
                "Đơn giá nhập dự kiến (VND)", "Thành tiền dự kiến (VND)"
            };
            int[] widths = {7, 28, 13, 32, 11, 19, 18, 22, 22, 23, 24};
            int rowIdx = st.writeReportBanner(sheet, headers.length,
                    "BẢNG ĐỀ XUẤT ĐẶT NHẬP BỔ SUNG DƯỢC PHẨM THEO ĐỊNH MỨC TỒN KHO",
                    "Căn cứ tính toán: Định mức tồn an toàn (Min/Max), tốc độ tiêu thụ 30 ngày & Lead-time nhà cung cấp", headers);

            long sumTon = 0;
            long sumDeXuat = 0;
            BigDecimal sumThanhTien = BigDecimal.ZERO;

            for (int i = 0; i < rows.size(); i++) {
                DeXuatNhapHangDTO r = rows.get(i);
                boolean alt = (i % 2 == 1);
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);
                st.setCell(row, 0, String.valueOf(i + 1), alt ? st.centerAltStyle : st.centerStyle);
                st.setCell(row, 1, r.getTenNhaThuoc(), alt ? st.textAltStyle : st.textStyle);
                st.setCell(row, 2, r.getMaThuoc(), alt ? st.centerAltStyle : st.centerStyle);
                st.setCell(row, 3, r.getTenThuoc(), alt ? st.textAltStyle : st.textStyle);
                st.setCell(row, 4, r.getDonViTinh(), alt ? st.centerAltStyle : st.centerStyle);
                st.setInt(row, 5, r.getTonKhaDung(), alt);
                st.setDec(row, 6, r.getBanTrungBinhNgay(), alt);
                st.setInt(row, 7, r.getSoNgayGiaoHang(), alt);
                st.setInt(row, 8, r.getSoLuongDeXuatNhap(), alt);
                st.setMoney(row, 9, r.getGiaNhapGanNhat(), alt);
                st.setMoney(row, 10, r.getThanhTienDuKien(), alt);

                sumTon += r.getTonKhaDung();
                sumDeXuat += r.getSoLuongDeXuatNhap();
                if (r.getThanhTienDuKien() != null) sumThanhTien = sumThanhTien.add(r.getThanhTienDuKien());
            }

            Row totalRow = sheet.createRow(rowIdx++);
            totalRow.setHeightInPoints(23);
            for (int c = 0; c < 5; c++) st.setCell(totalRow, c, c == 0 ? "TỔNG CỘNG ĐỀ XUẤT NHẬP HÀNG" : "", st.totalLabelStyle);
            sheet.addMergedRegion(new CellRangeAddress(totalRow.getRowNum(), totalRow.getRowNum(), 0, 4));
            setTotalInt(totalRow, 5, sumTon, st);
            st.setCell(totalRow, 6, "-", st.totalLabelStyle);
            st.setCell(totalRow, 7, "-", st.totalLabelStyle);
            setTotalInt(totalRow, 8, sumDeXuat, st);
            st.setCell(totalRow, 9, "-", st.totalLabelStyle);
            setTotalMoney(totalRow, 10, sumThanhTien, st);

            st.writeSignatureFooter(sheet, rowIdx, headers.length, widths);
            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Không thể khởi tạo file Excel đề xuất nhập hàng", e);
        }
    }

    public byte[] xuatExcelTopThuoc(List<TopThuocDTO> rows, LocalDate tuNgay, LocalDate denNgay) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ExcelWorkbookStyler st = new ExcelWorkbookStyler(wb);
            Sheet sheet = wb.createSheet("Top thuoc ban chay");
            String[] headers = {
                "STT", "Xếp hạng khu vực", "Khu vực địa lý", "Mã thuốc",
                "Tên thương mại dược phẩm", "Hoạt chất chính",
                "Tổng số lượng bán", "Tổng doanh thu (VND)"
            };
            int[] widths = {7, 16, 24, 13, 30, 30, 18, 24};
            int rowIdx = st.writeReportBanner(sheet, headers.length,
                    "BÁO CÁO XẾP HẠNG TOP DƯỢC PHẨM BÁN CHẠY THEO KHU VỰC",
                    moTaKyBaoCao(tuNgay, denNgay), headers);

            long sumSl = 0;
            BigDecimal sumDt = BigDecimal.ZERO;

            for (int i = 0; i < rows.size(); i++) {
                TopThuocDTO r = rows.get(i);
                boolean alt = (i % 2 == 1);
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);
                st.setCell(row, 0, String.valueOf(i + 1), alt ? st.centerAltStyle : st.centerStyle);
                st.setCell(row, 1, "Hạng #" + r.getXepHang(), alt ? st.centerAltStyle : st.centerStyle);
                st.setCell(row, 2, r.getKhuVuc(), alt ? st.textAltStyle : st.textStyle);
                st.setCell(row, 3, r.getMaThuoc(), alt ? st.centerAltStyle : st.centerStyle);
                st.setCell(row, 4, r.getTenThuoc(), alt ? st.textAltStyle : st.textStyle);
                st.setCell(row, 5, r.getHoatChat(), alt ? st.textAltStyle : st.textStyle);
                st.setInt(row, 6, r.getTongSoLuong(), alt);
                st.setMoney(row, 7, r.getTongDoanhThu(), alt);

                sumSl += r.getTongSoLuong();
                if (r.getTongDoanhThu() != null) sumDt = sumDt.add(r.getTongDoanhThu());
            }

            Row totalRow = sheet.createRow(rowIdx++);
            totalRow.setHeightInPoints(23);
            for (int c = 0; c < 6; c++) st.setCell(totalRow, c, c == 0 ? "TỔNG CỘNG TOP THUỐC" : "", st.totalLabelStyle);
            sheet.addMergedRegion(new CellRangeAddress(totalRow.getRowNum(), totalRow.getRowNum(), 0, 5));
            setTotalInt(totalRow, 6, sumSl, st);
            setTotalMoney(totalRow, 7, sumDt, st);

            st.writeSignatureFooter(sheet, rowIdx, headers.length, widths);
            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Không thể khởi tạo file Excel top thuốc bán chạy", e);
        }
    }

    private String moTaKyBaoCao(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null && denNgay == null) {
            return "Phạm vi: Theo bộ lọc hiện hành";
        }
        String tu = tuNgay != null ? ThoiGianVN.dinhDangNgay(tuNgay) : "...";
        String den = denNgay != null ? ThoiGianVN.dinhDangNgay(denNgay) : "...";
        return "Kỳ báo cáo: Từ ngày " + tu + " đến ngày " + den;
    }

    private void setTotalInt(Row r, int col, long val, ExcelWorkbookStyler st) {
        Cell cell = r.createCell(col);
        cell.setCellValue(val);
        cell.setCellStyle(st.totalIntStyle);
    }

    private void setTotalMoney(Row r, int col, BigDecimal val, ExcelWorkbookStyler st) {
        Cell cell = r.createCell(col);
        cell.setCellValue(val != null ? val.doubleValue() : 0d);
        cell.setCellStyle(st.totalCurrencyStyle);
    }

    private void setTotalDec(Row r, int col, BigDecimal val, ExcelWorkbookStyler st) {
        Cell cell = r.createCell(col);
        cell.setCellValue(val != null ? val.doubleValue() : 0d);
        cell.setCellStyle(st.totalDecimalStyle);
    }
}
