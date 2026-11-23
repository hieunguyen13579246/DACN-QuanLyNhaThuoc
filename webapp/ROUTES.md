# Danh mục Đường dẫn & Phân quyền (ROUTES & RBAC) – Hệ thống IS009

Tài liệu này liệt kê toàn bộ các đường dẫn giao diện (Web MVC View Routes M01–M23) và các điểm cuối REST API (`/api/**`) của hệ thống **IS009 – Hệ thống quản lý chuỗi nhà thuốc và phân tích nhu cầu dược phẩm theo khu vực (có quét mã vạch)** cùng phân quyền RBAC chi tiết theo 4 vai trò:
- `DUOCSI`: Dược sĩ bán hàng tại nhà thuốc
- `THUKHO`: Thủ kho nhà thuốc
- `QUANLY`: Quản lý nhà thuốc
- `QUANTRI`: Quản trị viên toàn chuỗi

---

## 1. Đường dẫn Giao diện Web (M01 – M23)

| Mã màn hình | Phương thức | Đường dẫn (URL) | Controller xử lý | Template Thymeleaf | Vai trò được phép | Phạm vi dữ liệu |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- |
| **M01** | `GET` | `/auth/login` | `AuthController` | `auth/login.html` | Công khai (`permitAll`) | — |
| **M01** | `POST` | `/auth/login` | Spring Security | — | Công khai (`permitAll`) | Xác thực BCrypt & kiểm tra trạng thái tài khoản/nhà thuốc |
| **M01** | `POST` | `/auth/logout` | Spring Security | — | Đã đăng nhập | Xóa session |
| **M02** | `GET` | `/` | `HomeController` | `home/index.html` | `DUOCSI`, `THUKHO`, `QUANLY`, `QUANTRI` | Nhà thuốc của mình (`QUANTRI`: toàn chuỗi) |
| **M03** | `GET` | `/banhang/pos` | `BanHangController` | `banhang/pos.html` | `DUOCSI`, `QUANLY` | Nhà thuốc của mình |
| **M03 (In)** | `GET` | `/banhang/hoa-don/{id}/in` | `BanHangController` | `banhang/hoa-don-in.html` | `DUOCSI`, `QUANLY`, `QUANTRI` | Nhà thuốc của mình (`QUANTRI`: toàn chuỗi) |
| **M04** | `GET` | `/donthuoc` | `DonThuocController` | `donthuoc/danh-sach.html` | `DUOCSI`, `QUANLY`, `QUANTRI` | Toàn hệ thống (tự động gọi `sp_CapNhatDonHetHieuLuc` trước khi liệt kê) |
| **M05** | `GET` | `/khachhang` | `KhachHangController` | `khachhang/danh-sach.html` | `DUOCSI`, `QUANLY`, `QUANTRI` | Toàn chuỗi (che số điện thoại `090****567` ở danh sách) |
| **M06** | `GET` | `/thuoc/tra-cuu-ma-vach` | `ThuocController` | `thuoc/tra-cuu-ma-vach.html` | `DUOCSI`, `THUKHO`, `QUANLY`, `QUANTRI` | Tồn kho FEFO theo nhà thuốc đang chọn/trực thuộc |
| **M07** | `GET` | `/banhang/lich-su` | `BanHangController` | `banhang/lich-su.html` | `DUOCSI`, `QUANLY`, `QUANTRI` | Nhà thuốc của mình (`QUANTRI`: toàn chuỗi) |
| **M08** | `GET` | `/kho/nhap-kho` | `KhoController` | `kho/nhap-kho.html` | `THUKHO`, `QUANLY` | Nhà thuốc của mình |
| **M09** | `GET` | `/kho/ton-kho` | `KhoController` | `kho/ton-kho.html` | `DUOCSI`, `THUKHO`, `QUANLY`, `QUANTRI` | Nhà thuốc của mình (`QUANTRI`: toàn chuỗi) |
| **M10** | `GET` | `/kho/phieu-kho` | `KhoController` | `kho/phieu-kho.html` | `THUKHO`, `QUANLY`, `QUANTRI` | Nhà thuốc của mình (`QUANTRI`: toàn chuỗi) |
| **M11** | `GET` | `/kho/in-tem` | `KhoController` | `kho/in-tem.html` | `THUKHO`, `QUANLY`, `QUANTRI` | Nhà thuốc của mình (`QUANTRI`: toàn chuỗi) |
| **M12** | `GET` | `/kho/duyet-phieu` | `KhoController` | `kho/duyet-phieu.html` | `QUANLY`, `QUANTRI` | `QUANLY`: phiếu có nhà thuốc nguồn là nhà thuốc mình; `QUANTRI`: toàn chuỗi |
| **M13** | `GET` | `/banhang/huy-giao-dich` | `BanHangController` | `banhang/huy-giao-dich.html` | `QUANLY` | Chỉ `QUANLY` của đúng nhà thuốc phát sinh giao dịch |
| **M14** | `GET` | `/canhbao` | `CanhBaoController` | `canhbao/danh-sach.html` | `DUOCSI`, `THUKHO`, `QUANLY`, `QUANTRI` | Nhà thuốc của mình (`QUANTRI`: toàn chuỗi) |
| **M15** | `GET` | `/kho/dinh-muc` | `KhoController` | `kho/dinh-muc.html` | `QUANLY`, `QUANTRI` | Nhà thuốc của mình (`QUANTRI`: toàn chuỗi) |
| **M16** | `GET` | `/thongke/de-xuat-nhap` | `ThongKeController` | `thongke/de-xuat-nhap.html` | `THUKHO`, `QUANLY`, `QUANTRI` | Nhà thuốc của mình (`QUANTRI`: toàn chuỗi) |
| **M17** | `GET` | `/thongke/dashboard-nha-thuoc` | `ThongKeController` | `thongke/dashboard-nha-thuoc.html` | `QUANLY`, `QUANTRI` | Nhà thuốc của mình (`QUANTRI`: chọn nhà thuốc bất kỳ) |
| **M18** | `GET` | `/thuoc/quan-ly` | `ThuocController` | `thuoc/quan-ly.html` | `QUANTRI` | Danh mục dùng chung toàn chuỗi |
| **M19** | `GET` | `/quantri/danh-muc-chuoi` | `HomeController` | `quantri/danh-muc-chuoi.html` | `QUANTRI` | Danh mục NCC, Khu vực, Nhà thuốc toàn chuỗi |
| **M20** | `GET` | `/quantri/nguoi-dung` | `NguoiDungController` | `quantri/nguoi-dung.html` | `QUANTRI` | Quản lý tài khoản người dùng toàn chuỗi |
| **M21** | `GET` | `/quantri/cau-hinh` | `CauHinhController` | `quantri/cau-hinh.html` | `QUANTRI` | Tham số hệ thống (`CauHinhHeThong`) |
| **M22** | `GET` | `/quantri/nhat-ky-doi-soat` | `HomeController` | `quantri/nhat-ky-doi-soat.html` | `QUANTRI` | Nhật ký `AuditLog`, `BienDongKho` và đối soát `sp_KiemTraDoiSoatTonKho` |
| **M23** | `GET` | `/thongke/dashboard-chuoi` | `ThongKeController` | `thongke/dashboard-chuoi.html` | `QUANLY`, `QUANTRI` | `QUANLY`: phạm vi nhà thuốc mình; `QUANTRI`: toàn chuỗi |

---

## 2. Đường dẫn REST API (`/api/**`)

Mọi phản hồi JSON từ REST API đều được đóng gói chuẩn hóa qua `ApiResponse<T>`:
```json
{
  "thanhCong": true,
  "maLoi": null,
  "thongBao": "Thao tác thành công",
  "huongDanSua": null,
  "duLieu": { ... }
}
```

### 2.1. Module Mã vạch & Tra cứu thuốc (`barcode`, `thuoc`)

| Phương thức | Đường dẫn API | Controller | Vai trò được phép | Mô tả nghiệp vụ / Stored Procedure |
| :---: | :--- | :--- | :--- | :--- |
| `GET` | `/api/barcode/validate-ean13?code=...` | `BarcodeApiController` | Đã đăng nhập | Kiểm tra tính hợp lệ EAN-13 (13 chữ số + checksum chuẩn GS1) |
| `GET` | `/api/barcode/parse-gs1?raw=...` | `BarcodeApiController` | Đã đăng nhập | Phân tích chuỗi GS1-128 / DataMatrix (AI `01` GTIN, `17` Hạn dùng, `10` Số lô, `21` Serial) |
| `GET` | `/api/thuoc/ma-vach/{code}` | `ThuocApiController` | `DUOCSI`, `THUKHO`, `QUANLY`, `QUANTRI` | Tra cứu thuốc theo mã vạch kèm danh sách lô tồn khả dụng sắp xếp theo FEFO |
| `GET` | `/api/thuoc/tim-kiem?tuKhoa=...` | `ThuocApiController` | `DUOCSI`, `THUKHO`, `QUANLY`, `QUANTRI` | Tìm kiếm thuốc theo mã, tên thuốc, hoạt chất |
| `POST` | `/api/thuoc` | `ThuocApiController` | `QUANTRI` | Thêm mới hoặc cập nhật thuốc trong danh mục chuỗi |
| `POST` | `/api/thuoc/nhom` | `ThuocApiController` | `QUANTRI` | Thêm mới hoặc cập nhật nhóm thuốc |
| `POST` | `/api/thuoc/ma-vach` | `ThuocApiController` | `QUANTRI` | Gán mã vạch mới cho thuốc qua `sp_ThemMaVachThuoc` (kiểm tra trùng & EAN-13) |

### 2.2. Module Bán hàng POS & Giao dịch (`banhang`)

| Phương thức | Đường dẫn API | Controller | Vai trò được phép | Mô tả nghiệp vụ / Stored Procedure |
| :---: | :--- | :--- | :--- | :--- |
| `POST` | `/api/banhang/xem-truoc-fefo` | `BanHangApiController` | `DUOCSI`, `QUANLY` | Xem trước phân bổ lô theo nguyên tắc FEFO qua `sp_XemTruocFEFO` |
| `POST` | `/api/banhang/thanh-toan` | `BanHangApiController` | `DUOCSI`, `QUANLY` | Thanh toán hóa đơn bán lẻ, trừ tồn FEFO, ghi nhận đơn thuốc qua `sp_BanHang` |
| `GET` | `/api/banhang/giao-dich/{id}` | `BanHangApiController` | `DUOCSI`, `QUANLY`, `QUANTRI` | Xem chi tiết giao dịch và các dòng lô thuốc đã xuất |
| `POST` | `/api/banhang/huy-giao-dich` | `BanHangApiController` | `QUANLY` | Hủy giao dịch trong thời hạn cho phép và hoàn trả tồn kho qua `sp_HuyGiaoDich` |

### 2.3. Module Đơn thuốc & Khách hàng (`donthuoc`, `khachhang`)

| Phương thức | Đường dẫn API | Controller | Vai trò được phép | Mô tả nghiệp vụ / Stored Procedure |
| :---: | :--- | :--- | :--- | :--- |
| `GET` | `/api/donthuoc/{id}` | `DonThuocApiController` | `DUOCSI`, `QUANLY`, `QUANTRI` | Xem chi tiết đơn thuốc và số lượng kê / đã bán / còn lại của từng thuốc |
| `GET` | `/api/donthuoc/hieu-luc?khachHangId=...` | `DonThuocApiController` | `DUOCSI`, `QUANLY` | Liệt kê đơn thuốc còn hiệu lực để gắn vào giỏ hàng POS |
| `POST` | `/api/donthuoc` | `DonThuocApiController` | `DUOCSI`, `QUANLY` | Tiếp nhận đơn thuốc mới qua `sp_TaoDonThuoc` |
| `POST` | `/api/donthuoc/{id}/huy` | `DonThuocApiController` | `DUOCSI`, `QUANLY` | Hủy đơn thuốc chưa phát sinh giao dịch qua `sp_HuyDonThuoc` |
| `GET` | `/api/khachhang/tim-sdt?sdt=...` | `KhachHangApiController` | `DUOCSI`, `QUANLY`, `QUANTRI` | Tìm nhanh khách hàng theo số điện thoại tại quầy POS |
| `POST` | `/api/khachhang` | `KhachHangApiController` | `DUOCSI`, `QUANLY`, `QUANTRI` | Thêm mới hoặc cập nhật khách hàng |

### 2.4. Module Kho & Định mức (`kho`)

| Phương thức | Đường dẫn API | Controller | Vai trò được phép | Mô tả nghiệp vụ / Stored Procedure |
| :---: | :--- | :--- | :--- | :--- |
| `GET` | `/api/kho/lo-kha-dung?nhaThuocId=...` | `KhoApiController` | `THUKHO`, `QUANLY`, `QUANTRI` | Lấy danh sách lô còn tồn để lập phiếu điều chuyển/xuất hủy hoặc in tem |
| `GET` | `/api/kho/phieu/{id}` | `KhoApiController` | `THUKHO`, `QUANLY`, `QUANTRI` | Xem chi tiết phiếu kho và các dòng lô thuốc |
| `POST` | `/api/kho/nhap-kho` | `KhoApiController` | `THUKHO`, `QUANLY` | Nhập kho từ nhà cung cấp qua `sp_NhapKho` |
| `POST` | `/api/kho/dieu-chuyen` | `KhoApiController` | `THUKHO`, `QUANLY` | Lập phiếu điều chuyển liên nhà thuốc (trạng thái `CHO_DUYET`) qua `sp_TaoPhieuDieuChuyen` |
| `POST` | `/api/kho/xuat-huy` | `KhoApiController` | `THUKHO`, `QUANLY` | Lập phiếu xuất hủy lô thuốc (trạng thái `CHO_DUYET`) qua `sp_TaoPhieuXuatHuy` |
| `POST` | `/api/kho/duyet-phieu` | `KhoApiController` | `QUANLY`, `QUANTRI` | Duyệt hoặc từ chối phiếu điều chuyển/xuất hủy qua `sp_DuyetPhieuKho` |
| `POST` | `/api/kho/phieu/{id}/huy` | `KhoApiController` | `THUKHO`, `QUANLY`, `QUANTRI` | Hủy phiếu kho đang chờ duyệt qua `sp_HuyPhieuKho` |
| `POST` | `/api/kho/dinh-muc` | `KhoApiController` | `QUANLY`, `QUANTRI` | Cập nhật định mức tồn tối thiểu / tối đa tại nhà thuốc |
| `POST` | `/api/kho/doi-soat` | `KhoApiController` | `QUANTRI` | Chạy đối soát tồn kho toàn chuỗi qua `sp_KiemTraDoiSoatTonKho` |

### 2.5. Module Cảnh báo & Thống kê (`canhbao`, `thongke`)

| Phương thức | Đường dẫn API | Controller | Vai trò được phép | Mô tả nghiệp vụ / Stored Procedure / View |
| :---: | :--- | :--- | :--- | :--- |
| `GET` | `/api/canhbao/tong-hop` | `CanhBaoApiController` | Đã đăng nhập | Tổng hợp số lượng cảnh báo từ `vw_CanhBaoHanDung`, `vw_CanhBaoTonThap`, `vw_TonChamLuanChuyen` |
| `GET` | `/api/thongke/nhu-cau-khu-vuc` | `ThongKeApiController` | `QUANLY`, `QUANTRI` | Dữ liệu bảng nhiệt Khu vực × Nhóm thuốc qua `sp_ThongKeNhuCauKhuVuc` |
| `GET` | `/api/thongke/xu-huong` | `ThongKeApiController` | `QUANLY`, `QUANTRI` | Dữ liệu xu hướng doanh thu theo tháng kèm % tăng trưởng qua `sp_ThongKeXuHuong` |
| `GET` | `/api/thongke/top-thuoc` | `ThongKeApiController` | `QUANLY`, `QUANTRI` | Top thuốc bán chạy theo số lượng hoặc doanh thu qua `sp_ThongKeTopThuoc` |
| `GET` | `/api/thongke/hieu-suat` | `ThongKeApiController` | `QUANLY`, `QUANTRI` | Hiệu suất các nhà thuốc trong chuỗi qua `sp_ThongKeHieuSuatNhaThuoc` |
| `GET` | `/api/thongke/chuoi/xuat-csv?loai=nhu-cau` (`/api/thongke/excel/nhu-cau-khu-vuc`) | `ThongKeApiController` | `QUANLY`, `QUANTRI` | Xuất file Excel `.xlsx` chuẩn thực tế bảng nhu cầu dược phẩm theo khu vực |
| `GET` | `/api/thongke/chuoi/xuat-csv?loai=hieu-suat` (`/api/thongke/excel/hieu-suat`) | `ThongKeApiController` | `QUANLY`, `QUANTRI` | Xuất file Excel `.xlsx` chuẩn thực tế hiệu suất chuỗi nhà thuốc |
| `GET` | `/api/thongke/chuoi/xuat-csv?loai=top-thuoc` (`/api/thongke/excel/top-thuoc`) | `ThongKeApiController` | `DUOCSI`, `THUKHO`, `QUANLY`, `QUANTRI` | Xuất file Excel `.xlsx` chuẩn thực tế xếp hạng top thuốc bán chạy |
| `GET` | `/api/thongke/xuat-csv?loai=de-xuat` (`/api/thongke/excel/de-xuat-nhap`) | `ThongKeApiController` | `THUKHO`, `QUANLY`, `QUANTRI` | Xuất file Excel `.xlsx` chuẩn thực tế danh sách đề xuất nhập hàng `sp_DeXuatNhapHang` |

### 2.6. Module Quản trị Danh mục chuỗi (`khuvuc`, `nhathuoc`, `nhacungcap`, `nguoidung`, `cauhinh`)

| Phương thức | Đường dẫn API | Controller | Vai trò được phép | Mô tả nghiệp vụ |
| :---: | :--- | :--- | :--- | :--- |
| `POST` | `/api/khuvuc` | `KhuVucApiController` | `QUANTRI` | Thêm mới / cập nhật khu vực địa lý |
| `POST` | `/api/nhathuoc` | `NhaThuocApiController` | `QUANTRI` | Thêm mới / cập nhật nhà thuốc trong chuỗi |
| `POST` | `/api/nhacungcap` | `NhaCungCapApiController` | `QUANTRI` | Thêm mới / cập nhật nhà cung cấp dược phẩm |
| `POST` | `/api/nguoidung` | `NguoiDungApiController` | `QUANTRI` | Tạo người dùng mới hoặc cập nhật vai trò, nhà thuốc, trạng thái |
| `POST` | `/api/nguoidung/{id}/dat-lai-mat-khau` | `NguoiDungApiController` | `QUANTRI` | Đặt lại mật khẩu người dùng (mã hóa BCrypt cost 10) |
| `PUT` | `/api/cauhinh` | `CauHinhApiController` | `QUANTRI` | Cập nhật giá trị cấu hình hệ thống (`CauHinhHeThong`) |
