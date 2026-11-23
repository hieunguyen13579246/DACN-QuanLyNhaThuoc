# IS009 – Hệ thống Quản lý Chuỗi Nhà thuốc và Phân tích Nhu cầu Dược phẩm theo Khu vực (có Quét mã vạch)

Ứng dụng Web quản lý vận hành chuỗi nhà thuốc đạt chuẩn GPP, kiểm soát tồn kho theo lô – hạn dùng (**FEFO – First Expired, First Out**), quản lý bán thuốc kê đơn (Rx), điều chuyển nội bộ, cảnh báo chủ động, quét mã vạch đa chế độ và kết xuất báo cáo phân tích nhu cầu dược phẩm theo khu vực địa lý ra biểu mẫu **Microsoft Excel (`.xlsx`)** chuẩn thực tế.

---

## 1. Công nghệ, Kiến trúc & Chuẩn Giao diện

- **Ngôn ngữ & Nền tảng:** Java 17, Spring Boot 3.3.5, Apache Maven
- **Giao diện & Trải nghiệm người dùng (UI/UX):**
  - **Spring MVC + Thymeleaf (Fragment Layout)**, Bootstrap 5.3, **Bootstrap Icons 1.11.3**, Chart.js 4, JsBarcode, `html5-qrcode`, JavaScript thuần (`fetch` API).
  - **Đồng nhất phông chữ `Times New Roman`:** Toàn bộ giao diện web, bảng biểu, hộp thoại, biểu đồ thống kê (Chart.js), tem nhãn mã vạch và file báo cáo Excel kết xuất đều sử dụng thống nhất phông chữ `"Times New Roman", Times, serif`.
  - **Thanh điều hướng bên trái (Sidebar) trực quan bằng Icon:** Các chức năng được phân thành 4 nhóm nghiệp vụ (*Bán hàng & Đơn thuốc*, *Kho & Mã vạch*, *Quản lý & Phân tích*, *Quản trị chuỗi*) với biểu tượng trực quan trong khung bo góc (ngôi nhà cho *Trang chủ*, giỏ hàng cho *Bán hàng POS*, nhóm người cho *Khách hàng*, kiện hàng cho *Nhập kho*, dấu tích duyệt cho *Duyệt phiếu kho*, biểu đồ cho *Dashboard toàn chuỗi*...), hiển thị tên tiếng Việt tự nhiên (không kèm mã kỹ thuật `M01`–`M23` trên giao diện).
- **Kết xuất Báo cáo Excel thực tế (`Apache POI 5.2.5 – XSSFWorkbook`):**
  - Xuất trực tiếp file **`.xlsx`** có đầy đủ **Tiêu đề đơn vị & Quốc hiệu – Tiêu ngữ**, tên báo cáo, kỳ dữ liệu, bảng dữ liệu kẻ khung đóng viền, bật sẵn **AutoFilter** & **Freeze Pane**, định dạng số lượng `#,##0` và tiền tệ `#,##0 "₫"`, dòng **`TỔNG CỘNG`** nổi bật cuối bảng và **khối 3 chữ ký xác nhận** (*Người lập biểu – Phụ trách kho/Dược sĩ – Ban Giám đốc/Quản lý*).
- **Truy cập dữ liệu:**
  - **Spring Data JPA (`ddl-auto=none`):** Đọc/ghi các bảng danh mục và cấu hình.
  - **Spring JDBC (`SimpleJdbcCall` & `JdbcTemplate`):** Gọi trực tiếp toàn bộ 18 Stored Procedures nghiệp vụ/thống kê và đọc 6 Views từ SQL Server (`mssql-jdbc`).
- **Bảo mật & Phân quyền:** Spring Security 6 (Form Login, Session, `BCryptPasswordEncoder` cost 10, phân quyền RBAC 4 vai trò kết hợp kiểm tra phạm vi nhà thuốc tại tầng dịch vụ qua `PhamViService`).
- **Kiểm thử & Kiến trúc:** JUnit 5, Mockito, **ArchUnit** (`ArchitectureTest` kiểm soát chặt chẽ kiến trúc phân tầng 0 → 4, không có phụ thuộc vòng, cô lập module qua `Service` & `DTO`).

---

## 2. Yêu cầu Môi trường & Hướng dẫn Chạy

### 2.1. Yêu cầu cài đặt
1. **JDK 17+** và **Apache Maven 3.8+**
2. **Microsoft SQL Server 2019 / 2022**
3. Khởi tạo cơ sở dữ liệu `IS009` (bộ script chuẩn mã hóa UTF-8 `-f 65001` trong thư mục `../database/`):
   ```powershell
   cd ..\database
   .\run_all.ps1 -ServerInstance "localhost"
   ```

### 2.2. Cấu hình kết nối Cơ sở dữ liệu
Sao chép `.env.example` hoặc thiết lập biến môi trường (mặc định `application.properties` đã cấu hình sẵn để chạy ngay trên máy cục bộ):
- `DB_URL`: `jdbc:sqlserver://localhost:1433;databaseName=IS009;encrypt=true;trustServerCertificate=true`
- `DB_USERNAME`: `is009_app`
- `DB_PASSWORD`: `Is009@AppPass2026!`
- `SERVER_PORT`: `8080`

### 2.3. Chạy kiểm thử và khởi động ứng dụng
```powershell
cd d:\DACN\webapp

# 1. Chạy toàn bộ 25 Unit Test + Kiểm tra kiến trúc ArchUnit
mvn test

# 2. Khởi động ứng dụng Web
mvn spring-boot:run
```
Sau khi khởi động thành công, truy cập trình duyệt tại: **`http://localhost:8080`**

---

## 3. Danh sách Tài khoản Demo (Mật khẩu chung: `123456`)

Tất cả tài khoản mẫu trong `database/06_seed_p1.sql` đều sử dụng mật khẩu mặc định là **`123456`** (đã băm bằng BCrypt cost 10):

| Email đăng nhập | Mật khẩu | Vai trò | Nhà thuốc trực thuộc | Phạm vi quyền hạn chính |
| :--- | :---: | :--- | :--- | :--- |
| **`qt01@nhathuoc.vn`** | `123456` | `QUANTRI` | Toàn chuỗi (`NULL`) | Quản trị danh mục chuỗi, cấu hình, người dùng, đối soát tồn kho, duyệt phiếu toàn chuỗi, xem & xuất Excel Dashboard toàn chuỗi |
| **`ql01@nhathuoc.vn`** | `123456` | `QUANLY` | NT001 - Quận 1 (ID: 1) | Bán hàng POS, hủy giao dịch, duyệt phiếu nhập kho/điều chuyển/xuất hủy của NT001, định mức tồn, Dashboard nhà thuốc |
| **`tk01@nhathuoc.vn`** | `123456` | `THUKHO` | NT001 - Quận 1 (ID: 1) | Lập phiếu nhập kho từ NCC (quét GS1), lập phiếu điều chuyển/xuất hủy, in tem mã vạch, xem & xuất Excel đề xuất nhập hàng |
| **`ds01@nhathuoc.vn`** | `123456` | `DUOCSI` | NT001 - Quận 1 (ID: 1) | Bán hàng POS quét mã FEFO, tiếp nhận & tra cứu đơn thuốc, quản lý khách hàng, tra cứu thuốc bằng mã vạch |
| **`ql02@nhathuoc.vn`** | `123456` | `QUANLY` | NT002 - Quận Bình Thạnh (ID: 2) | Quản lý vận hành tại Nhà thuốc NT002 |
| **`tk02@nhathuoc.vn`** | `123456` | `THUKHO` | NT002 - Quận Bình Thạnh (ID: 2) | Thủ kho tại Nhà thuốc NT002 |
| **`ds02@nhathuoc.vn`** | `123456` | `DUOCSI` | NT002 - Quận Bình Thạnh (ID: 2) | Dược sĩ bán hàng tại Nhà thuốc NT002 |
| **`ql09@nhathuoc.vn`** | `123456` | `QUANLY` | NT009 - Quận Ninh Kiều, Cần Thơ (ID: 9) | Quản lý vận hành & duyệt phiếu tại Nhà thuốc NT009 |
| **`tk09@nhathuoc.vn`** | `123456` | `THUKHO` | NT009 - Quận Ninh Kiều, Cần Thơ (ID: 9) | Thủ kho tại Nhà thuốc NT009 |
| **`ds09@nhathuoc.vn`** | `123456` | `DUOCSI` | NT009 - Quận Ninh Kiều, Cần Thơ (ID: 9) | Dược sĩ bán hàng tại Nhà thuốc NT009 |
| **`qt02@nhathuoc.vn`** | `123456` | `QUANTRI` | Toàn chuỗi (`NULL`) | Quản trị viên dự phòng toàn chuỗi |

---

## 4. Danh mục Chức năng & Biểu tượng Điều hướng (Sidebar)

Thanh menu bên trái tự động hiển thị các mục tương ứng với vai trò của người dùng đang đăng nhập:

| Nhóm trên Sidebar | Tên mục trên giao diện | Biểu tượng (Bootstrap Icon) | Đường dẫn URL | Vai trò truy cập |
| :--- | :--- | :--- | :--- | :--- |
| **Tổng quan** | **Trang chủ** | 🏠 `bi-house-door-fill` | `/` | Tất cả vai trò |
| **Bán hàng & Đơn thuốc** | **Bán hàng POS** | 🛒 `bi-cart-check-fill` | `/banhang/pos` | `DUOCSI`, `QUANLY` |
| | **Đơn thuốc** | 📋 `bi-file-earmark-medical-fill` | `/donthuoc` | `DUOCSI`, `QUANLY`, `QUANTRI` |
| | **Khách hàng** | 👥 `bi-people-fill` | `/khachhang` | `DUOCSI`, `QUANLY`, `QUANTRI` |
| | **Tra cứu mã vạch** | 🔢 `bi-upc-scan` | `/thuoc/tra-cuu-ma-vach` | Tất cả vai trò |
| | **Lịch sử giao dịch** | 🕒 `bi-clock-history` | `/banhang/lich-su` | `DUOCSI`, `QUANLY`, `QUANTRI` |
| | **Hủy giao dịch** | ❌ `bi-x-octagon-fill` | `/banhang/huy-giao-dich` | `QUANLY` |
| **Kho & Mã vạch** | **Nhập kho (Quét GS1)** | 📦 `bi-box-seam-fill` | `/kho/nhap-kho` | `THUKHO`, `QUANLY`, `QUANTRI` |
| | **Tồn kho & Lô** | 📑 `bi-layers-fill` | `/kho/ton-kho` | Tất cả vai trò |
| | **Điều chuyển & Xuất hủy** | 🔄 `bi-arrow-left-right` | `/kho/phieu-kho` | `THUKHO`, `QUANLY`, `QUANTRI` |
| | **In tem mã vạch** | 🖨️ `bi-printer-fill` | `/kho/in-tem` | `THUKHO`, `QUANLY`, `QUANTRI` |
| | **Cảnh báo kho** | 🔔 `bi-bell-fill` | `/canhbao` | Tất cả vai trò |
| **Quản lý & Phân tích** | **Duyệt phiếu kho** | ✅ `bi-check2-square` | `/kho/duyet-phieu` | `QUANLY`, `QUANTRI` |
| | **Định mức tồn** | 🎚️ `bi-sliders2-vertical` | `/kho/dinh-muc` | `QUANLY`, `QUANTRI` |
| | **Đề xuất nhập hàng** | 💡 `bi-lightbulb-fill` | `/thongke/de-xuat-nhap` | `THUKHO`, `QUANLY`, `QUANTRI` |
| | **Dashboard nhà thuốc** | 📈 `bi-graph-up-arrow` | `/thongke/dashboard-nha-thuoc` | Tất cả vai trò |
| | **Dashboard toàn chuỗi** | 📊 `bi-bar-chart-line-fill` | `/thongke/dashboard-chuoi` | `QUANLY`, `QUANTRI` |
| **Quản trị chuỗi** | **Thuốc, Nhóm & Mã vạch** | 💊 `bi-capsule-pill` | `/thuoc/quan-ly` | `QUANTRI` |
| | **NCC, Khu vực, Nhà thuốc** | 🏥 `bi-buildings-fill` | `/quantri/danh-muc-chuoi` | `QUANTRI` |
| | **Người dùng** | 🛡️ `bi-person-badge-fill` | `/quantri/nguoi-dung` | `QUANTRI` |
| | **Cấu hình hệ thống** | ⚙️ `bi-gear-fill` | `/quantri/cau-hinh` | `QUANTRI` |
| | **Nhật ký & Đối soát** | ✔️ `bi-shield-check` | `/quantri/nhat-ky-doi-soat` | `QUANTRI` |

---

## 5. Hướng dẫn Nghiệp vụ Kho, Mã vạch & Xuất Báo cáo Excel

### 5.1. Cơ chế Lập Phiếu Kho & Duyệt Phiếu Kho (`NHAP`, `DIEU_CHUYEN`, `XUAT_HUY`)
Tất cả các nghiệp vụ làm thay đổi tồn kho từ phiếu kho đều tuân thủ chặt chẽ quy trình **2 bước (Lập phiếu Chờ duyệt → Kiểm tra & Duyệt phiếu)**:
1. **Bước 1 – Lập phiếu kho (`CHO_DUYET`):**
   - Khi **Thủ kho (`tk01@nhathuoc.vn`)**, **Quản lý (`ql01@nhathuoc.vn`)** hoặc **Quản trị (`qt01@nhathuoc.vn`)** lập **Phiếu Nhập kho** tại mục **Nhập kho thuốc (`/kho/nhap-kho`)** hoặc lập **Phiếu Điều chuyển / Xuất hủy** tại mục **Phiếu điều chuyển & hủy (`/kho/phieu-kho`)**:
   - Hệ thống gọi `sp_NhapKho` / `sp_TaoPhieuDieuChuyen` / `sp_TaoPhieuXuatHuy` để ghi nhận phiếu ở trạng thái **`Đang chờ duyệt (CHO_DUYET)`** (chưa cộng/trừ số lượng vào `TonKho`).
2. **Bước 2 – Quản lý / Quản trị xem xét và Duyệt phiếu (`/kho/duyet-phieu`):**
   - **Quản lý nhà thuốc (`ql01@nhathuoc.vn`)** hoặc **Quản trị chuỗi (`qt01@nhathuoc.vn`)** vào mục **Duyệt phiếu kho (`/kho/duyet-phieu`)** sẽ thấy ngay danh sách các phiếu **Nhập kho**, **Điều chuyển**, **Xuất hủy** đang ở trạng thái **`Chờ duyệt`**.
   - Tại đây có thể bấm **Xem** để kiểm tra chi tiết nhà cung cấp, thuốc, số lô, hạn dùng, số lượng, đơn giá nhập:
     - Bấm **Duyệt**: Gọi `sp_DuyetPhieuKho (@ket_qua = 'DUYET')` → Hệ thống chính thức **cộng tồn kho** (với phiếu Nhập kho), **chuyển tồn kho** (với phiếu Điều chuyển) hoặc **trừ tồn kho** (với phiếu Xuất hủy), ghi nhận vào `BienDongKho` và chuyển trạng thái phiếu sang **`Đã duyệt (HOAN_THANH)`**.
     - Bấm **Từ chối**: Bắt buộc nhập lý do từ chối → Phiếu chuyển sang trạng thái **`Từ chối (TU_CHOI)`** và không làm thay đổi tồn kho.

### 5.2. Quét Mã vạch Đa chế độ (Camera, Máy quét USB, Nhập tay & GS1)
1. **Máy quét mã vạch USB & Nhập phím `Enter`:**
   - Tại các màn hình **Bán hàng POS**, **Tra cứu mã vạch** và **Nhập kho (Quét GS1)**, ô mã vạch luôn tự động nhận tiêu điểm (`autofocus`).
   - Bộ lắng nghe `barcode-scanner.js` phân biệt tốc độ gõ của máy quét USB (`≤ 50ms/ký tự`) và gõ tay nhấn `Enter`, tự động kích hoạt tra cứu ngay lập tức.
2. **Quét mã vạch bằng Camera (`html5-qrcode`):**
   - Nhấn nút **"Bật Camera"** cạnh ô quét mã vạch để mở khung quét trực tiếp từ webcam hoặc camera điện thoại (hỗ trợ chuẩn `EAN-13`, `EAN-8`, `CODE-128`, `CODE-39`, `QR Code`, `DataMatrix`), có khóa chống quét lặp liên tiếp (`scanLock`).
3. **Giải mã tự động chuỗi GS1-128 / GS1 DataMatrix khi Nhập kho:**
   - Khi quét hoặc dán chuỗi GS1 (ví dụ: `(01)08935001200012(17)281231(10)LO2026A`) tại màn hình **Nhập kho (Quét GS1)**, hệ thống tự động bóc tách:
     - **AI `(01)` GTIN:** Tự động tìm và chọn thuốc tương ứng.
     - **AI `(17)` Hạn dùng (`YYMMDD`):** Tự động điền ô Hạn sử dụng (`2028-12-31`).
     - **AI `(10)` Số lô:** Tự động điền ô Số lô (`LO2026A`).

### 5.3. Xuất Báo cáo Microsoft Excel Chuẩn Thực tế (`.xlsx`)
Hệ thống cung cấp các nút **Xuất Excel (`.xlsx`)** màu xanh lá tại:
- **Dashboard toàn chuỗi (`/thongke/dashboard-chuoi`):**
  - *Xuất Excel Nhu cầu khu vực* → `Bao_Cao_Nhu_Cau_Duoc_Pham_Khu_Vuc_IS009.xlsx`
  - *Xuất Excel Hiệu suất NT* → `Bao_Cao_Hieu_Suat_Chuoi_Nha_Thuoc_IS009.xlsx`
  - *Xuất Excel Top thuốc* → `Bao_Cao_Top_Thuoc_Ban_Chay_IS009.xlsx`
- **Đề xuất nhập hàng (`/thongke/de-xuat-nhap`):**
  - *Xuất Excel Đề xuất nhập hàng (.xlsx)* → `Bang_De_Xuat_Nhap_Hang_GPP_IS009.xlsx`
- **Dashboard nhà thuốc (`/thongke/dashboard-nha-thuoc`):**
  - *Xuất Excel Top thuốc (.xlsx)* → `Bao_Cao_Top_Thuoc_Ban_Chay_IS009.xlsx`

---

## 6. Bảng Mã lỗi Nghiệp vụ (`50001` – `50018`)

Tầng `ErrorCodeMapper` tự động bắt mã lỗi SQL Server (`50001`–`50018`) từ các Stored Procedures và chuyển thành thông báo tiếng Việt thân thiện kèm hướng dẫn khắc phục trên giao diện:

| Mã lỗi | Tên hằng lỗi | Thông báo hiển thị | Hướng dẫn khắc phục trên giao diện |
| :---: | :--- | :--- | :--- |
| **50001** | `TON_KHO_KHONG_DU` | Tồn kho khả dụng không đủ để thực hiện giao dịch. | Giảm số lượng bán/xuất hoặc lập phiếu điều chuyển từ nhà thuốc khác trong chuỗi. |
| **50002** | `LO_DA_HET_HAN` | Lô thuốc đã hết hạn sử dụng, hệ thống chặn xuất bán. | Chọn lô khác còn hạn sử dụng hoặc lập phiếu xuất hủy đối với lô đã hết hạn. |
| **50003** | `THIEU_DON_THUOC_KE_DON` | Giỏ hàng có thuốc bắt buộc kê đơn (Rx) nhưng chưa gắn đơn thuốc hợp lệ. | Chọn khách hàng và gắn đơn thuốc còn hiệu lực có chứa thuốc kê đơn này trước khi thanh toán. |
| **50004** | `HAN_DUNG_NHAP_QUA_NGAN` | Hạn dùng của lô thuốc nhập mới ngắn hơn số ngày tối thiểu cho phép. | Kiểm tra lại ngày hết hạn trên bao bì hoặc từ chối nhập lô hàng quá cận hạn từ nhà cung cấp. |
| **50005** | `PHIEU_KHONG_O_TRANG_THAI_CHO_DUYET` | Phiếu kho không còn ở trạng thái Chờ duyệt nên không thể duyệt hoặc hủy. | Làm mới (F5) danh sách phiếu kho để cập nhật trạng thái mới nhất. |
| **50006** | `GIAO_DICH_DA_HUY` | Giao dịch này đã được hủy trước đó. | Kiểm tra lại mã giao dịch trong lịch sử bán hàng. |
| **50007** | `QUA_THOI_HAN_HUY_GIAO_DICH` | Đã quá thời hạn cho phép hủy giao dịch (theo cấu hình hệ thống). | Liên hệ Quản trị viên chuỗi để kiểm tra tham số `SO_GIO_CHO_PHEP_HUY_GD`. |
| **50008** | `VUOT_SO_LUONG_DON_THUOC` | Số lượng thuốc kê đơn xuất bán vượt quá số lượng còn lại trong đơn thuốc. | Giảm số lượng bán về đúng số lượng còn lại được phép mua trong đơn thuốc. |
| **50009** | `DON_THUOC_HET_HIEU_LUC` | Đơn thuốc đã hết hiệu lực hoặc đã bị hủy. | Tiếp nhận đơn thuốc mới còn hiệu lực của bác sĩ cho khách hàng. |
| **50010** | `DON_THUOC_KHONG_CHUA_THUOC_KE_DON` | Thuốc kê đơn trong giỏ hàng không nằm trong danh mục thuốc của đơn đã gắn. | Kiểm tra lại đơn thuốc đã chọn hoặc bổ sung đúng thuốc theo chỉ định. |
| **50011** | `MA_VACH_DA_TON_TAI` | Mã vạch này đã được gán cho một thuốc trong hệ thống. | Tra cứu mã vạch tại menu *Tra cứu mã vạch* để xem thuốc nào đang sở hữu mã này. |
| **50012** | `SAI_PHAM_VI_NHA_THUOC` | Bạn không có quyền thao tác dữ liệu thuộc phạm vi của nhà thuốc khác. | Chuyển về đúng nhà thuốc được phân công hoặc đăng nhập bằng tài khoản Quản trị chuỗi. |
| **50013** | `DU_LIEU_DAU_VAO_KHONG_HOP_LE` | Dữ liệu đầu vào không hợp lệ hoặc thiếu thông tin bắt buộc. | Kiểm tra lại số lượng (> 0), đơn giá (≥ 0), nhà thuốc nguồn/đích và lý do thao tác. |
| **50014** | `KHONG_TIM_THAY_BAN_GHI` | Không tìm thấy bản ghi dữ liệu yêu cầu trong hệ thống. | Làm mới trang và kiểm tra lại mã định danh của phiếu, đơn thuốc hoặc giao dịch. |
| **50015** | `MA_VACH_EAN13_KHONG_HOP_LE` | Mã vạch chuẩn EAN-13 không đúng chữ số kiểm tra (Check Digit). | Kiểm tra lại 13 chữ số trên bao bì hoặc đổi loại mã vạch sang `CODE128` / `NOIBO`. |
| **50016** | `DON_THUOC_DA_PHAT_SINH_GIAO_DICH` | Đơn thuốc đã phát sinh giao dịch bán hàng nên không thể hủy. | Chỉ có thể hủy đơn thuốc ở trạng thái Chưa bán (`CHUA_BAN`). |
| **50017** | `DON_THUOC_KHAC_KHACH_HANG` | Đơn thuốc được gắn không thuộc về khách hàng đang chọn trong giỏ hàng. | Chọn đúng khách hàng đứng tên trên đơn thuốc trước khi thanh toán. |
| **50018** | `DOI_TUONG_NGUNG_HOAT_DONG` | Thuốc, Nhà thuốc, Nhà cung cấp hoặc Người dùng đã ngừng hoạt động. | Kích hoạt lại đối tượng trong phần Quản trị danh mục hoặc chọn đối tượng khác đang hoạt động. |

---

## 7. Kịch bản Demo 8 Bước Khép kín (End-to-End Walkthrough)

Để kiểm chứng toàn bộ luồng nghiệp vụ từ Quản trị → Thủ kho → Dược sĩ → Quản lý trên thanh Sidebar:

1. **Bước 1 – Quản trị danh mục & Gán mã vạch (`qt01@nhathuoc.vn` / `123456`):**
   - Đăng nhập tài khoản `qt01@nhathuoc.vn`. Trên Sidebar bên trái, vào mục **Thuốc, Nhóm & Mã vạch** (`/thuoc/quan-ly`), chọn một thuốc và thử nhập mã EAN-13 sai chữ số kiểm tra → giao diện báo lỗi ngay tức thì. Lưu mã vạch hợp lệ qua `sp_ThemMaVachThuoc`.
2. **Bước 2 – Lập phiếu nhập kho bằng mã vạch & GS1 và Duyệt nhập (`tk01@nhathuoc.vn` → `ql01@nhathuoc.vn`):**
   - Đăng nhập `tk01@nhathuoc.vn` (Thủ kho NT001 - Quận 1). Vào mục **Nhập kho thuốc** (`/kho/nhap-kho`), quét hoặc dán mã `(01)08935001200012(17)280630(10)LONT2601` vào ô GS1 → hệ thống tự động điền Thuốc, Số lô `LONT2601` và Hạn dùng `30/06/2028`. Nhập số lượng `100`, giá nhập `12000` và bấm **Gửi phiếu nhập chờ duyệt** (`sp_NhapKho`) → phiếu ở trạng thái **`Chờ duyệt (CHO_DUYET)`** (chưa cộng tồn kho ngay).
   - Đăng nhập `ql01@nhathuoc.vn` (hoặc `qt01@nhathuoc.vn`), vào mục **Duyệt phiếu kho** (`/kho/duyet-phieu`), kiểm tra chi tiết và bấm **Duyệt** (`sp_DuyetPhieuKho`) → thuốc chính thức được cộng vào `TonKho` và ghi nhận `BienDongKho`.
3. **Bước 3 – In tem mã vạch & Kiểm tra tồn kho FEFO (`tk01@nhathuoc.vn`):**
   - Vào mục **In tem mã vạch** (`/kho/in-tem`), chọn lô vừa nhập để tạo lưới tem mã vạch.
   - Vào mục **Tồn kho & Lô** (`/kho/ton-kho`) hoặc **Tra cứu mã vạch** (`/thuoc/tra-cuu-ma-vach`), quét mã vạch của thuốc để xem danh sách các lô đang tồn được sắp xếp tự động theo thứ tự hạn dùng tăng dần (**FEFO**).
4. **Bước 4 – Bán hàng POS thông thường & Xem trước phân lô FEFO (`ds01@nhathuoc.vn` / `123456`):**
   - Đăng nhập `ds01@nhathuoc.vn`. Vào mục **Bán hàng tại quầy (POS)** (`/banhang/pos`), quét mã vạch thuốc không kê đơn (OTC).
   - Hệ thống tự động thêm vào giỏ và hiển thị bảng **Phân bổ lô tự động theo FEFO** (`sp_XemTruocFEFO`). Chọn phương thức thanh toán (`Tiền mặt`, `Chuyển khoản QR`, hoặc `Thẻ ngân hàng`; với `Tiền mặt` nhập **Khách đưa** hoặc chọn mệnh giá nhanh để hệ thống tự tính **Tiền thối lại khách**), sau đó bấm **Thanh toán & In hóa đơn (`F9`)** (`sp_BanHang`) → mở cửa sổ **In hóa đơn**.
5. **Bước 5 – Kiểm soát bán thuốc kê đơn Rx (`ds01@nhathuoc.vn`):**
   - Tại **Bán hàng POS** (`/banhang/pos`), quét một thuốc kháng sinh kê đơn (Rx) và bấm Thanh toán khi chưa gắn đơn thuốc → Hệ thống chặn ngay với mã lỗi **`50003`** (*Thiếu đơn thuốc kê đơn*).
   - Sang mục **Đơn thuốc** (`/donthuoc`), tiếp nhận đơn thuốc mới cho khách hàng, kê thuốc Rx số lượng `2`. Quay lại **Bán hàng POS**, tìm khách hàng theo SĐT, chọn đơn thuốc vừa tạo và bấm Thanh toán → Giao dịch thành công, số lượng đã bán trong đơn thuốc tự động cập nhật.
6. **Bước 6 – Cảnh báo chủ động & Lập phiếu điều chuyển/xuất hủy (`tk01@nhathuoc.vn`):**
   - Đăng nhập `tk01@nhathuoc.vn`, vào mục **Cảnh báo kho** (`/canhbao`) xem 3 tab: *Cận hạn & Hết hạn*, *Tồn thấp dưới định mức*, *Tồn chậm luân chuyển*.
   - Nhấn nút **Xuất hủy ngay** hoặc vào mục **Điều chuyển & Xuất hủy** (`/kho/phieu-kho`) để lập phiếu điều chuyển/xuất hủy → phiếu được tạo ở trạng thái **`Đang chờ duyệt (CHO_DUYET)`**.
7. **Bước 7 – Duyệt phiếu kho & Hủy giao dịch (`ql01@nhathuoc.vn` / `123456`):**
   - Đăng nhập `ql01@nhathuoc.vn`. Vào mục **Duyệt phiếu kho** (`/kho/duyet-phieu`), bấm **Duyệt** phiếu xuất hủy/điều chuyển vừa lập (`sp_DuyetPhieuKho`) → tồn kho tự động trừ/cộng chính xác và ghi nhận vào `BienDongKho`.
   - Vào mục **Hủy giao dịch** (`/banhang/huy-giao-dich`), chọn một giao dịch mới phát sinh trong ngày, nhập lý do hủy để hoàn trả tồn kho về đúng các lô ban đầu (`sp_HuyGiaoDich`).
8. **Bước 8 – Phân tích nhu cầu theo khu vực, Xuất Excel (`.xlsx`) & Đối soát tồn kho (`qt01@nhathuoc.vn` / `123456`):**
   - Đăng nhập `qt01@nhathuoc.vn`. Vào mục **Dashboard toàn chuỗi** (`/thongke/dashboard-chuoi`) xem **Bảng nhiệt (Heatmap) Nhu cầu Khu vực × Nhóm thuốc**, biểu đồ **Xu hướng doanh thu theo tháng kèm % tăng trưởng**, **Top thuốc bán chạy** và **Hiệu suất nhà thuốc**. Nhấn **Xuất Excel Nhu cầu khu vực**, **Xuất Excel Hiệu suất NT** hoặc **Xuất Excel Top thuốc** để tải file báo cáo `.xlsx` chuẩn thực tế.
   - Vào mục **Nhật ký & Đối soát** (`/quantri/nhat-ky-doi-soat`), nhấn **Chạy đối soát ngay** (`sp_KiemTraDoiSoatTonKho`) để kiểm tra tính toàn vẹn giữa bảng `TonKho` và tổng lịch sử `BienDongKho`.

---

## 8. Ghi chú Kỹ thuật & Giả định

- **Đồng bộ quyền DB cho tài khoản ứng dụng (`is009_app`):** Theo thiết kế bảo mật, mọi thao tác nghiệp vụ phát sinh giao dịch/tồn kho đều đi qua Stored Procedure (`GRANT EXECUTE`). Đối với các màn hình quản trị danh mục đơn thuần dùng Spring Data JPA (`Thuoc`, `NhomThuoc`, `NhaCungCap`, `KhuVuc`, `NhaThuoc`, `NguoiDung`, `KhachHang`, `DinhMucTon`, `CauHinhHeThong`), tài khoản `is009_app` được cấp quyền `SELECT, INSERT, UPDATE` trên các bảng danh mục đó để phục vụ CRUD trực tiếp.
- **Dữ liệu cá nhân giả lập:** Toàn bộ tên khách hàng, bác sĩ, số điện thoại và địa chỉ trong hệ thống là dữ liệu giả lập tiếng Việt có dấu chuẩn UTF-8 phục vụ học tập, đồng thời số điện thoại luôn được che tự động (`091****386`) trên danh sách theo quy định bảo vệ dữ liệu.
