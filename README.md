# IS009 – Hệ thống Quản lý Chuỗi Nhà thuốc và Phân tích Nhu cầu Dược phẩm theo Khu vực

Đồ án chuyên ngành xây dựng hệ thống quản lý vận hành chuỗi nhà thuốc đạt chuẩn GPP (bán hàng POS theo nguyên tắc FEFO, quản lý đơn thuốc kê toa Rx, nhập/điều chuyển/xuất hủy kho có luồng phê duyệt, quét mã vạch, kết xuất báo cáo Excel `.xlsx`) và phân tích nhu cầu dược phẩm theo khu vực địa lý.

## Cấu trúc thư mục

- **`database/`**: Bộ script SQL Server khởi tạo CSDL `IS009` gồm cấu trúc bảng, ràng buộc & chỉ mục, hàm & Stored Procedures nghiệp vụ/thống kê, triggers kiểm soát toàn vẹn, views, dữ liệu mẫu cho 9 chi nhánh (`NT001`–`NT009`, bao gồm chi nhánh Cần Thơ Ninh Kiều), phân quyền bảo mật và kịch bản kiểm thử (`01_schema.sql` .. `08_kiem_thu_kich_ban.sql`).
- **`docs/`**: Tài liệu phân tích & thiết kế hệ thống (khảo sát nghiệp vụ, Use Case, Activity Diagram, Sequence Diagram, ERD, mô hình quan hệ, Class Diagram, kiến trúc phân quyền & giao diện, thống kê báo cáo, truy vết kiểm thử demo và ảnh chụp màn hình trong `docs/images/`).
- **`webapp/`**: Ứng dụng web **Java 17 + Spring Boot 3.3.5 + Thymeleaf + Bootstrap 5 + Apache POI `.xlsx`** kết nối SQL Server. Xem hướng dẫn cài đặt, cấu hình và chạy chi tiết tại [webapp/README.md](webapp/README.md).

## Khởi chạy nhanh

1. Khởi tạo cơ sở dữ liệu `IS009` trên SQL Server bằng bộ script trong `database/` theo thứ tự `01_schema.sql` → `07_security.sql` (mã hóa UTF-8 với `sqlcmd -f 65001`).
2. Di chuyển vào `webapp/`, kiểm tra kết nối SQL Server trong `src/main/resources/application.properties` và chạy ứng dụng với `mvn spring-boot:run`.
3. Truy cập `http://localhost:8080` để sử dụng hệ thống.
