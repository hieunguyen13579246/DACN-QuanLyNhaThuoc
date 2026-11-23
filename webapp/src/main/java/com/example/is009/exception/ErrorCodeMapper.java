package com.example.is009.exception;

import com.example.is009.common.ApiResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.Map;

public final class ErrorCodeMapper {

    public record ThongTinLoi(String thongBaoMacDinh, String huongDanSua, HttpStatus status) {
    }

    private static final Map<String, ThongTinLoi> BANG_MA_LOI = Map.ofEntries(
            Map.entry("50001", new ThongTinLoi(
                    "Thuốc không tồn tại hoặc đã ngừng kinh doanh.",
                    "Kiểm tra lại danh mục thuốc hoặc liên hệ Quản trị viên kích hoạt lại thuốc.",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50002", new ThongTinLoi(
                    "Không đủ tồn kho cho một hoặc nhiều mặt hàng/lô thuốc.",
                    "Giảm số lượng cần bán/xuất hoặc lập phiếu nhập kho/điều chuyển bổ sung.",
                    HttpStatus.CONFLICT)),
            Map.entry("50003", new ThongTinLoi(
                    "Lô thuốc đã hết hạn sử dụng, không được phép bán (BR03).",
                    "Chọn lô thuốc khác còn hạn dùng và lập phiếu xuất hủy cho lô hết hạn.",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50004", new ThongTinLoi(
                    "Vi phạm quy định bán thuốc kê đơn (thiếu đơn thuốc, thuốc không nằm trong đơn, hoặc đơn của nhà thuốc khác).",
                    "Vào màn Đơn thuốc (M04) để tiếp nhận đơn hợp lệ tại nhà thuốc hiện tại rồi gắn vào giỏ hàng.",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50005", new ThongTinLoi(
                    "Đơn thuốc đã hết hiệu lực hoặc đã bán hết/bị hủy.",
                    "Yêu cầu khách hàng cung cấp đơn thuốc mới còn hiệu lực từ cơ sở khám chữa bệnh.",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50006", new ThongTinLoi(
                    "Số lượng bán vượt quá số lượng còn được phép mua trong đơn thuốc.",
                    "Giảm số lượng thuốc kê đơn xuống nhỏ hơn hoặc bằng số lượng còn lại trên đơn.",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50007", new ThongTinLoi(
                    "Mã vạch không hợp lệ (sai chuẩn 13 chữ số/chữ số kiểm EAN-13) hoặc đã tồn tại.",
                    "Kiểm tra lại chữ số kiểm tra cuối cùng của mã EAN-13 hoặc nhập mã vạch chưa được gán.",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50008", new ThongTinLoi(
                    "Ngày tháng không hợp lệ (hạn dùng phải sau ngày nhập và ngày sản xuất; ngày kê đơn không ở tương lai).",
                    "Kiểm tra và nhập lại Ngày sản xuất, Hạn dùng (> hôm nay) hoặc Ngày kê đơn (≤ hôm nay).",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50009", new ThongTinLoi(
                    "Trạng thái của phiếu/đơn/giao dịch không cho phép thực hiện thao tác này.",
                    "Làm mới trang để cập nhật trạng thái mới nhất trước khi thao tác lại.",
                    HttpStatus.CONFLICT)),
            Map.entry("50010", new ThongTinLoi(
                    "Thiếu lý do bắt buộc hoặc lý do không hợp lệ.",
                    "Nhập đầy đủ lý do (hủy giao dịch, từ chối duyệt phiếu hoặc lý do xuất hủy) trước khi xác nhận.",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50011", new ThongTinLoi(
                    "Bạn không có quyền thực hiện thao tác này hoặc dữ liệu thuộc nhà thuốc khác.",
                    "Đăng nhập bằng tài khoản đúng vai trò và đúng phạm vi nhà thuốc được phân công.",
                    HttpStatus.FORBIDDEN)),
            Map.entry("50012", new ThongTinLoi(
                    "Nhà thuốc không hợp lệ (đang tạm ngưng hoặc nhà thuốc đích trùng nhà thuốc nguồn).",
                    "Chọn nhà thuốc đích khác nhà thuốc nguồn và đang ở trạng thái HOAT_DONG.",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50013", new ThongTinLoi(
                    "Tài khoản người dùng không tồn tại hoặc đã bị vô hiệu hóa.",
                    "Liên hệ Quản trị viên chuỗi (QUANTRI) để kiểm tra và kích hoạt tài khoản.",
                    HttpStatus.FORBIDDEN)),
            Map.entry("50014", new ThongTinLoi(
                    "Không tìm thấy dữ liệu yêu cầu (phiếu kho, đơn thuốc, giao dịch hoặc thuốc).",
                    "Kiểm tra lại mã tra cứu hoặc làm mới lại danh sách dữ liệu.",
                    HttpStatus.NOT_FOUND)),
            Map.entry("50015", new ThongTinLoi(
                    "Đã quá thời hạn cho phép hủy giao dịch (mặc định 24 giờ kể từ lúc bán).",
                    "Liên hệ Quản trị viên chuỗi để kiểm tra cấu hình hoặc xử lý theo quy trình ngoại lệ.",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50016", new ThongTinLoi(
                    "Danh sách chi tiết hàng hóa trống hoặc dữ liệu JSON không hợp lệ.",
                    "Thêm ít nhất một mặt hàng với số lượng > 0 trước khi lưu/xác nhận.",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50017", new ThongTinLoi(
                    "Khoảng thời gian tra cứu/thống kê không hợp lệ hoặc vượt quá 731 ngày.",
                    "Chọn Từ ngày ≤ Đến ngày và giới hạn phạm vi tối đa trong vòng 24 tháng (731 ngày).",
                    HttpStatus.BAD_REQUEST)),
            Map.entry("50018", new ThongTinLoi(
                    "Khách hàng không tồn tại trong hệ thống.",
                    "Vào mục Khách hàng để tạo mới hồ sơ khách hàng trước khi tiếp nhận đơn thuốc.",
                    HttpStatus.NOT_FOUND)),
            Map.entry("50099", new ThongTinLoi(
                    "Bảng lịch sử biến động kho / nhật ký kiểm toán là chỉ-thêm (append-only), cấm sửa xóa.",
                    "Sử dụng chức năng Hủy giao dịch hoặc lập Phiếu kho mới thay vì sửa trực tiếp lịch sử.",
                    HttpStatus.FORBIDDEN))
    );

    private ErrorCodeMapper() {
    }

    public static ThongTinLoi traCuu(String errorCode) {
        if (errorCode == null) {
            return new ThongTinLoi(
                    "Đã xảy ra lỗi khi xử lý nghiệp vụ.",
                    "Vui lòng kiểm tra lại dữ liệu đầu vào và thử lại.",
                    HttpStatus.BAD_REQUEST);
        }
        return BANG_MA_LOI.getOrDefault(errorCode, new ThongTinLoi(
                "Lỗi nghiệp vụ (" + errorCode + ").",
                "Vui lòng kiểm tra lại thông tin nhập liệu và thử lại.",
                HttpStatus.BAD_REQUEST));
    }

    public static NghiepVuException tuDataAccessException(DataAccessException ex) {
        SQLException sqlEx = timSqlException(ex);
        if (sqlEx != null) {
            int code = sqlEx.getErrorCode();
            if (code == 1205) {
                return new NghiepVuException("DEADLOCK",
                        "Hệ thống đang xử lý giao dịch đồng thời trên cùng lô thuốc.",
                        "Vui lòng nhấn Xác nhận lại sau giây lát.");
            }
            String codeStr = String.valueOf(code);
            if (BANG_MA_LOI.containsKey(codeStr)) {
                ThongTinLoi info = BANG_MA_LOI.get(codeStr);
                String msg = (sqlEx.getMessage() != null && !sqlEx.getMessage().isBlank())
                        ? sqlEx.getMessage()
                        : info.thongBaoMacDinh();
                return new NghiepVuException(codeStr, msg, info.huongDanSua());
            }
            if (code == 2627 || code == 2601) {
                return new NghiepVuException("DUPLICATE",
                        "Mã hoặc dữ liệu định danh đã tồn tại trong hệ thống.",
                        "Vui lòng kiểm tra lại mã (mã thuốc, mã vạch, email, mã nhà thuốc...) để không bị trùng.");
            }
            if (code == 547) {
                return new NghiepVuException("CONSTRAINT",
                        "Dữ liệu vi phạm ràng buộc toàn vẹn (giá trị hoặc khóa ngoại không hợp lệ).",
                        "Kiểm tra lại các giá trị số lượng, đơn giá (> 0) và thông tin danh mục liên kết.");
            }
        }
        return new NghiepVuException("DB_ERROR",
                "Không thể thực hiện thao tác cơ sở dữ liệu: " + ex.getMostSpecificCause().getMessage(),
                "Kiểm tra lại dữ liệu nhập vào hoặc thử lại.");
    }

    public static ResponseEntity<ApiResponse<Void>> mapException(NghiepVuException ex) {
        ThongTinLoi info = traCuu(ex.getErrorCode());
        String thongBao = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : info.thongBaoMacDinh();
        String huongDan = (ex.getHuongDanSua() != null && !ex.getHuongDanSua().isBlank())
                ? ex.getHuongDanSua()
                : info.huongDanSua();
        return ResponseEntity.status(info.status())
                .body(ApiResponse.thatBai(ex.getErrorCode(), thongBao, huongDan));
    }

    public static Map<String, ThongTinLoi> tatCaMaLoi() {
        return BANG_MA_LOI;
    }

    private static SQLException timSqlException(Throwable t) {
        Throwable cur = t;
        while (cur != null) {
            if (cur instanceof SQLException sqlEx) {
                return sqlEx;
            }
            cur = cur.getCause();
        }
        return null;
    }
}
