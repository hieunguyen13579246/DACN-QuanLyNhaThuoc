# 04 – Sequence Diagram

## SD01 – Bán thuốc quét mã vạch + FEFO (không kê đơn)

```plantuml
@startuml SD01_BanThuocFEFO
actor "Dược sĩ" as DS
participant "BanHangController" as CTR
participant "BanHangService" as SVC
participant "BarcodeService" as BC
database "SQL Server" as DB

DS -> CTR : POST /ban/quet-ma-vach {ma_vach}
CTR -> BC : parseMaVach(ma_vach)
BC -> DB : SELECT thuoc_id FROM MaVachThuoc WHERE ma_vach=?
DB --> BC : thuoc_id
BC --> CTR : ThuocDTO

CTR -> SVC : timLoFEFO(nha_thuoc_id, thuoc_id)
SVC -> DB : SELECT lo_thuoc_id, han_dung FROM TonKho\nJOIN LoThuoc ON ...\nWHERE nha_thuoc_id=? AND thuoc_id=?\nAND han_dung >= TODAY AND so_luong>0\nORDER BY han_dung ASC\nFOR UPDATE (UPDLOCK)
DB --> SVC : [lo_thuoc_id, han_dung, so_luong]
SVC -> SVC : kiemTraCanhBao30Ngay(han_dung)
SVC --> CTR : LoFEFODTO (kèm canh_bao nếu có)
CTR --> DS : Hiển thị thuốc + lô + cảnh báo ≤30ngày

DS -> CTR : POST /ban/xac-nhan {giao_dich_payload JSON:\n nha_thuoc_id, nguoi_ban_id,\n items:[{thuoc_id,lo_thuoc_id,so_luong,don_gia}],\n phuong_thuc_thanh_toan, khach_hang_id?}
CTR -> SVC : taoGiaoDich(payload)

SVC -> DB : BEGIN TRANSACTION
SVC -> DB : SELECT so_luong FROM TonKho\nWHERE nha_thuoc_id=? AND lo_thuoc_id=?\nWITH (UPDLOCK, ROWLOCK)
DB --> SVC : so_luong_hien_tai
SVC -> SVC : kiemTraTonDu(so_luong_hien_tai, so_luong_ban)
alt Đủ tồn
  SVC -> DB : INSERT GiaoDich (trang_thai=HOAN_THANH)
  SVC -> DB : INSERT ChiTietGiaoDich (don_gia, gia_von) -- BR15
  SVC -> DB : UPDATE TonKho SET so_luong -= so_luong_ban -- BR04
  SVC -> DB : INSERT BienDongKho (loai=BAN) -- BR12
  SVC -> DB : COMMIT
  SVC --> CTR : GiaoDichDTO {ma_giao_dich, thanh_tien}
  CTR --> DS : Hóa đơn thành công
else Không đủ tồn
  SVC -> DB : ROLLBACK
  SVC --> CTR : Exception("Không đủ tồn")
  CTR --> DS : Lỗi "Hết hàng"
end
@enduml
```

---

## SD02 – Bán thuốc theo đơn

```plantuml
@startuml SD02_BanTheoEon
actor "Dược sĩ" as DS
participant "BanHangController" as CTR
participant "BanHangService" as SVC
participant "DonThuocService" as DSVC
database "SQL Server" as DB

DS -> CTR : GET /don-thuoc/{ma_don}
CTR -> DSVC : timDonThuoc(ma_don, nha_thuoc_id)
DSVC -> DB : SELECT dt.*, ctdt.* FROM DonThuoc dt\nJOIN ChiTietDonThuoc ctdt ON ...\nWHERE ma_don=? AND nha_thuoc_id=? -- BR01
DB --> DSVC : DonThuocDTO
DSVC -> DSVC : kiemTraHieuLuc(ngay_ke_don + so_ngay_hieu_luc) -- BR05
alt Đơn không hợp lệ
  DSVC --> CTR : Exception("Đơn hết hiệu lực / sai nhà thuốc")
  CTR --> DS : Thông báo lỗi
else Đơn hợp lệ
  DSVC --> CTR : DonThuocDTO (danh sách thuốc + SL còn bán được)
  CTR --> DS : Hiển thị thông tin đơn

  DS -> CTR : POST /ban/theo-don {don_thuoc_id, items:[...], ...}
  CTR -> SVC : taoGiaoDichTheoEon(payload)
  SVC -> DSVC : kiemTraSoLuong(don_thuoc_id, thuoc_id, so_luong_ban)
  DSVC -> DB : SELECT so_luong_ke - so_luong_da_ban FROM ChiTietDonThuoc -- BR01
  DB --> DSVC : so_luong_duoc_phep
  DSVC -> DSVC : validate(so_luong_ban <= so_luong_duoc_phep)

  SVC -> DB : BEGIN TRANSACTION
  SVC -> DB : INSERT GiaoDich (don_thuoc_id=?)
  SVC -> DB : INSERT ChiTietGiaoDich
  SVC -> DB : UPDATE TonKho (UPDLOCK)
  SVC -> DB : INSERT BienDongKho loại BAN
  SVC -> DB : UPDATE ChiTietDonThuoc\nSET so_luong_da_ban += so_luong_ban -- BR01
  SVC -> DB : UPDATE DonThuoc SET trang_thai=? -- CHUA_BAN→BAN_MOT_PHAN→DA_BAN_HET
  SVC -> DB : COMMIT
  SVC --> CTR : GiaoDichDTO
  CTR --> DS : Hóa đơn thành công
end
@enduml
```

---

## SD03 – Nhập kho (có đọc GS1 Data Matrix)

```plantuml
@startuml SD03_NhapKho
actor "Thủ kho" as TK
participant "KhoController" as CTR
participant "KhoService" as SVC
participant "Gs1Parser" as GS1
database "SQL Server" as DB

TK -> CTR : POST /kho/phieu-nhap/tao {nha_cung_cap_id}
CTR -> SVC : taoPhieuNhap(thukho_id, ncc_id)
SVC -> DB : INSERT PhieuKho (loai=NHAP, trang_thai=CHO_DUYET)
DB --> SVC : phieu_kho_id
SVC --> CTR : phieu_kho_id

loop Quét từng sản phẩm
  TK -> CTR : POST /kho/phieu-nhap/{id}/quet {raw_barcode}
  CTR -> GS1 : parse(raw_barcode)
  alt Mã GS1/DataMatrix
    GS1 -> GS1 : extractGTIN_Lot_Expiry(raw_barcode)
    GS1 --> CTR : {gtin, so_lo, han_dung}
    CTR -> DB : SELECT thuoc_id FROM MaVachThuoc WHERE ma_vach=gtin
    DB --> CTR : thuoc_id
    CTR --> TK : Tự điền thuoc_id, so_lo, han_dung
  else EAN13 / CODE128
    GS1 --> CTR : {ma_vach}
    CTR -> DB : SELECT thuoc_id FROM MaVachThuoc WHERE ma_vach=ma_vach
    DB --> CTR : thuoc_id
    CTR --> TK : Cần nhập thủ công: so_lo, han_dung
  end
  TK -> CTR : PUT /kho/phieu-nhap/{id}/dong {so_lo, han_dung, ngay_sx, so_luong, don_gia}
  CTR -> SVC : kiemTraBR07(thuoc_id, so_lo, han_dung, ngay_nhap)
  SVC -> SVC : validate: han_dung > ngay_nhap > ngay_sx
  SVC -> DB : SELECT id FROM LoThuoc WHERE thuoc_id=? AND so_lo=?
  alt Lô đã tồn tại
    SVC -> DB : INSERT ChiTietPhieuKho (lo_thuoc_id existing)
  else Lô mới
    SVC -> DB : INSERT LoThuoc (thuoc_id, so_lo, han_dung, ...)
    SVC -> DB : INSERT ChiTietPhieuKho
  end
end

TK -> CTR : POST /kho/phieu-nhap/{id}/hoan-thanh
CTR -> SVC : xacNhanNhapKho(phieu_kho_id)
SVC -> DB : BEGIN TRANSACTION
loop Mỗi ChiTietPhieuKho
  SVC -> DB : MERGE TonKho ...\n(INSERT or UPDATE so_luong += delta)
  SVC -> DB : INSERT BienDongKho (loai=NHAP) -- BR12
end
SVC -> DB : UPDATE PhieuKho SET trang_thai=HOAN_THANH
SVC -> DB : COMMIT
SVC --> CTR : OK
CTR --> TK : Nhập kho thành công
@enduml
```

---

## SD04 – Điều chuyển (tạo → duyệt)

```plantuml
@startuml SD04_DieuChuyen
actor "Thủ kho" as TK
actor "Quản lý" as QL
participant "KhoController" as CTR
participant "KhoService" as SVC
database "SQL Server" as DB

TK -> CTR : POST /kho/dieu-chuyen/tao {nha_thuoc_den_id, items}
CTR -> SVC : taoPhieuDieuChuyen(tk_id, payload)
SVC -> DB : Validate nha_thuoc_nguon ≠ nha_thuoc_den (BR08)
loop Mỗi item
  SVC -> DB : SELECT so_luong FROM TonKho\nWHERE nha_thuoc_id=nguon AND lo_thuoc_id=?
  DB --> SVC : ton_hien_tai
  SVC -> SVC : validate ton_hien_tai >= so_luong
end
SVC -> DB : INSERT PhieuKho (loai=DIEU_CHUYEN, CHO_DUYET)
SVC -> DB : INSERT ChiTietPhieuKho (nhiều dòng)
SVC --> CTR : phieu_id
CTR --> TK : Phiếu CHO_DUYET

QL -> CTR : GET /kho/dieu-chuyen/{id}
CTR -> DB : SELECT phieu + chi_tiet
DB --> CTR : PhieuDieuChuyenDTO
CTR --> QL : Hiển thị phiếu

alt Quản lý duyệt
  QL -> CTR : POST /kho/dieu-chuyen/{id}/duyet
  CTR -> SVC : duyetPhieu(ql_id, phieu_id)
  SVC -> DB : BEGIN TRANSACTION
  loop Mỗi lô trong phiếu (theo thứ tự lo_thuoc_id tăng dần – tránh deadlock)
    SVC -> DB : UPDATE TonKho SET so_luong -= x\nWHERE nha_thuoc_id=nguon AND lo_thuoc_id=?\nWITH (UPDLOCK, ROWLOCK)
    SVC -> DB : MERGE TonKho (INSERT or UPDATE so_luong += x)\nWHERE nha_thuoc_id=den
    SVC -> DB : INSERT BienDongKho (DIEU_CHUYEN_XUAT)
    SVC -> DB : INSERT BienDongKho (DIEU_CHUYEN_NHAP)
  end
  SVC -> DB : UPDATE PhieuKho SET trang_thai=HOAN_THANH,\nnguoi_duyet_id=?, thoi_gian_hoan_thanh=NOW()
  SVC -> DB : COMMIT
  SVC -> DB : INSERT AuditLog (BR14)
  SVC --> CTR : OK
  CTR --> QL : Duyệt thành công
else Quản lý từ chối
  QL -> CTR : POST /kho/dieu-chuyen/{id}/tu-choi {ly_do}
  CTR -> SVC : tuChoiPhieu(ql_id, phieu_id, ly_do)
  SVC -> DB : UPDATE PhieuKho SET trang_thai=TU_CHOI, ghi_chu=ly_do
  SVC -> DB : INSERT AuditLog (BR14)
  SVC --> CTR : OK
  CTR --> QL : Từ chối thành công
end
@enduml
```

---

## SD05 – Hủy giao dịch

```plantuml
@startuml SD05_HuyGiaoDich
actor "Quản lý" as QL
participant "BanHangController" as CTR
participant "BanHangService" as SVC
database "SQL Server" as DB

QL -> CTR : POST /giao-dich/{id}/huy {ly_do}
CTR -> SVC : huyGiaoDich(ql_id, gd_id, ly_do)

SVC -> DB : SELECT gd.*, ctgd.* FROM GiaoDich gd\nJOIN ChiTietGiaoDich ctgd ON ...\nWHERE gd.id=? AND trang_thai=HOAN_THANH
DB --> SVC : GiaoDichDTO

SVC -> SVC : kiemTraVaiTro(ql_id == QUANLY) -- BR10
SVC -> SVC : kiemTraThoiGian:\nNOW() - thoi_gian <= gio_cho_phep_huy * 3600s -- BR10
SVC -> SVC : validate ly_do != null

alt Hợp lệ
  SVC -> DB : BEGIN TRANSACTION
  SVC -> DB : UPDATE GiaoDich SET trang_thai=DA_HUY,\nly_do_huy=?, nguoi_huy_id=?, thoi_gian_huy=NOW()
  loop Mỗi ChiTietGiaoDich
    SVC -> DB : UPDATE TonKho SET so_luong += so_luong_ban\nWHERE nha_thuoc_id=? AND lo_thuoc_id=? -- BR10
    SVC -> DB : INSERT BienDongKho (loai=HOAN_KHO) -- BR12
  end
  alt Giao dịch có don_thuoc_id
    SVC -> DB : UPDATE ChiTietDonThuoc\nSET so_luong_da_ban -= so_luong_ban -- BR10
    SVC -> DB : UPDATE DonThuoc SET trang_thai=... (recalculate)
  end
  SVC -> DB : COMMIT
  SVC -> DB : INSERT AuditLog (BR14)
  SVC --> CTR : OK
  CTR --> QL : Hủy thành công; kho đã hoàn
else Không hợp lệ
  SVC --> CTR : Exception(lý do từ chối)
  CTR --> QL : Thông báo lỗi
end
@enduml
```

---

## SD06 – Hai dược sĩ bán lô cuối (xử lý đồng thời)

```plantuml
@startuml SD06_DongThoiLosCuoi
actor "Dược sĩ A" as DSA
actor "Dược sĩ B" as DSB
participant "BanHangService\n(Instance A)" as SVCA
participant "BanHangService\n(Instance B)" as SVCB
database "SQL Server" as DB

note over DB: TonKho[nha_thuoc=1][lo=42].so_luong = 1

DSA -> SVCA : taoGiaoDich([lo=42, sl=1])
DSB -> SVCB : taoGiaoDich([lo=42, sl=1])

SVCA -> DB : BEGIN TRAN\nSELECT so_luong FROM TonKho\nWHERE ... WITH (UPDLOCK, ROWLOCK)
note right: SVCA giữ khóa hàng

SVCB -> DB : BEGIN TRAN\nSELECT so_luong FROM TonKho\nWHERE ... WITH (UPDLOCK, ROWLOCK)
note right: SVCB bị block – chờ SVCA

SVCA -> DB : so_luong=1 >= 1 → OK
SVCA -> DB : UPDATE TonKho SET so_luong=0
SVCA -> DB : INSERT GiaoDich, BienDongKho
SVCA -> DB : COMMIT
note right: SVCA hoàn thành; khóa gải phóng

SVCB -> DB : so_luong=0 < 1 → ROLLBACK
SVCB --> DSB : Exception("Hết hàng – lô vừa được bán hết")

SVCA --> DSA : Thành công
@enduml
```
