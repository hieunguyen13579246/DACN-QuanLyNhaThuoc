# 01 – Khảo sát Nghiệp vụ

## 1.1 Bối cảnh

Chuỗi nhà thuốc hoạt động tại nhiều tỉnh/quận, hiện quản lý bằng sổ tay và Excel. Tình trạng tồn kho lệch sổ, bán nhầm thuốc cận/hết hạn, không kiểm tra đơn kê toa, điều chuyển bằng tin nhắn, nhập hàng theo cảm tính và không có số liệu phân tích theo khu vực là các vấn đề cấp bách cần giải quyết. Hệ thống IS009 được xây dựng để số hóa toàn bộ nghiệp vụ và cung cấp nền tảng phân tích nhu cầu dược phẩm theo địa bàn.

## 1.2 Mục tiêu hệ thống

| Mã | Mục tiêu |
|----|----------|
| OB01 | Quản lý danh mục thuốc và mã vạch tập trung toàn chuỗi |
| OB02 | Bán thuốc qua POS quét mã vạch, áp dụng FEFO tự động |
| OB03 | Kiểm soát tồn kho theo lô, không cho âm, cảnh báo hạn/tồn thấp |
| OB04 | Kiểm tra đơn thuốc kê toa trước khi bán thuốc bắt buộc kê đơn |
| OB05 | Quy trình duyệt cho điều chuyển và xuất hủy |
| OB06 | Phân tích nhu cầu dược phẩm theo khu vực (tỉnh/quận) |
| OB07 | Đề xuất nhập hàng dựa trên quy tắc nghiệp vụ |
| OB08 | Cung cấp dashboard cho quản lý nhà thuốc và quản trị chuỗi |

## 1.3 Phạm vi

### Trong phạm vi
- Danh mục thuốc, mã vạch, nhà cung cấp, khu vực, nhà thuốc
- Bán thuốc (không kê đơn và theo đơn), quét mã vạch POS
- Nhập kho, điều chuyển, xuất hủy (có luồng duyệt)
- Cảnh báo hạn dùng, tồn thấp, tồn chậm
- Phân tích nhu cầu theo khu vực; đề xuất nhập hàng bằng quy tắc
- Hủy giao dịch có kiểm soát; nhật ký kiểm toán
- Đọc GS1/Data Matrix khi nhập kho (bonus)

### Ngoài phạm vi
- Bảo hiểm y tế, trả hàng nhà cung cấp
- Thuốc gây nghiện/hướng thần (quản lý theo quy định riêng)
- Thanh toán thật (cổng thanh toán), kế toán/VAT
- Đơn vị quy đổi hộp–vỉ–viên

## 1.4 Stakeholder

| Stakeholder | Vai trò trong hệ thống | Quyền lợi/Mong đợi |
|-------------|------------------------|---------------------|
| Dược sĩ | Bán hàng POS, tiếp nhận đơn thuốc, tra cứu | Bán nhanh, cảnh báo hạn rõ ràng |
| Thủ kho | Nhập kho, điều chuyển, xuất hủy, in tem | Phiếu kho chính xác, in tem nhanh |
| Quản lý nhà thuốc | Duyệt phiếu, hủy giao dịch, dashboard nhà thuốc | Số liệu thời gian thực, cảnh báo kịp thời |
| Quản trị chuỗi | Danh mục, cấu hình hệ thống, dashboard toàn chuỗi | Thống nhất dữ liệu, phân tích khu vực |
| Khách hàng | Mua thuốc, xuất trình đơn | Phục vụ nhanh, đúng thuốc |
| Nhà cung cấp | Cung cấp thuốc, thông tin lô | Đặt hàng chính xác |
| Bộ phận CNTT | Vận hành hệ thống | Hệ thống ổn định, dễ bảo trì |

## 1.5 Phương pháp thực hiện

- **Phỏng vấn**: dược sĩ trưởng, thủ kho, quản lý 2 chi nhánh (mô phỏng)
- **Quan sát**: quy trình bán và nhập kho tại quầy
- **Phân tích tài liệu**: danh mục thuốc Excel, phiếu nhập kho giấy, Thông tư 52/2017/TT-BYT, 26/2025/TT-BYT
- **Mô hình hóa**: UML Use Case, Activity, Sequence, Class Diagram; ERD Mermaid

---

## 2. AS-IS – Mô tả quy trình hiện tại

### P1 – Quản lý danh mục thuốc
1. Nhân viên nhập thông tin thuốc vào file Excel riêng từng nhà thuốc
2. Cập nhật giá bán thủ công; không có mã vạch thống nhất
3. Tra cứu bằng cách tìm trong Excel; dễ nhầm tên biệt dược
4. Không có trường "thuốc kê đơn"; nhân viên nhớ theo kinh nghiệm

### P2 – Nhập kho
1. Nhà cung cấp giao hàng, nhân viên kiểm đếm thực tế
2. Ghi tay vào sổ nhập kho hoặc Excel: tên thuốc, số lô, hạn dùng, số lượng, giá
3. Cập nhật tồn trong file Excel tồn kho (dễ sai công thức)
4. Không lưu hệ thống: số lô, ngày sản xuất, nhà cung cấp

### P3 – Bán thuốc
1. Khách đưa đơn hoặc hỏi thuốc
2. Nhân viên tra Excel để biết tồn còn không
3. Lấy thuốc theo thứ tự cảm tính (không đảm bảo FEFO)
4. Ghi tay hóa đơn hoặc nhập Excel; trừ tồn thủ công
5. Thuốc kê đơn: không kiểm tra đơn; bán theo lời khách

### P4 – Điều chuyển và xuất hủy
1. Quản lý nhắn tin/gọi điện cho nhà thuốc kia
2. Xuất kho và nhập kho ghi vào sổ mỗi bên
3. Không có xác nhận điện tử; dễ sai lệch số lượng
4. Thuốc hết hạn: lập danh sách Excel, tiêu hủy và ghi sổ

### P5 – Theo dõi hạn dùng và tồn kho
1. Hàng tháng kiểm tra Excel từng nhà thuốc để tìm thuốc cận hạn
2. Tồn kho tổng hợp thủ công, không có con số tức thời
3. Không có định mức tồn tối thiểu; nhập theo cảm tính

### P6 – Phân tích nhu cầu và đề xuất nhập hàng
1. Xem lịch sử xuất kho Excel để ước lượng nhu cầu
2. Không phân tích theo khu vực; không theo mùa/xu hướng
3. Đặt hàng dựa hoàn toàn vào kinh nghiệm quản lý

---

## 3. Pain Points

| # | Nhóm | Vấn đề | Hậu quả |
|---|------|---------|---------|
| PP01 | Tồn kho | Tồn lệch giữa sổ và thực tế | Mất doanh thu, kiểm kê mất nhiều giờ |
| PP02 | An toàn dược | Bán thuốc kê đơn không kiểm tra đơn | Vi phạm TT52; rủi ro pháp lý |
| PP03 | An toàn dược | Bán thuốc cận/hết hạn do không kiểm tra FEFO | Nguy hiểm cho sức khỏe khách |
| PP04 | Liên chi nhánh | Điều chuyển bằng tin nhắn, không xác nhận | Sai lệch số lượng, tranh chấp |
| PP05 | Phân tích | Không có số liệu theo khu vực | Nhập hàng không đúng nhu cầu địa bàn |
| PP06 | Mã vạch | Không có mã vạch thống nhất | Sai sót khi bán; không tra cứu được nhanh |
| PP07 | Nhân sự | Tốn thời gian kiểm kê, cập nhật sổ tay | Giảm thời gian phục vụ khách |
| PP08 | Kiểm soát | Không có nhật ký; không truy vết hủy giao dịch | Gian lận không phát hiện được |

---

## 4. Vai trò và Trách nhiệm

| Vai trò | Trách nhiệm chính | Phạm vi dữ liệu |
|---------|-------------------|-----------------|
| Dược sĩ (DUOCSI) | Bán hàng POS, tiếp nhận đơn thuốc, quản lý khách hàng, tra cứu | Nhà thuốc mình |
| Thủ kho (THUKHO) | Tạo phiếu nhập/điều chuyển/xuất hủy, nhận hàng điều chuyển, in tem | Nhà thuốc mình |
| Quản lý (QUANLY) | Duyệt phiếu, hủy giao dịch, cảnh báo, định mức tồn, dashboard | Nhà thuốc mình |
| Quản trị chuỗi (QUANTRI) | Danh mục, mã vạch, NCC, khu vực, người dùng, cấu hình, nhật ký, dashboard | Toàn chuỗi |
| Thiết bị quét | Đọc mã vạch (USB bàn phím / camera) | Actor kỹ thuật |

---

## 5. Yêu cầu chức năng (FR)

| Mã | Mô tả | Ưu tiên |
|----|-------|---------|
| FR01 | Quản lý danh mục thuốc (thêm/sửa/ngưng bán) | Cao |
| FR02 | Quản lý mã vạch thuốc (EAN13, CODE128, nội bộ); in tem | Cao |
| FR03 | Quản lý nhà cung cấp, khu vực, nhà thuốc | Cao |
| FR04 | Quản lý người dùng và phân quyền RBAC | Cao |
| FR05 | Nhập kho: tạo phiếu, đọc GS1 (bonus), xác nhận hoàn thành | Cao |
| FR06 | Bán hàng POS: quét mã vạch, FEFO tự động, tính tiền | Cao |
| FR07 | Bán theo đơn thuốc: kiểm tra hiệu lực, số lượng cho phép | Cao |
| FR08 | Quản lý khách hàng và đơn thuốc | Cao |
| FR09 | Điều chuyển kho: tạo, duyệt, hoàn thành | Cao |
| FR10 | Xuất hủy: tạo, duyệt, ghi lý do | Cao |
| FR11 | Hủy giao dịch bán: kiểm soát thời gian, hoàn kho | Cao |
| FR12 | Cảnh báo hạn dùng (3 mức); tồn thấp; tồn chậm | Cao |
| FR13 | Định mức tồn tối thiểu / tối đa theo nhà thuốc | Trung |
| FR14 | Dashboard nhà thuốc (doanh thu, tồn, cảnh báo) | Cao |
| FR15 | Dashboard chuỗi (toàn bộ, phân tích khu vực) | Cao |
| FR16 | Phân tích nhu cầu theo khu vực; K1–K7 | Cao |
| FR17 | Đề xuất nhập hàng theo quy tắc (K5) | Trung |
| FR18 | Nhật ký kiểm toán (AuditLog) | Cao |
| FR19 | Đối soát tồn kho (BienDongKho vs TonKho) | Trung |
| FR20 | Cấu hình hệ thống (CauHinh) | Trung |

## 6. Yêu cầu phi chức năng (NFR)

| Mã | Mô tả |
|----|-------|
| NFR01 | Thời gian phản hồi POS ≤ 2 giây (quét → hiển thị thuốc) |
| NFR02 | Tính toàn vẹn dữ liệu tồn kho: transaction ACID; không âm |
| NFR03 | Phân quyền theo vai trò; dữ liệu nhà thuốc cách ly (BR13) |
| NFR04 | Che số điện thoại khách trên báo cáo (BR16) |
| NFR05 | Hỗ trợ đồng thời ≥ 10 người bán cùng lúc toàn chuỗi |
| NFR06 | Dữ liệu y tế giả lập; không dùng thông tin cá nhân thật |
| NFR07 | Giao diện web responsive; tương thích Chrome/Edge |
| NFR08 | Nhật ký kiểm toán không xóa được (chỉ thêm) |

---

## 7. Quy tắc Nghiệp vụ (BR)

| Mã | Mô tả | Ngoại lệ / Ghi chú | Nơi áp dụng |
|----|-------|---------------------|-------------|
| BR01 | Thuốc kê đơn chỉ bán khi gắn DonThuoc còn hiệu lực; thuốc có trong đơn; SL ≤ được phép; chỉ bán tại nhà thuốc tiếp nhận | — | FR06, FR07 |
| BR02 | FEFO: hệ thống tự chọn lô gần hạn nhất còn tồn; có thể chọn thủ công | — | FR06 |
| BR03 | Không bán lô hết hạn (han_dung < hôm nay VN); cảnh báo ≤30 ngày | — | FR06 |
| BR04 | Không bán vượt tồn lô; tồn không âm; xử lý bằng transaction + khóa | Hai người bán lô cuối: UPDLOCK | FR06 |
| BR05 | Đơn hết hiệu lực sau so_ngay_hieu_luc ngày từ ngay_ke_don; mặc định 5 ngày (TT52/2017, 26/2025) | Cần đối chiếu văn bản gốc | FR07, FR08 |
| BR06 | Mã vạch duy nhất toàn hệ thống; EAN13 đủ 13 chữ số và đúng chữ số kiểm | — | FR02 |
| BR07 | Nhập kho: han_dung > ngày nhập > ngay_san_xuat; (thuoc_id, so_lo) duy nhất; nhập lô đã có thì cộng dồn | — | FR05 |
| BR08 | Điều chuyển: hai nhà thuốc khác nhau; đủ tồn lô; quản lý nơi xuất duyệt; hoàn thành xử lý theo transaction | — | FR09 |
| BR09 | Xuất hủy cần lý do (HET_HAN, HU_HONG, THU_HOI, KHAC) và quản lý duyệt | — | FR10 |
| BR10 | Hủy GD: chỉ QUANLY; trong gio_cho_phep_huy_giao_dich giờ (mặc định 24h); bắt buộc lý do; hoàn kho đúng lô | — | FR11 |
| BR11 | Cảnh báo hạn: đã hết / ≤30 / ≤60 / ≤90 ngày (cấu hình). Tồn thấp: tổng tồn < ton_toi_thieu. Tồn chậm: có tồn, không bán trong so_ngay_ton_cham ngày | — | FR12 |
| BR12 | Mọi biến động tồn ghi BienDongKho; TonKho khớp tổng BienDongKho (đối soát) | — | FR05–FR11, FR19 |
| BR13 | DUOCSI/THUKHO/QUANLY chỉ thấy dữ liệu nhà thuốc mình; QUANTRI xem toàn chuỗi | — | NFR03 |
| BR14 | Đổi giá, hủy GD, duyệt/từ chối phiếu, xuất hủy ghi AuditLog | — | FR18 |
| BR15 | Giá bán lẻ > 0; ChiTietGiaoDich lưu đơn giá và giá vốn tại thời điểm bán | — | FR06 |
| BR16 | Che bớt số điện thoại khách trên báo cáo, dashboard | — | NFR04 |

---

## 8. Yêu cầu Dashboard

| Dashboard | Người xem | Chỉ số chính |
|-----------|-----------|--------------|
| Dashboard nhà thuốc | QUANLY, DUOCSI | Doanh thu hôm nay/tuần, tồn theo lô, cảnh báo hạn/tồn thấp, top thuốc bán |
| Dashboard chuỗi | QUANTRI | Doanh thu toàn chuỗi, phân tích khu vực, xu hướng theo tháng, hiệu suất từng nhà thuốc, đề xuất nhập hàng |

---

## 9. Kế hoạch Dữ liệu Giả lập và Bảo vệ Nhạy cảm

- Toàn bộ dữ liệu khách hàng, SĐT, đơn thuốc, bác sĩ, chứng chỉ, số lô do script sinh ngẫu nhiên
- Tên biệt dược là tên giả (không trùng thuốc thật đang lưu hành)
- SĐT khách: ẩn 4 chữ số giữa trên báo cáo/dashboard (BR16)
- Script sinh dữ liệu: tham chiếu file `scripts/seed_fake_data.sql`
- Không lưu trữ thông tin cá nhân thật trong hệ thống demo/kiểm thử

---

> **Giả định:**
> 1. Chuỗi có khoảng 5–20 nhà thuốc, hoạt động tại 3–5 tỉnh/thành.
> 2. Mỗi nhà thuốc có 1 quản lý, 1–3 dược sĩ, 1 thủ kho.
> 3. Các nhà thuốc dùng chung catalog thuốc (danh mục tập trung tại QUANTRI).
> 4. Kết nối internet ổn định; không cần chế độ offline.
> 5. Thông tư 26/2025/TT-BYT áp dụng tương tự TT52/2017 về thời hạn đơn; cần đối chiếu bản chính.
