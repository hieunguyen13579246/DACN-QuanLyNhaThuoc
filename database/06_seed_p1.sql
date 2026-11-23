-- 06_seed_p1.sql – Dữ liệu tĩnh IS009
USE IS009; SET QUOTED_IDENTIFIER ON;
GO
DISABLE TRIGGER trg_BienDongKho_ChiThem_Upd ON BienDongKho;
DISABLE TRIGGER trg_BienDongKho_ChiThem_Del ON BienDongKho;
DISABLE TRIGGER trg_AuditLog_ChiThem_Upd    ON AuditLog;
DISABLE TRIGGER trg_AuditLog_ChiThem_Del    ON AuditLog;
GO

-- KhuVuc
SET IDENTITY_INSERT KhuVuc ON;
INSERT INTO KhuVuc(id,ma,ten,cap,khu_vuc_cha_id,dan_so) VALUES
(1,N'HCM',N'TP. Hồ Chí Minh',N'TINH',NULL,NULL),
(2,N'HCM-Q1',N'Quận 1',N'QUAN',1,150000),
(3,N'HCM-BT',N'Quận Bình Thạnh',N'QUAN',1,500000),
(4,N'HCM-TB',N'Quận Tân Bình',N'QUAN',1,450000),
(5,N'HN',N'Hà Nội',N'TINH',NULL,NULL),
(6,N'HN-HK',N'Quận Hoàn Kiếm',N'QUAN',5,180000),
(7,N'HN-DD',N'Quận Đống Đa',N'QUAN',5,350000),
(8,N'DN',N'Đà Nẵng',N'TINH',NULL,NULL),
(9,N'DN-HC',N'Quận Hải Châu',N'QUAN',8,220000),
(10,N'DN-TK',N'Quận Thanh Khê',N'QUAN',8,180000),
(11,N'CT',N'Cần Thơ',N'TINH',NULL,NULL),
(12,N'CT-NK',N'Quận Ninh Kiều',N'QUAN',11,285000);
SET IDENTITY_INSERT KhuVuc OFF;
GO

-- NhaThuoc
SET IDENTITY_INSERT NhaThuoc ON;
INSERT INTO NhaThuoc(id,ma,ten,dia_chi,khu_vuc_id,so_dien_thoai,trang_thai,ngay_khai_truong) VALUES
(1,N'NT001',N'Nhà thuốc An Khang Quận 1',N'12 Nguyễn Huệ, Quận 1, TP.HCM',2,N'02838221001',N'HOAT_DONG','2022-01-15'),
(2,N'NT002',N'Nhà thuốc An Khang Bình Thạnh',N'45 Đinh Bộ Lĩnh, Quận Bình Thạnh, TP.HCM',3,N'02835108002',N'HOAT_DONG','2022-03-01'),
(3,N'NT003',N'Nhà thuốc An Khang Tân Bình',N'88 Cộng Hòa, Quận Tân Bình, TP.HCM',4,N'02838443003',N'HOAT_DONG','2022-06-10'),
(4,N'NT004',N'Nhà thuốc Minh Châu Hoàn Kiếm',N'5 Hàng Trống, Quận Hoàn Kiếm, Hà Nội',6,N'02439261004',N'HOAT_DONG','2021-11-20'),
(5,N'NT005',N'Nhà thuốc Minh Châu Đống Đa 1',N'23 Tôn Đức Thắng, Quận Đống Đa, Hà Nội',7,N'02435624005',N'HOAT_DONG','2022-02-14'),
(6,N'NT006',N'Nhà thuốc Minh Châu Đống Đa 2',N'67 Khâm Thiên, Quận Đống Đa, Hà Nội',7,N'02438856006',N'HOAT_DONG','2022-08-01'),
(7,N'NT007',N'Nhà thuốc Đà Nẵng Hải Châu',N'10 Trần Phú, Quận Hải Châu, Đà Nẵng',9,N'02363823007',N'HOAT_DONG','2022-01-05'),
(8,N'NT008',N'Nhà thuốc Đà Nẵng Thanh Khê',N'34 Điện Biên Phủ, Quận Thanh Khê, Đà Nẵng',10,N'02363612008',N'HOAT_DONG','2022-09-01'),
(9,N'NT009',N'Nhà Thuốc Cần Thơ Ninh Kiều',N'66 Trần Hưng Đạo, Quận Ninh Kiều, Cần Thơ',12,N'02314105009',N'HOAT_DONG','2023-12-05');
SET IDENTITY_INSERT NhaThuoc OFF;
GO

-- NguoiDung (password = BCrypt "123456")
SET IDENTITY_INSERT NguoiDung ON;
INSERT INTO NguoiDung(id,ho_ten,email,password_hash,vai_tro,nha_thuoc_id,so_chung_chi_hanh_nghe,kich_hoat,created_at) VALUES
(1,N'Nguyễn Quản Trị 1',N'qt01@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'QUANTRI',NULL,NULL,1,SYSUTCDATETIME()),
(2,N'Trần Quản Trị 2',N'qt02@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'QUANTRI',NULL,NULL,1,SYSUTCDATETIME()),
(3,N'Lê Quản Lý NT1',N'ql01@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'QUANLY',1,NULL,1,SYSUTCDATETIME()),
(4,N'Phạm Quản Lý NT2',N'ql02@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'QUANLY',2,NULL,1,SYSUTCDATETIME()),
(5,N'Hoàng Quản Lý NT3',N'ql03@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'QUANLY',3,NULL,1,SYSUTCDATETIME()),
(6,N'Đinh Quản Lý NT4',N'ql04@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'QUANLY',4,NULL,1,SYSUTCDATETIME()),
(7,N'Bùi Quản Lý NT5',N'ql05@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'QUANLY',5,NULL,1,SYSUTCDATETIME()),
(8,N'Vũ Quản Lý NT6',N'ql06@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'QUANLY',6,NULL,1,SYSUTCDATETIME()),
(9,N'Đặng Quản Lý NT7',N'ql07@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'QUANLY',7,NULL,1,SYSUTCDATETIME()),
(10,N'Ngô Quản Lý NT8',N'ql08@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'QUANLY',8,NULL,1,SYSUTCDATETIME()),
(11,N'Trần Dược Sĩ NT1',N'ds01@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'DUOCSI',1,N'DS-001/2020',1,SYSUTCDATETIME()),
(12,N'Lê Dược Sĩ NT2',N'ds02@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'DUOCSI',2,N'DS-002/2021',1,SYSUTCDATETIME()),
(13,N'Nguyễn Dược Sĩ NT3',N'ds03@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'DUOCSI',3,N'DS-003/2019',1,SYSUTCDATETIME()),
(14,N'Phạm Dược Sĩ NT4',N'ds04@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'DUOCSI',4,N'DS-004/2022',1,SYSUTCDATETIME()),
(15,N'Hoàng Dược Sĩ NT5',N'ds05@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'DUOCSI',5,N'DS-005/2020',1,SYSUTCDATETIME()),
(16,N'Lý Thủ Kho NT1',N'tk01@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'THUKHO',1,NULL,1,SYSUTCDATETIME()),
(17,N'Mai Thủ Kho NT2',N'tk02@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'THUKHO',2,NULL,1,SYSUTCDATETIME()),
(18,N'Cao Thủ Kho NT3',N'tk03@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'THUKHO',3,NULL,1,SYSUTCDATETIME()),
(19,N'Tô Thủ Kho NT6',N'tk06@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'THUKHO',6,NULL,1,SYSUTCDATETIME()),
(20,N'Dư Thủ Kho NT7',N'tk07@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'THUKHO',7,NULL,1,SYSUTCDATETIME()),
(21,N'Nguyễn Quản Lý NT9',N'ql09@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'QUANLY',9,NULL,1,SYSUTCDATETIME()),
(22,N'Nguyễn Dược Sĩ NT9',N'ds09@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'DUOCSI',9,N'DS-009/2023',1,SYSUTCDATETIME()),
(23,N'Trần Thủ Kho NT9',N'tk09@nhathuoc.vn',N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu',N'THUKHO',9,NULL,1,SYSUTCDATETIME());
SET IDENTITY_INSERT NguoiDung OFF;
GO

-- NhomThuoc + NhaCungCap
SET IDENTITY_INSERT NhomThuoc ON;
INSERT INTO NhomThuoc(id,ma,ten) VALUES
(1,N'KS',N'Kháng sinh'),(2,N'GP',N'Hạ sốt Giảm đau'),
(3,N'TH',N'Tiêu hóa'),(4,N'TM',N'Tim mạch'),
(5,N'HH',N'Hô hấp'),(6,N'VT',N'Vitamin & Bổ sung'),
(7,N'DL',N'Da liễu'),(8,N'NT',N'Nội tiết');
SET IDENTITY_INSERT NhomThuoc OFF;
SET IDENTITY_INSERT NhaCungCap ON;
INSERT INTO NhaCungCap(id,ma,ten,ma_so_thue,trang_thai) VALUES
(1,N'NCC001',N'Công ty CP Dược Hậu Giang',N'0100375518',N'HOAT_DONG'),
(2,N'NCC002',N'Công ty CP Pymepharco',N'4200150824',N'HOAT_DONG'),
(3,N'NCC003',N'Công ty CP Traphaco',N'0101656884',N'HOAT_DONG'),
(4,N'NCC004',N'Công ty CP Dược phẩm OPV',N'0300717509',N'HOAT_DONG'),
(5,N'NCC005',N'Công ty CP Hóa - Dược phẩm Mekophar',N'0300399906',N'HOAT_DONG'),
(6,N'NCC006',N'Công ty CP Dược phẩm Imexpharm',N'1800155119',N'HOAT_DONG');
SET IDENTITY_INSERT NhaCungCap OFF;
GO

-- Thuoc (60 thuoc; can_ke_don=1 ~18 thuoc ~30%)
SET IDENTITY_INSERT Thuoc ON;
INSERT INTO Thuoc(id,ma,ten,hoat_chat,ham_luong,dang_bao_che,don_vi_tinh,nhom_thuoc_id,can_ke_don,gia_ban_le,trang_thai) VALUES
(1,N'TH001',N'Amoxicillin 500mg',N'Amoxicillin',N'500mg',N'Viên nén',N'Viên',1,1,2500,N'DANG_BAN'),
(2,N'TH002',N'Augmentin 625mg',N'Amoxicillin + Acid Clavulanic',N'625mg',N'Viên nén',N'Viên',1,1,8500,N'DANG_BAN'),
(3,N'TH003',N'Azithromycin 500mg',N'Azithromycin',N'500mg',N'Viên nén',N'Viên',1,1,6000,N'DANG_BAN'),
(4,N'TH004',N'Ciprofloxacin 500mg',N'Ciprofloxacin',N'500mg',N'Viên nén',N'Viên',1,1,4500,N'DANG_BAN'),
(5,N'TH005',N'Metronidazole 250mg',N'Metronidazole',N'250mg',N'Viên nén',N'Viên',1,0,1200,N'DANG_BAN'),
(6,N'TH006',N'Doxycycline 100mg',N'Doxycycline',N'100mg',N'Viên nang',N'Viên',1,1,3000,N'DANG_BAN'),
(7,N'TH007',N'Cefuroxime 500mg',N'Cefuroxime',N'500mg',N'Viên nén',N'Viên',1,1,7500,N'DANG_BAN'),
(8,N'TH008',N'Erythromycin 500mg',N'Erythromycin',N'500mg',N'Viên nén',N'Viên',1,0,2000,N'DANG_BAN'),
(9,N'TH009',N'Clarithromycin 250mg',N'Clarithromycin',N'250mg',N'Viên nén',N'Viên',1,1,5500,N'DANG_BAN'),
(10,N'TH010',N'Cotrimoxazole 480mg',N'Trimethoprim + Sulfamethoxazole',N'480mg',N'Viên nén',N'Viên',1,0,1800,N'DANG_BAN'),
(11,N'TH011',N'Paracetamol 500mg',N'Paracetamol',N'500mg',N'Viên nén',N'Viên',2,0,1000,N'DANG_BAN'),
(12,N'TH012',N'Ibuprofen 400mg',N'Ibuprofen',N'400mg',N'Viên nén',N'Viên',2,0,1500,N'DANG_BAN'),
(13,N'TH013',N'Diclofenac 50mg',N'Diclofenac',N'50mg',N'Viên nén',N'Viên',2,0,1800,N'DANG_BAN'),
(14,N'TH014',N'Meloxicam 7.5mg',N'Meloxicam',N'7.5mg',N'Viên nén',N'Viên',2,1,4200,N'DANG_BAN'),
(15,N'TH015',N'Paracetamol sủi 650mg',N'Paracetamol',N'650mg',N'Viên sủi',N'Viên',2,0,3500,N'DANG_BAN'),
(16,N'TH016',N'Aspirin 100mg',N'Acid Acetylsalicylic',N'100mg',N'Viên nén',N'Viên',2,1,2200,N'DANG_BAN'),
(17,N'TH017',N'Tramadol 50mg',N'Tramadol',N'50mg',N'Viên nang',N'Viên',2,1,5000,N'DANG_BAN'),
(18,N'TH018',N'Omeprazole 20mg',N'Omeprazole',N'20mg',N'Viên nang',N'Viên',3,0,2800,N'DANG_BAN'),
(19,N'TH019',N'Esomeprazole 40mg',N'Esomeprazole',N'40mg',N'Viên nang',N'Viên',3,1,8000,N'DANG_BAN'),
(20,N'TH020',N'Domperidone 10mg',N'Domperidone',N'10mg',N'Viên nén',N'Viên',3,0,1600,N'DANG_BAN'),
(21,N'TH021',N'Loperamide 2mg',N'Loperamide',N'2mg',N'Viên nang',N'Viên',3,0,1800,N'DANG_BAN'),
(22,N'TH022',N'Smecta 3g',N'Diosmectite',N'3g',N'Gói bột',N'Gói',3,0,5000,N'DANG_BAN'),
(23,N'TH023',N'Lansoprazole 30mg',N'Lansoprazole',N'30mg',N'Viên nang',N'Viên',3,1,6500,N'DANG_BAN'),
(24,N'TH024',N'Metoclopramide 10mg',N'Metoclopramide',N'10mg',N'Viên nén',N'Viên',3,0,1500,N'DANG_BAN'),
(25,N'TH025',N'Berberin 50mg',N'Berberine',N'50mg',N'Viên nén',N'Viên',3,0,800,N'DANG_BAN'),
(26,N'TH026',N'Amlodipine 5mg',N'Amlodipine',N'5mg',N'Viên nén',N'Viên',4,1,3500,N'DANG_BAN'),
(27,N'TH027',N'Atorvastatin 20mg',N'Atorvastatin',N'20mg',N'Viên nén',N'Viên',4,1,9500,N'DANG_BAN'),
(28,N'TH028',N'Metoprolol 50mg',N'Metoprolol',N'50mg',N'Viên nén',N'Viên',4,1,4000,N'DANG_BAN'),
(29,N'TH029',N'Lisinopril 10mg',N'Lisinopril',N'10mg',N'Viên nén',N'Viên',4,1,3200,N'DANG_BAN'),
(30,N'TH030',N'Bisoprolol 5mg',N'Bisoprolol',N'5mg',N'Viên nén',N'Viên',4,1,3800,N'DANG_BAN'),
(31,N'TH031',N'Salbutamol 4mg',N'Salbutamol',N'4mg',N'Viên nén',N'Viên',5,0,1800,N'DANG_BAN'),
(32,N'TH032',N'Loratadine 10mg',N'Loratadine',N'10mg',N'Viên nén',N'Viên',5,0,2000,N'DANG_BAN'),
(33,N'TH033',N'Cetirizine 10mg',N'Cetirizine',N'10mg',N'Viên nén',N'Viên',5,0,2200,N'DANG_BAN'),
(34,N'TH034',N'Montelukast 10mg',N'Montelukast',N'10mg',N'Viên nén',N'Viên',5,1,12000,N'DANG_BAN'),
(35,N'TH035',N'Bromhexine 8mg',N'Bromhexine',N'8mg',N'Viên nén',N'Viên',5,0,1500,N'DANG_BAN'),
(36,N'TH036',N'Acemuc 200mg (NAC)',N'N-Acetylcysteine',N'200mg',N'Gói bột',N'Gói',5,0,4500,N'DANG_BAN'),
(37,N'TH037',N'Theophylline 100mg',N'Theophylline',N'100mg',N'Viên nén',N'Viên',5,1,2500,N'DANG_BAN'),
(38,N'TH038',N'Fexofenadine 180mg',N'Fexofenadine',N'180mg',N'Viên nén',N'Viên',5,0,9000,N'DANG_BAN'),
(39,N'TH039',N'Vitamin C sủi 1000mg',N'Acid Ascorbic',N'1000mg',N'Viên sủi',N'Viên',6,0,3500,N'DANG_BAN'),
(40,N'TH040',N'Vitamin B tổng hợp (B-Complex)',N'Vitamin nhóm B',N'Tổng hợp',N'Viên nén',N'Viên',6,0,2000,N'DANG_BAN'),
(41,N'TH041',N'Vitamin D3 1000IU',N'Cholecalciferol',N'1000IU',N'Viên nang',N'Viên',6,0,4000,N'DANG_BAN'),
(42,N'TH042',N'Canxi 500mg',N'Calcium Gluconate',N'500mg',N'Viên nén',N'Viên',6,0,1500,N'DANG_BAN'),
(43,N'TH043',N'Sắt Fumarate 200mg',N'Ferrous Fumarate',N'200mg',N'Viên nén',N'Viên',6,0,2500,N'DANG_BAN'),
(44,N'TH044',N'Dầu cá Omega-3 1000mg',N'Omega-3',N'1000mg',N'Viên nang',N'Viên',6,0,5000,N'DANG_BAN'),
(45,N'TH045',N'Kem bôi Clotrimazole 1%',N'Clotrimazole',N'1%',N'Kem bôi',N'Tuýp',7,0,25000,N'DANG_BAN'),
(46,N'TH046',N'Kem bôi Betamethasone 0.1%',N'Betamethasone',N'0.1%',N'Kem bôi',N'Tuýp',7,1,35000,N'DANG_BAN'),
(47,N'TH047',N'Kem bôi Hydrocortisone 1%',N'Hydrocortisone',N'1%',N'Kem bôi',N'Tuýp',7,0,22000,N'DANG_BAN'),
(48,N'TH048',N'Kem bôi Acyclovir 5%',N'Acyclovir',N'5%',N'Kem bôi',N'Tuýp',7,0,45000,N'DANG_BAN'),
(49,N'TH049',N'Kem bôi Miconazole 2%',N'Miconazole',N'2%',N'Kem bôi',N'Tuýp',7,0,28000,N'DANG_BAN'),
(50,N'TH050',N'Metformin 500mg',N'Metformin',N'500mg',N'Viên nén',N'Viên',8,1,1200,N'DANG_BAN'),
(51,N'TH051',N'Glibenclamide 5mg',N'Glibenclamide',N'5mg',N'Viên nén',N'Viên',8,1,1800,N'DANG_BAN'),
(52,N'TH052',N'Levothyroxine 50mcg',N'Levothyroxine',N'50mcg',N'Viên nén',N'Viên',8,1,3500,N'DANG_BAN'),
(53,N'TH053',N'Gliclazide 80mg',N'Gliclazide',N'80mg',N'Viên nén',N'Viên',8,1,2500,N'DANG_BAN'),
(54,N'TH054',N'Oresol bù điện giải 27.9g',N'Oresol (ORS)',N'27.9g',N'Gói bột',N'Gói',3,0,6000,N'DANG_BAN'),
(55,N'TH055',N'Nexium 20mg',N'Esomeprazole',N'20mg',N'Viên nén',N'Viên',3,0,12000,N'DANG_BAN'),
(56,N'TH056',N'Telfast 180mg',N'Fexofenadine',N'180mg',N'Viên nén',N'Viên',5,0,15000,N'DANG_BAN'),
(57,N'TH057',N'Kẽm Zinc 70mg sủi',N'Zinc Gluconate',N'70mg',N'Viên sủi',N'Viên',6,0,3000,N'DANG_BAN'),
(58,N'TH058',N'Aquamin Canxi 600mg',N'Calcium Carbonate',N'600mg',N'Viên nén',N'Viên',6,0,3200,N'DANG_BAN'),
(59,N'TH059',N'Gel bôi giảm đau Ketoprofen 2.5%',N'Ketoprofen',N'2.5%',N'Gel bôi',N'Tuýp',2,0,38000,N'DANG_BAN'),
(60,N'TH060',N'Zinnat 500mg',N'Cefuroxime',N'500mg',N'Viên nén',N'Viên',1,1,18000,N'DANG_BAN');
SET IDENTITY_INSERT Thuoc OFF;
GO

-- MaVachThuoc (EAN13 hop le tien to 893; check digit da tinh san)
SET IDENTITY_INSERT MaVachThuoc ON;
INSERT INTO MaVachThuoc(id,thuoc_id,ma_vach,loai_ma_vach,la_ma_chinh) VALUES
(1,1,N'8930000000019',N'EAN13',1),(2,2,N'8930000000026',N'EAN13',1),
(3,3,N'8930000000033',N'EAN13',1),(4,4,N'8930000000040',N'EAN13',1),
(5,5,N'8930000000057',N'EAN13',1),(6,6,N'8930000000064',N'EAN13',1),
(7,7,N'8930000000071',N'EAN13',1),(8,8,N'8930000000088',N'EAN13',1),
(9,9,N'8930000000095',N'EAN13',1),(10,10,N'8930000000101',N'EAN13',1),
(11,11,N'8930000000118',N'EAN13',1),(12,12,N'8930000000125',N'EAN13',1),
(13,13,N'8930000000132',N'EAN13',1),(14,14,N'8930000000149',N'EAN13',1),
(15,15,N'8930000000156',N'EAN13',1),(16,16,N'8930000000163',N'EAN13',1),
(17,17,N'8930000000170',N'EAN13',1),(18,18,N'8930000000187',N'EAN13',1),
(19,19,N'8930000000194',N'EAN13',1),(20,20,N'8930000000200',N'EAN13',1),
(21,21,N'8930000000217',N'EAN13',1),(22,22,N'8930000000224',N'EAN13',1),
(23,23,N'8930000000231',N'EAN13',1),(24,24,N'8930000000248',N'EAN13',1),
(25,25,N'8930000000255',N'EAN13',1),(26,26,N'8930000000262',N'EAN13',1),
(27,27,N'8930000000279',N'EAN13',1),(28,28,N'8930000000286',N'EAN13',1),
(29,29,N'8930000000293',N'EAN13',1),(30,30,N'8930000000309',N'EAN13',1),
(31,31,N'8930000000316',N'EAN13',1),(32,32,N'8930000000323',N'EAN13',1),
(33,33,N'8930000000330',N'EAN13',1),(34,34,N'8930000000347',N'EAN13',1),
(35,35,N'8930000000354',N'EAN13',1),(36,36,N'8930000000361',N'EAN13',1),
(37,37,N'8930000000378',N'EAN13',1),(38,38,N'8930000000385',N'EAN13',1),
(39,39,N'8930000000392',N'EAN13',1),(40,40,N'8930000000408',N'EAN13',1),
(41,41,N'8930000000415',N'EAN13',1),(42,42,N'8930000000422',N'EAN13',1),
(43,43,N'8930000000439',N'EAN13',1),(44,44,N'8930000000446',N'EAN13',1),
(45,45,N'8930000000453',N'EAN13',1),(46,46,N'8930000000460',N'EAN13',1),
(47,47,N'8930000000477',N'EAN13',1),(48,48,N'8930000000484',N'EAN13',1),
(49,49,N'8930000000491',N'EAN13',1),(50,50,N'8930000000507',N'EAN13',1),
(51,51,N'8930000000514',N'EAN13',1),(52,52,N'8930000000521',N'EAN13',1),
(53,53,N'8930000000538',N'EAN13',1),(54,54,N'8930000000545',N'EAN13',1),
(55,55,N'8930000000552',N'EAN13',1),(56,56,N'8930000000569',N'EAN13',1),
(57,57,N'8930000000576',N'EAN13',1),(58,58,N'8930000000583',N'EAN13',1),
(59,59,N'8930000000590',N'EAN13',1),(60,60,N'8930000000606',N'EAN13',1),
-- CODE128 noi bo cho mot so thuoc pho thong
(61,11,N'IS009-0011',N'CODE128',0),(62,18,N'IS009-0018',N'CODE128',0),
(63,25,N'IS009-0025',N'CODE128',0),(64,32,N'IS009-0032',N'CODE128',0),
(65,39,N'IS009-0039',N'CODE128',0),(66,40,N'IS009-0040',N'CODE128',0),
(67,54,N'IS009-0054',N'CODE128',0),(68,55,N'IS009-0055',N'CODE128',0);
SET IDENTITY_INSERT MaVachThuoc OFF;
GO
PRINT N'06_seed_p1: Dữ liệu tĩnh đã nạp xong.';
