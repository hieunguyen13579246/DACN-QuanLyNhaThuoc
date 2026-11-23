# 07 – Class Diagram

## 1. Class Diagram Miền Dữ liệu

*(20 lớp tương ứng 20 bảng, phương thức nghiệp vụ chính, enum)*

```plantuml
@startuml CD_Domain
skinparam classAttributeIconSize 0
skinparam style strictuml

' ========== ENUM ==========
enum VaiTro {
  DUOCSI
  THUKHO
  QUANLY
  QUANTRI
}

enum LoaiPhieu {
  NHAP
  XUAT_HUY
  DIEU_CHUYEN
}

enum TrangThaiPhieu {
  CHO_DUYET
  HOAN_THANH
  TU_CHOI
  DA_HUY
}

enum LoaiMaVach {
  EAN13
  GTIN14
  CODE128
  NOI_BO
}

enum LoaiBienDong {
  NHAP
  BAN
  HOAN_KHO
  DIEU_CHUYEN_XUAT
  DIEU_CHUYEN_NHAP
  XUAT_HUY
  DIEU_CHINH
}

enum MucCanhBao {
  DA_HET_HAN
  MUC3_30_NGAY
  MUC2_60_NGAY
  MUC1_90_NGAY
}

enum TrangThaiGD {
  HOAN_THANH
  DA_HUY
}

enum TrangThaiDon {
  CHUA_BAN
  BAN_MOT_PHAN
  DA_BAN_HET
  HET_HIEU_LUC
  HUY
}

' ========== MÔ HÌNH MIỀN ==========
class KhuVuc {
  +int id
  +String ma
  +String ten
  +String cap
  +Integer khuVucChaId
  +Integer danSo
  --
  +int tinhDanSoTinh()
}

class NhaThuoc {
  +int id
  +String ma
  +String ten
  +String diaChi
  +int khuVucId
  +String soDienThoai
  +String trangThai
  +LocalDate ngayKhaiTruong
}

class NguoiDung {
  +int id
  +String hoTen
  +String email
  +String passwordHash
  +VaiTro vaiTro
  +Integer nhaThuocId
  +String soChungChiHanhNghe
  +boolean kichHoat
  +Instant createdAt
  --
  +boolean coQuyenDuyetPhieu()
  +boolean thuocNhaThuoc(int nhaThuocId)
}

class NhomThuoc {
  +int id
  +String ma
  +String ten
}

class Thuoc {
  +int id
  +String ma
  +String ten
  +String hoatChat
  +String hamLuong
  +String dangBaoChe
  +String donViTinh
  +int nhomThuocId
  +String nhaSanXuat
  +boolean canKeDon
  +BigDecimal giaBanLe
  +String trangThai
  +String moTa
  --
  +boolean dangBan()
}

class MaVachThuoc {
  +int id
  +int thuocId
  +String maVach
  +LoaiMaVach loaiMaVach
  +boolean laMaChinh
  +Instant createdAt
}

class NhaCungCap {
  +int id
  +String ma
  +String ten
  +String diaChi
  +String soDienThoai
  +String maSoThue
  +String trangThai
}

class LoThuoc {
  +int id
  +int thuocId
  +String soLo
  +LocalDate ngaySanXuat
  +LocalDate hanDung
  +Integer nhaCungCapId
  +BigDecimal giaNhap
  +Instant createdAt
  --
  +boolean daHetHan()
  +int soNgayConLai()
  +MucCanhBao mucCanhBao(CauHinh cfg)
}

class TonKho {
  +int nhaThuocId
  +int loThuocId
  +int soLuong
  +Instant capNhatLuc
  --
  +boolean duTon(int canBan)
}

class DinhMucTon {
  +int id
  +int nhaThuocId
  +int thuocId
  +int tonToiThieu
  +Integer tonToiDa
  +int soNgayGiaoHang
  --
  +boolean tonThap(int tonHienTai)
}

class PhieuKho {
  +int id
  +String maPhieu
  +LoaiPhieu loaiPhieu
  +int nhaThuocId
  +Integer nhaThuocDenId
  +Integer nhaCungCapId
  +String lyDo
  +String ghiChu
  +TrangThaiPhieu trangThai
  +int nguoiLapId
  +Integer nguoiDuyetId
  +Instant thoiGianLap
  +Instant thoiGianHoanThanh
  +BigDecimal tongGiaTri
  --
  +boolean coTheHuy()
}

class ChiTietPhieuKho {
  +int id
  +int phieuKhoId
  +int loThuocId
  +int soLuong
  +BigDecimal donGia
}

class BienDongKho {
  +long id
  +int nhaThuocId
  +int loThuocId
  +LoaiBienDong loaiBienDong
  +int soLuongThayDoi
  +int soDuSau
  +String chungTuLoai
  +int chungTuId
  +Integer nguoiThucHienId
  +Instant thoiGian
}

class KhachHang {
  +int id
  +String ma
  +String hoTen
  +String soDienThoai
  +String gioiTinh
  +Short namSinh
  +Integer khuVucId
  +String diaChi
  +String ghiChuDiUng
  +Instant createdAt
  --
  +String anSoDienThoai()
}

class DonThuoc {
  +int id
  +String maDon
  +int khachHangId
  +int nhaThuocId
  +String tenBacSi
  +String coSoKham
  +String chanDoan
  +LocalDate ngayKeDon
  +int soNgayHieuLuc
  +TrangThaiDon trangThai
  +int nguoiTiepNhanId
  +Instant createdAt
  --
  +LocalDate ngayHetHieuLuc()
  +boolean conHieuLuc()
  +void capNhatTrangThai()
}

class ChiTietDonThuoc {
  +int id
  +int donThuocId
  +int thuocId
  +int soLuongKe
  +int soLuongDaBan
  +String lieuDung
  --
  +int soLuongConDuocBan()
}

class GiaoDich {
  +int id
  +String maGiaoDich
  +int nhaThuocId
  +int nguoiBanId
  +Integer khachHangId
  +Integer donThuocId
  +Instant thoiGian
  +BigDecimal tongTien
  +BigDecimal giamGia
  +BigDecimal thanhTien
  +String phuongThucThanhToan
  +TrangThaiGD trangThai
  +String lyDoHuy
  +Integer nguoiHuyId
  +Instant thoiGianHuy
  +String ghiChu
  --
  +boolean coTheHuy(int gioChoPhep)
}

class ChiTietGiaoDich {
  +int id
  +int giaoDichId
  +int thuocId
  +int loThuocId
  +int soLuong
  +BigDecimal donGia
  +BigDecimal giaVon
  +BigDecimal thanhTien
  --
  +BigDecimal loiNhuan()
}

class CauHinh {
  +String khoa
  +String giaTri
  +String moTa
  --
  +int toInt()
}

class AuditLog {
  +long id
  +Integer nguoiDungId
  +String hanhDong
  +String doiTuong
  +Integer doiTuongId
  +String duLieuCu
  +String duLieuMoi
  +Instant createdAt
}

' ========== QUAN HỆ ==========
KhuVuc "1" *-- "0..*" NhaThuoc
KhuVuc "1" o-- "0..*" KhuVuc : cha
NhaThuoc "1" *-- "1..*" NguoiDung
Thuoc "0..*" --> "1" NhomThuoc
Thuoc "1" *-- "1..*" MaVachThuoc
LoThuoc "0..*" --> "1" Thuoc
LoThuoc "0..*" --> "0..1" NhaCungCap
TonKho --> NhaThuoc
TonKho --> LoThuoc
DinhMucTon --> NhaThuoc
DinhMucTon --> Thuoc
PhieuKho "0..*" --> "1" NhaThuoc
PhieuKho "1" *-- "1..*" ChiTietPhieuKho
ChiTietPhieuKho --> LoThuoc
BienDongKho --> NhaThuoc
BienDongKho --> LoThuoc
KhachHang "0..*" --> "0..1" KhuVuc
DonThuoc "0..*" --> "1" KhachHang
DonThuoc "1" *-- "1..*" ChiTietDonThuoc
ChiTietDonThuoc --> Thuoc
GiaoDich "0..*" --> "1" NhaThuoc
GiaoDich "1" *-- "1..*" ChiTietGiaoDich
GiaoDich --> "0..1" DonThuoc
GiaoDich --> "0..1" KhachHang
ChiTietGiaoDich --> LoThuoc
ChiTietGiaoDich --> Thuoc
@enduml
```

---

## 2. Class Diagram Lớp Ứng dụng

```plantuml
@startuml CD_Application
skinparam classAttributeIconSize 0
skinparam packageStyle frame

package "BanHang" {
  class BanHangController {
    +ResponseEntity quetMaVach(String maVach, int nhaThuocId)
    +ResponseEntity xacNhanBan(GioHangDTO dto)
    +ResponseEntity huyGiaoDich(int id, HuyGDRequest req)
    +ResponseEntity lichSuGiaoDich(int nhaThuocId, LocalDate tu, LocalDate den)
  }

  class BanHangService {
    -TonKhoRepository tonKhoRepo
    -GiaoDichRepository gdRepo
    -DonThuocService donThuocSvc
    -BarcodeService barcodeSvc
    --
    +LoFEFODTO timLoFEFO(int nhaThuocId, int thuocId)
    +GiaoDichDTO taoGiaoDich(GioHangDTO dto)
    +void huyGiaoDich(int giaoDichId, int quanLyId, String lyDo)
    -void kiemTraBR01(int donThuocId, int thuocId, int soLuong)
    -void kiemTraBR10(GiaoDich gd, int quanLyId)
  }

  class GioHangDTO {
    +int nhaThuocId
    +int nguoiBanId
    +Integer khachHangId
    +Integer donThuocId
    +String phuongThucThanhToan
    +BigDecimal giamGia
    +List<GioHangItemDTO> items
  }

  class GioHangItemDTO {
    +int thuocId
    +int loThuocId
    +int soLuong
    +BigDecimal donGia
  }
}

package "Kho" {
  class KhoController {
    +ResponseEntity taoPhieuNhap(PhieuNhapDTO dto)
    +ResponseEntity hoanThanhNhap(int id, NhapKhoJSON json)
    +ResponseEntity taoPhieuDieuChuyen(PhieuDCDTO dto)
    +ResponseEntity duyetPhieu(int id, DuyetRequest req)
    +ResponseEntity tuChoiPhieu(int id, TuChoiRequest req)
    +ResponseEntity taoPhieuXuatHuy(PhieuXHDTO dto)
    +ResponseEntity xemTonKho(int nhaThuocId)
  }

  class KhoService {
    -TonKhoRepository tonKhoRepo
    -BienDongKhoRepository bdkRepo
    -PhieuKhoRepository phieuRepo
    --
    +void nhapKho(int phieuId, List<NhapKhoItem> items)
    +void duyetDieuChuyen(int phieuId, int quanLyId)
    +void duyetXuatHuy(int phieuId, int quanLyId)
    -void capNhatTonKho(int nhaThuocId, int loThuocId, int delta, LoaiBienDong loai, int chungTuId)
  }
}

package "Barcode" {
  class BarcodeService {
    -MaVachThuocRepository mvtRepo
    --
    +ThuocDTO timTheoMaVach(String maVach)
    +Gs1Data parseGs1(String rawBarcode)
    +void inTemNoiBo(int loThuocId, int nhaThuocId)
  }

  class Ean13Validator {
    +{static} boolean isValid(String ean13)
    +{static} char tinhCheckDigit(String ean12)
  }

  class Gs1Parser {
    +Gs1Data parse(String gs1String)
    -String extractAI(String gs1, String ai)
  }

  class Gs1Data {
    +String gtin
    +LocalDate hanDung
    +String soLo
  }
}

package "ThongKe" {
  class ThongKeController {
    +ResponseEntity nhuCauKhuVuc(int khuVucId, LocalDate tu, LocalDate den, Integer nhomThuocId)
    +ResponseEntity xuHuong(LocalDate tu, LocalDate den, Integer nhaThuocId)
    +ResponseEntity deXuatNhapHang(int nhaThuocId)
    +ResponseEntity canhBaoHanDung(int nhaThuocId)
    +ResponseEntity canhBaoTonThap(int nhaThuocId)
  }

  class ThongKeService {
    -SqlServerTemplate jdbcTemplate
    --
    +List<NhuCauKhuVucDTO> thongKeNhuCau(ThongKeParam params)
    +List<XuHuongDTO> thongKeXuHuong(ThongKeParam params)
    +List<DeXuatNhapHangDTO> tinhDeXuat(int nhaThuocId)
    -BigDecimal banTrungBinhNgay(int thuocId, int nhaThuocId, int soNgay)
  }
}

package "Common" {
  class ErrorCodeMapper {
    +{static} ResponseEntity mapException(NghiepVuException ex)
  }

  class NghiepVuException {
    +String errorCode
    +String message
    +NghiepVuException(String code, String msg)
  }

  interface Repository<T, ID> {
    +T findById(ID id)
    +void save(T entity)
    +void delete(ID id)
  }

  class TonKhoRepository {
    +TonKho timTheoNhaThuocVaLo(int nhaThuocId, int loThuocId)
    +List<TonKho> timTheoNhaThuoc(int nhaThuocId)
    +void capNhat(TonKho tonKho)
  }

  class GiaoDichRepository {
    +GiaoDich timById(int id)
    +void luu(GiaoDich gd)
  }

  class MaVachThuocRepository {
    +MaVachThuoc timTheoMaVach(String maVach)
  }

  class BienDongKhoRepository {
    +void ghi(BienDongKho bdk)
    +List<BienDongKho> timTheoLoVaNhaThuoc(int nhaThuocId, int loThuocId)
  }
}

BanHangController --> BanHangService
BanHangController --> ErrorCodeMapper
BanHangService --> TonKhoRepository
BanHangService --> GiaoDichRepository
BanHangService --> BarcodeService
BanHangService ..> NghiepVuException : throws
KhoController --> KhoService
KhoService --> TonKhoRepository
KhoService --> BienDongKhoRepository
BarcodeService --> Ean13Validator
BarcodeService --> Gs1Parser
BarcodeService --> MaVachThuocRepository
ThongKeController --> ThongKeService
@enduml
```

---

## 3. Bảng Trách nhiệm Lớp

| Lớp | Trách nhiệm | Ghi chú |
|-----|-------------|---------|
| BanHangController | Nhận request HTTP, validate đầu vào, trả response | REST endpoint |
| BanHangService | Orchestrate luồng bán hàng, áp dụng BR02–BR15 | Transaction boundary |
| KhoController | Endpoint quản lý phiếu kho | — |
| KhoService | Nghiệp vụ nhập/điều chuyển/xuất hủy, cập nhật tồn | Transaction boundary |
| BarcodeService | Tra cứu mã vạch, parse GS1, in tem | Wrap BR06 |
| Ean13Validator | Validate EAN13 (13 ký tự, checkdigit) | Tĩnh, testable |
| Gs1Parser | Parse chuỗi GS1/DataMatrix → GTIN, lô, hạn | Tĩnh, testable |
| ThongKeController | Endpoint báo cáo K1–K7 | Delegate sang SP |
| ThongKeService | Gọi stored procedure, tính đề xuất K5 | Read-only |
| TonKhoRepository | CRUD + lock TonKho | UPDLOCK, ROWLOCK |
| BienDongKhoRepository | Append-only BienDongKho | INSERT only |
| ErrorCodeMapper | Map exception → HTTP response code + JSON | Tập trung xử lý lỗi |
| NghiepVuException | Lỗi nghiệp vụ có mã (BR01, BR03...) | Checked exception |
