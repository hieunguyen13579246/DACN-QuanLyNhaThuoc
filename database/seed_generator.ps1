# =============================================================
# seed_generator.ps1 – IS009 (Hoàn chỉnh)
# Sinh dữ liệu giả lập (06_seed_p1.sql) & có thể nạp toàn bộ DB IS009
# Chạy: .\seed_generator.ps1 [-RunSql]
# Yêu cầu: PowerShell 5.1+, SQL Server (sqlcmd)
# =============================================================
param(
    [switch]$RunSql = $false,
    [string]$Server = "localhost"
)

# ─── THAM SỐ CẤU HÌNH ────────────────────────────────────────
$OUT        = "$PSScriptRoot\06_seed.sql"
$TODAY      = [datetime]::Today          # ngày hôm nay VN
$START_DATE = $TODAY.AddMonths(-12)      # bắt đầu 12 tháng trước
$RAND       = [Random]::new(42)          # seed cố định (tái tạo được)

# BCrypt hash của "123456" (bcryptjs $2b$ cost=10, tương thích $2a$)
$PW_HASH = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu'

# ─── HÀM TIỆN ÍCH ────────────────────────────────────────────

# Escape single-quote cho T-SQL
function Q([string]$s) { return $s.Replace("'","''") }

# Số ngẫu nhiên trong [min,max]
function RandInt([int]$min,[int]$max) { return $RAND.Next($min,$max+1) }

# Chọn ngẫu nhiên 1 phần tử từ mảng
function Pick([array]$arr) { return $arr[$RAND.Next($arr.Length)] }

# Ngày ngẫu nhiên trong khoảng
function RandDate([datetime]$from,[datetime]$to) {
    $diff = ($to - $from).Days
    return $from.AddDays($RAND.Next($diff+1))
}

# Sinh mã EAN-13 hợp lệ (tiền tố 893 = Việt Nam)
function MakeEAN13 {
    $digits = @(8,9,3)
    1..9 | ForEach-Object { $digits += $RAND.Next(10) }
    $sum = 0
    for ($i=0; $i -lt 12; $i++) {
        $sum += if ($i % 2 -eq 0) { $digits[$i] } else { $digits[$i]*3 }
    }
    $check = (10 - ($sum % 10)) % 10
    $digits += $check
    return ($digits -join '')
}

# Sinh mã CODE128 nội bộ
function MakeCODE128([string]$prefix,[int]$id) {
    return "$prefix{0:D4}" -f $id
}

# Định dạng DATETIME2 UTC từ DateTime VN local
function FmtDT([datetime]$d) {
    $utc = $d.ToUniversalTime()
    return "'{0:yyyy-MM-dd HH:mm:ss}'" -f $utc
}

# Định dạng DATE
function FmtD([datetime]$d) { return "'{0:yyyy-MM-dd}'" -f $d }

# Khởi tạo tồn kho trong bộ nhớ: [nha_thuoc_id][lo_thuoc_id] = so_luong
$tonKho = @{}      # hashtable lồng

function TonHienTai([int]$nt,[int]$lo) {
    if ($tonKho.ContainsKey($nt) -and $tonKho[$nt].ContainsKey($lo)) {
        return $tonKho[$nt][$lo]
    }
    return 0
}

function CapNhatTon([int]$nt,[int]$lo,[int]$delta) {
    if (-not $tonKho.ContainsKey($nt))  { $tonKho[$nt]  = @{} }
    if (-not $tonKho[$nt].ContainsKey($lo)) { $tonKho[$nt][$lo] = 0 }
    $tonKho[$nt][$lo] += $delta
    if ($tonKho[$nt][$lo] -lt 0) { $tonKho[$nt][$lo] = 0 }  # safeguard
}

# ─── DỮ LIỆU TĨNH ────────────────────────────────────────────

$khuVuc = @(
    [pscustomobject]@{id=1;ma='HCM';ten='TP. Hồ Chí Minh';cap='TINH';cha=$null;dan_so=$null},
    [pscustomobject]@{id=2;ma='HCM-Q1';ten='Quận 1';cap='QUAN';cha=1;dan_so=150000},
    [pscustomobject]@{id=3;ma='HCM-BT';ten='Quận Bình Thạnh';cap='QUAN';cha=1;dan_so=500000},
    [pscustomobject]@{id=4;ma='HCM-TB';ten='Quận Tân Bình';cap='QUAN';cha=1;dan_so=450000},
    [pscustomobject]@{id=5;ma='HN';ten='Hà Nội';cap='TINH';cha=$null;dan_so=$null},
    [pscustomobject]@{id=6;ma='HN-HK';ten='Quận Hoàn Kiếm';cap='QUAN';cha=5;dan_so=180000},
    [pscustomobject]@{id=7;ma='HN-DD';ten='Quận Đống Đa';cap='QUAN';cha=5;dan_so=350000},
    [pscustomobject]@{id=8;ma='DN';ten='Đà Nẵng';cap='TINH';cha=$null;dan_so=$null},
    [pscustomobject]@{id=9;ma='DN-HC';ten='Quận Hải Châu';cap='QUAN';cha=8;dan_so=220000},
    [pscustomobject]@{id=10;ma='DN-TK';ten='Quận Thanh Khê';cap='QUAN';cha=8;dan_so=180000}
)

$nhaThuoc = @(
    [pscustomobject]@{id=1;ma='NT001';ten='Nhà thuốc An Khang Q1';dia_chi='12 Nguyễn Huệ, Q.1';kv=2;sdt='02838221001';ngay=[datetime]'2022-01-15'},
    [pscustomobject]@{id=2;ma='NT002';ten='Nhà thuốc An Khang Bình Thạnh';dia_chi='45 Đinh Bộ Lĩnh, Q.BT';kv=3;sdt='02835108002';ngay=[datetime]'2022-03-01'},
    [pscustomobject]@{id=3;ma='NT003';ten='Nhà thuốc An Khang Tân Bình';dia_chi='88 Cộng Hòa, Q.TB';kv=4;sdt='02838443003';ngay=[datetime]'2022-06-10'},
    [pscustomobject]@{id=4;ma='NT004';ten='Nhà thuốc Minh Châu HK';dia_chi='5 Hàng Trống, Hoàn Kiếm';kv=6;sdt='02439261004';ngay=[datetime]'2021-11-20'},
    [pscustomobject]@{id=5;ma='NT005';ten='Nhà thuốc Minh Châu ĐĐ';dia_chi='23 Tôn Đức Thắng, Đống Đa';kv=7;sdt='02435624005';ngay=[datetime]'2022-02-14'},
    [pscustomobject]@{id=6;ma='NT006';ten='Nhà thuốc Minh Châu ĐĐ2';dia_chi='67 Khâm Thiên, Đống Đa';kv=7;sdt='02438856006';ngay=[datetime]'2022-08-01'},
    [pscustomobject]@{id=7;ma='NT007';ten='Nhà thuốc Đà Nẵng Hải Châu';dia_chi='10 Trần Phú, Hải Châu';kv=9;sdt='02363823007';ngay=[datetime]'2022-01-05'},
    [pscustomobject]@{id=8;ma='NT008';ten='Nhà thuốc Đà Nẵng Thanh Khê';dia_chi='34 Điện Biên Phủ, Thanh Khê';kv=10;sdt='02363612008';ngay=[datetime]'2022-09-01'}
)

# vai_tro, nha_thuoc_id(null=QUANTRI), email, ho_ten
$nguoiDung = @(
    # QUANTRI (nha_thuoc_id NULL)
    [pscustomobject]@{id=1;ht='Nguyễn Thị Quản Trị';email='qt01@nhathuoc.vn';vt='QUANTRI';nt=$null;cc=$null},
    [pscustomobject]@{id=2;ht='Trần Văn Quản Trị';email='qt02@nhathuoc.vn';vt='QUANTRI';nt=$null;cc=$null},
    # QUANLY (mỗi nhà thuốc 1)
    [pscustomobject]@{id=3;ht='Lê Thị Quản Lý';email='ql01@nhathuoc.vn';vt='QUANLY';nt=1;cc=$null},
    [pscustomobject]@{id=4;ht='Phạm Văn Quản Lý';email='ql02@nhathuoc.vn';vt='QUANLY';nt=2;cc=$null},
    [pscustomobject]@{id=5;ht='Hoàng Thị Quản Lý';email='ql03@nhathuoc.vn';vt='QUANLY';nt=3;cc=$null},
    [pscustomobject]@{id=6;ht='Đinh Văn Quản Lý';email='ql04@nhathuoc.vn';vt='QUANLY';nt=4;cc=$null},
    [pscustomobject]@{id=7;ht='Bùi Thị Quản Lý';email='ql05@nhathuoc.vn';vt='QUANLY';nt=5;cc=$null},
    [pscustomobject]@{id=8;ht='Vũ Văn Quản Lý';email='ql06@nhathuoc.vn';vt='QUANLY';nt=6;cc=$null},
    [pscustomobject]@{id=9;ht='Đặng Thị Quản Lý';email='ql07@nhathuoc.vn';vt='QUANLY';nt=7;cc=$null},
    [pscustomobject]@{id=10;ht='Ngô Văn Quản Lý';email='ql08@nhathuoc.vn';vt='QUANLY';nt=8;cc=$null},
    # DUOCSI (NT001,NT002,NT003,NT004,NT005)
    [pscustomobject]@{id=11;ht='Trần Thị Dược Sĩ';email='ds01@nhathuoc.vn';vt='DUOCSI';nt=1;cc='DS-001/2020'},
    [pscustomobject]@{id=12;ht='Lê Văn Dược Sĩ';email='ds02@nhathuoc.vn';vt='DUOCSI';nt=2;cc='DS-002/2021'},
    [pscustomobject]@{id=13;ht='Nguyễn Thị Dược';email='ds03@nhathuoc.vn';vt='DUOCSI';nt=3;cc='DS-003/2019'},
    [pscustomobject]@{id=14;ht='Phạm Văn Dược';email='ds04@nhathuoc.vn';vt='DUOCSI';nt=4;cc='DS-004/2022'},
    [pscustomobject]@{id=15;ht='Hoàng Thị Dược';email='ds05@nhathuoc.vn';vt='DUOCSI';nt=5;cc='DS-005/2020'},
    # THUKHO (NT001,NT002,NT003,NT006,NT007)
    [pscustomobject]@{id=16;ht='Lý Văn Thủ Kho';email='tk01@nhathuoc.vn';vt='THUKHO';nt=1;cc=$null},
    [pscustomobject]@{id=17;ht='Mai Thị Thủ Kho';email='tk02@nhathuoc.vn';vt='THUKHO';nt=2;cc=$null},
    [pscustomobject]@{id=18;ht='Cao Văn Thủ Kho';email='tk03@nhathuoc.vn';vt='THUKHO';nt=3;cc=$null},
    [pscustomobject]@{id=19;ht='Tô Thị Thủ Kho';email='tk06@nhathuoc.vn';vt='THUKHO';nt=6;cc=$null},
    [pscustomobject]@{id=20;ht='Dư Văn Thủ Kho';email='tk07@nhathuoc.vn';vt='THUKHO';nt=7;cc=$null}
)

# Tra nhanh: NT → THUKHO id, DUOCSI id, QUANLY id
$tkByNT  = @{}; $dsMap = @{}; $qlByNT = @{}
foreach ($u in $nguoiDung) {
    if ($u.vt -eq 'THUKHO') { $tkByNT[$u.nt]  = $u.id }
    if ($u.vt -eq 'DUOCSI') { $dsMap[$u.nt]   = $u.id }
    if ($u.vt -eq 'QUANLY') { $qlByNT[$u.nt]  = $u.id }
}
# Fallback: nhà thuốc không có DUOCSI dùng QUANLY
foreach ($nt in $nhaThuoc) {
    if (-not $dsMap.ContainsKey($nt.id)) { $dsMap[$nt.id] = $qlByNT[$nt.id] }
}

$nhomThuoc = @(
    @{id=1;ma='KS';ten='Kháng sinh'},
    @{id=2;ma='GP';ten='Hạ sốt/Giảm đau'},
    @{id=3;ma='TH';ten='Tiêu hóa'},
    @{id=4;ma='TM';ten='Tim mạch'},
    @{id=5;ma='HH';ten='Hô hấp'},
    @{id=6;ma='VT';ten='Vitamin/Bổ sung'},
    @{id=7;ma='DL';ten='Da liễu'},
    @{id=8;ma='NT';ten='Nội tiết'}
)

$nhaCungCap = @(
    @{id=1;ma='NCC001';ten='Công ty Dược Hậu Giang';mst='0100375518'},
    @{id=2;ma='NCC002';ten='Công ty Pymepharco';mst='4200150824'},
    @{id=3;ma='NCC003';ten='Công ty Traphaco';mst='0101656884'},
    @{id=4;ma='NCC004';ten='Công ty OPV';mst='0300717509'},
    @{id=5;ma='NCC005';ten='Công ty Mekophar';mst='0300399906'},
    @{id=6;ma='NCC006';ten='Công ty Imexpharm';mst='1800155119'}
)

# Danh sách 60 thuốc: id, ma, ten, hoat_chat, ham_luong, dang_bc, don_vi, nhom, can_ke_don, gia_ban_le
# can_ke_don=1 khoảng 30% (~18 thuốc)
$thuocs = @(
    # ── Kháng sinh (nhom=1, eid 1-10, ~50% kê đơn) ──
    @{id=1; ma='TH001';ten='Amoxicillin Stada 500mg';hc='Amoxicillin';hl='500mg';dbc='Viên nén';dv='Viên';nt=1;ked=1;gia=2500},
    @{id=2; ma='TH002';ten='Augmentin 625mg';hc='Amoxicillin+Clavulanate';hl='625mg';dbc='Viên nén';dv='Viên';nt=1;ked=1;gia=8500},
    @{id=3; ma='TH003';ten='Azithromycin 500mg';hc='Azithromycin';hl='500mg';dbc='Viên nén';dv='Viên';nt=1;ked=1;gia=6000},
    @{id=4; ma='TH004';ten='Ciprofloxacin 500mg';hc='Ciprofloxacin';hl='500mg';dbc='Viên nén';dv='Viên';nt=1;ked=1;gia=4500},
    @{id=5; ma='TH005';ten='Metronidazole 250mg';hc='Metronidazole';hl='250mg';dbc='Viên nén';dv='Viên';nt=1;ked=0;gia=1200},
    @{id=6; ma='TH006';ten='Doxycycline 100mg';hc='Doxycycline';hl='100mg';dbc='Viên nang';dv='Viên';nt=1;ked=1;gia=3000},
    @{id=7; ma='TH007';ten='Cefuroxime 500mg';hc='Cefuroxime';hl='500mg';dbc='Viên nén';dv='Viên';nt=1;ked=1;gia=7500},
    @{id=8; ma='TH008';ten='Erythromycin 500mg';hc='Erythromycin';hl='500mg';dbc='Viên nén';dv='Viên';nt=1;ked=0;gia=2000},
    @{id=9; ma='TH009';ten='Clarithromycin 250mg';hc='Clarithromycin';hl='250mg';dbc='Viên nén';dv='Viên';nt=1;ked=1;gia=5500},
    @{id=10;ma='TH010';ten='Trimethoprim 160mg';hc='Trimethoprim+Sulfamethoxazole';hl='160/800mg';dbc='Viên nén';dv='Viên';nt=1;ked=0;gia=1800},
    # ── Hạ sốt/Giảm đau (nhom=2) ──
    @{id=11;ma='TH011';ten='Paracetamol Stada 500mg';hc='Paracetamol';hl='500mg';dbc='Viên nén';dv='Viên';nt=2;ked=0;gia=1000},
    @{id=12;ma='TH012';ten='Ibuprofen 400mg';hc='Ibuprofen';hl='400mg';dbc='Viên nén';dv='Viên';nt=2;ked=0;gia=1500},
    @{id=13;ma='TH013';ten='Diclofenac 50mg';hc='Diclofenac';hl='50mg';dbc='Viên nén';dv='Viên';nt=2;ked=0;gia=1800},
    @{id=14;ma='TH014';ten='Meloxicam 7.5mg';hc='Meloxicam';hl='7.5mg';dbc='Viên nén';dv='Viên';nt=2;ked=1;gia=4200},
    @{id=15;ma='TH015';ten='Paracetamol 650mg Efferalgan';hc='Paracetamol';hl='650mg';dbc='Viên sủi';dv='Viên';nt=2;ked=0;gia=3500},
    @{id=16;ma='TH016';ten='Aspirin pH8 100mg';hc='Aspirin';hl='100mg';dbc='Viên nén';dv='Viên';nt=2;ked=1;gia=2200},
    @{id=17;ma='TH017';ten='Tramadol 50mg';hc='Tramadol';hl='50mg';dbc='Viên nang';dv='Viên';nt=2;ked=1;gia=5000},
    # ── Tiêu hóa (nhom=3) ──
    @{id=18;ma='TH018';ten='Omeprazole 20mg';hc='Omeprazole';hl='20mg';dbc='Viên nang';dv='Viên';nt=3;ked=0;gia=2800},
    @{id=19;ma='TH019';ten='Esomeprazole 40mg';hc='Esomeprazole';hl='40mg';dbc='Viên nang';dv='Viên';nt=3;ked=1;gia=8000},
    @{id=20;ma='TH020';ten='Domperidone 10mg';hc='Domperidone';hl='10mg';dbc='Viên nén';dv='Viên';nt=3;ked=0;gia=1600},
    @{id=21;ma='TH021';ten='Loperamide 2mg';hc='Loperamide';hl='2mg';dbc='Viên nang';dv='Viên';nt=3;ked=0;gia=1800},
    @{id=22;ma='TH022';ten='Smecta 3g';hc='Diosmectit';hl='3g';dbc='Gói bột';dv='Gói';nt=3;ked=0;gia=5000},
    @{id=23;ma='TH023';ten='Lansoprazole 30mg';hc='Lansoprazole';hl='30mg';dbc='Viên nang';dv='Viên';nt=3;ked=1;gia=6500},
    @{id=24;ma='TH024';ten='Metoclopramide 10mg';hc='Metoclopramide';hl='10mg';dbc='Viên nén';dv='Viên';nt=3;ked=0;gia=1500},
    @{id=25;ma='TH025';ten='Berberin 50mg';hc='Berberine';hl='50mg';dbc='Viên nén';dv='Viên';nt=3;ked=0;gia=800},
    # ── Tim mạch (nhom=4) ──
    @{id=26;ma='TH026';ten='Amlodipine 5mg';hc='Amlodipine';hl='5mg';dbc='Viên nén';dv='Viên';nt=4;ked=1;gia=3500},
    @{id=27;ma='TH027';ten='Atorvastatin 20mg';hc='Atorvastatin';hl='20mg';dbc='Viên nén';dv='Viên';nt=4;ked=1;gia=9500},
    @{id=28;ma='TH028';ten='Metoprolol 50mg';hc='Metoprolol';hl='50mg';dbc='Viên nén';dv='Viên';nt=4;ked=1;gia=4000},
    @{id=29;ma='TH029';ten='Lisinopril 10mg';hc='Lisinopril';hl='10mg';dbc='Viên nén';dv='Viên';nt=4;ked=1;gia=3200},
    @{id=30;ma='TH030';ten='Bisoprolol 5mg';hc='Bisoprolol';hl='5mg';dbc='Viên nén';dv='Viên';nt=4;ked=1;gia=3800},
    # ── Hô hấp (nhom=5) ──
    @{id=31;ma='TH031';ten='Salbutamol 4mg';hc='Salbutamol';hl='4mg';dbc='Viên nén';dv='Viên';nt=5;ked=0;gia=1800},
    @{id=32;ma='TH032';ten='Loratadine 10mg';hc='Loratadine';hl='10mg';dbc='Viên nén';dv='Viên';nt=5;ked=0;gia=2000},
    @{id=33;ma='TH033';ten='Cetirizine 10mg';hc='Cetirizine';hl='10mg';dbc='Viên nén';dv='Viên';nt=5;ked=0;gia=2200},
    @{id=34;ma='TH034';ten='Montelukast 10mg';hc='Montelukast';hl='10mg';dbc='Viên nén';dv='Viên';nt=5;ked=1;gia=12000},
    @{id=35;ma='TH035';ten='Bromhexine 8mg';hc='Bromhexine';hl='8mg';dbc='Viên nén';dv='Viên';nt=5;ked=0;gia=1500},
    @{id=36;ma='TH036';ten='N-Acetylcysteine 200mg';hc='N-Acetylcysteine';hl='200mg';dbc='Gói bột';dv='Gói';nt=5;ked=0;gia=4500},
    @{id=37;ma='TH037';ten='Theophylline 100mg';hc='Theophylline';hl='100mg';dbc='Viên nén';dv='Viên';nt=5;ked=1;gia=2500},
    @{id=38;ma='TH038';ten='Fexofenadine 180mg';hc='Fexofenadine';hl='180mg';dbc='Viên nén';dv='Viên';nt=5;ked=0;gia=9000},
    # ── Vitamin/Bổ sung (nhom=6) ──
    @{id=39;ma='TH039';ten='Vitamin C 1000mg';hc='Acid ascorbic';hl='1000mg';dbc='Viên sủi';dv='Viên';nt=6;ked=0;gia=3500},
    @{id=40;ma='TH040';ten='Vitamin B complex';hc='Vitamin B tổng hợp';hl='Hỗn hợp';dbc='Viên nén';dv='Viên';nt=6;ked=0;gia=2000},
    @{id=41;ma='TH041';ten='Vitamin D3 1000IU';hc='Cholecalciferol';hl='1000IU';dbc='Viên nang';dv='Viên';nt=6;ked=0;gia=4000},
    @{id=42;ma='TH042';ten='Canxi Corbiere';hc='Calcium gluconate';hl='500mg';dbc='Viên nén';dv='Viên';nt=6;ked=0;gia=1500},
    @{id=43;ma='TH043';ten='Sắt Fumarate 200mg';hc='Ferrous fumarate';hl='200mg';dbc='Viên nén';dv='Viên';nt=6;ked=0;gia=2500},
    @{id=44;ma='TH044';ten='Omega-3 1000mg';hc='Omega-3 fatty acids';hl='1000mg';dbc='Viên nang';dv='Viên';nt=6;ked=0;gia=5000},
    @{id=45;ma='TH045';ten='Kẽm gluconate 70mg';hc='Zinc gluconate';hl='70mg';dbc='Viên sủi';dv='Viên';nt=6;ked=0;gia=3000},
    # ── Da liễu (nhom=7) ──
    @{id=46;ma='TH046';ten='Clotrimazole 1% kem';hc='Clotrimazole';hl='1%';dbc='Kem bôi';dv='Tuýp';nt=7;ked=0;gia=25000},
    @{id=47;ma='TH047';ten='Betamethasone 0.1%';hc='Betamethasone';hl='0.1%';dbc='Kem bôi';dv='Tuýp';nt=7;ked=1;gia=35000},
    @{id=48;ma='TH048';ten='Hydrocortisone 1%';hc='Hydrocortisone';hl='1%';dbc='Kem bôi';dv='Tuýp';nt=7;ked=0;gia=22000},
    @{id=49;ma='TH049';ten='Acyclovir 5% kem';hc='Acyclovir';hl='5%';dbc='Kem bôi';dv='Tuýp';nt=7;ked=0;gia=45000},
    @{id=50;ma='TH050';ten='Miconazole 2%';hc='Miconazole';hl='2%';dbc='Kem bôi';dv='Tuýp';nt=7;ked=0;gia=28000},
    # ── Nội tiết (nhom=8) ──
    @{id=51;ma='TH051';ten='Metformin 500mg';hc='Metformin';hl='500mg';dbc='Viên nén';dv='Viên';nt=8;ked=1;gia=1200},
    @{id=52;ma='TH052';ten='Glibenclamide 5mg';hc='Glibenclamide';hl='5mg';dbc='Viên nén';dv='Viên';nt=8;ked=1;gia=1800},
    @{id=53;ma='TH053';ten='Levothyroxine 50mcg';hc='Levothyroxine';hl='50mcg';dbc='Viên nén';dv='Viên';nt=8;ked=1;gia=3500},
    @{id=54;ma='TH054';ten='Insulin Mixtard 30/70';hc='Insulin';hl='100IU/ml';dbc='Lọ tiêm';dv='Lọ';nt=8;ked=1;gia=180000},
    @{id=55;ma='TH055';ten='Gliclazide 80mg';hc='Gliclazide';hl='80mg';dbc='Viên nén';dv='Viên';nt=8;ked=1;gia=2500},
    # ── Thêm phổ thông không kê đơn ──
    @{id=56;ma='TH056';ten='Nexium Mups 20mg';hc='Esomeprazole';hl='20mg';dbc='Viên nén';dv='Viên';nt=3;ked=0;gia=12000},
    @{id=57;ma='TH057';ten='Aquamin Canxi';hc='Calcium carbonate';hl='600mg';dbc='Viên nén';dv='Viên';nt=6;ked=0;gia=3200},
    @{id=58;ma='TH058';ten='Telfast 180mg';hc='Fexofenadine';hl='180mg';dbc='Viên nén';dv='Viên';nt=5;ked=0;gia=15000},
    @{id=59;ma='TH059';ten='Oresol 27.9g';hc='ORS';hl='27.9g';dbc='Gói bột';dv='Gói';nt=3;ked=0;gia=6000},
    @{id=60;ma='TH060';ten='Cream Dove dưỡng ẩm';hc='Urea 5%';hl='5%';dbc='Kem bôi';dv='Tuýp';nt=7;ked=0;gia=55000}
)

# ─── BẮT ĐẦU XUẤT FILE ───────────────────────────────────────
Write-Host "Đang sinh dữ liệu seed..."
$lines = [System.Collections.Generic.List[string]]::new()

$lines.Add("-- ====================================================")
$lines.Add("-- 06_seed.sql  –  IS009  Dữ liệu giả lập")
$lines.Add("-- Sinh bởi seed_generator.ps1 vào $(Get-Date -Format 'yyyy-MM-dd HH:mm')")
$lines.Add("-- CẢNH BÁO: Chạy lại 01_schema.sql trước để làm sạch")
$lines.Add("-- ====================================================")
$lines.Add("USE IS009;")
$lines.Add("GO")
$lines.Add("-- Tắt trigger append-only để nạp nhanh (bật lại cuối file)")
$lines.Add("DISABLE TRIGGER trg_BienDongKho_ChiThem_Upd ON BienDongKho;")
$lines.Add("DISABLE TRIGGER trg_BienDongKho_ChiThem_Del ON BienDongKho;")
$lines.Add("DISABLE TRIGGER trg_AuditLog_ChiThem_Upd    ON AuditLog;")
$lines.Add("DISABLE TRIGGER trg_AuditLog_ChiThem_Del    ON AuditLog;")
$lines.Add("SET IDENTITY_INSERT CauHinh OFF;")
$lines.Add("GO")
$lines.Add("")
$lines.Add("-- Đặt IDENTITY_INSERT cho từng bảng khi cần")
$lines.Add("")

# ─── HELPER xuất INSERT ──────────────────────────────────────

function EmitSection([string]$title) {
    $lines.Add(""); $lines.Add("-- ── $title ──")
}

# ─── KHU VUC ─────────────────────────────────────────────────
EmitSection "KhuVuc"
$lines.Add("SET IDENTITY_INSERT KhuVuc ON;")
foreach ($k in $khuVuc) {
    $cha   = if ($null -eq $k.cha)   { "NULL" } else { $k.cha }
    $dan   = if ($null -eq $k.dan_so){ "NULL" } else { $k.dan_so }
    $lines.Add("INSERT INTO KhuVuc(id,ma,ten,cap,khu_vuc_cha_id,dan_so) VALUES($($k.id),N'$(Q $k.ma)',N'$(Q $k.ten)',N'$($k.cap)',$cha,$dan);")
}
$lines.Add("SET IDENTITY_INSERT KhuVuc OFF;")

# ─── NHA THUOC ───────────────────────────────────────────────
EmitSection "NhaThuoc"
$lines.Add("SET IDENTITY_INSERT NhaThuoc ON;")
foreach ($n in $nhaThuoc) {
    $lines.Add("INSERT INTO NhaThuoc(id,ma,ten,dia_chi,khu_vuc_id,so_dien_thoai,trang_thai,ngay_khai_truong) VALUES($($n.id),N'$(Q $n.ma)',N'$(Q $n.ten)',N'$(Q $n.dia_chi)',$($n.kv),N'$($n.sdt)',N'HOAT_DONG',$(FmtD $n.ngay));")
}
$lines.Add("SET IDENTITY_INSERT NhaThuoc OFF;")

# ─── NGUOI DUNG ──────────────────────────────────────────────
EmitSection "NguoiDung"
$lines.Add("SET IDENTITY_INSERT NguoiDung ON;")
foreach ($u in $nguoiDung) {
    $nt  = if ($null -eq $u.nt) { "NULL" } else { $u.nt }
    $cc  = if ($null -eq $u.cc) { "NULL" } else { "N'$(Q $u.cc)'" }
    $lines.Add("INSERT INTO NguoiDung(id,ho_ten,email,password_hash,vai_tro,nha_thuoc_id,so_chung_chi_hanh_nghe,kich_hoat,created_at) VALUES($($u.id),N'$(Q $u.ht)',N'$(Q $u.email)',N'$(Q $PW_HASH)',N'$($u.vt)',$nt,$cc,1,SYSUTCDATETIME());")
}
$lines.Add("SET IDENTITY_INSERT NguoiDung OFF;")

# ─── NHOM THUOC ──────────────────────────────────────────────
EmitSection "NhomThuoc"
$lines.Add("SET IDENTITY_INSERT NhomThuoc ON;")
foreach ($n in $nhomThuoc) {
    $lines.Add("INSERT INTO NhomThuoc(id,ma,ten) VALUES($($n.id),N'$(Q $n.ma)',N'$(Q $n.ten)');")
}
$lines.Add("SET IDENTITY_INSERT NhomThuoc OFF;")

# ─── NHA CUNG CAP ────────────────────────────────────────────
EmitSection "NhaCungCap"
$lines.Add("SET IDENTITY_INSERT NhaCungCap ON;")
foreach ($n in $nhaCungCap) {
    $lines.Add("INSERT INTO NhaCungCap(id,ma,ten,ma_so_thue,trang_thai) VALUES($($n.id),N'$(Q $n.ma)',N'$(Q $n.ten)',N'$(Q $n.mst)',N'HOAT_DONG');")
}
$lines.Add("SET IDENTITY_INSERT NhaCungCap OFF;")

# ─── THUOC ───────────────────────────────────────────────────
EmitSection "Thuoc"
$lines.Add("SET IDENTITY_INSERT Thuoc ON;")
foreach ($t in $thuocs) {
    $lines.Add("INSERT INTO Thuoc(id,ma,ten,hoat_chat,ham_luong,dang_bao_che,don_vi_tinh,nhom_thuoc_id,can_ke_don,gia_ban_le,trang_thai) VALUES($($t.id),N'$(Q $t.ma)',N'$(Q $t.ten)',N'$(Q $t.hc)',N'$(Q $t.hl)',N'$(Q $t.dbc)',N'$(Q $t.dv)',$($t.nt),$($t.ked),$($t.gia),N'DANG_BAN');")
}
$lines.Add("SET IDENTITY_INSERT Thuoc OFF;")

# ─── MA VACH THUOC ───────────────────────────────────────────
EmitSection "MaVachThuoc"
$lines.Add("SET IDENTITY_INSERT MaVachThuoc ON;")
$mvId = 1
# Tạo EAN-13 duy nhất
$usedEAN = @{}
foreach ($t in $thuocs) {
    # Dùng vòng lặp để đảm bảo unique
    do { $ean = MakeEAN13 } while ($usedEAN.ContainsKey($ean))
    $usedEAN[$ean] = 1
    $lines.Add("INSERT INTO MaVachThuoc(id,thuoc_id,ma_vach,loai_ma_vach,la_ma_chinh) VALUES($mvId,$($t.id),N'$ean',N'EAN13',1);")
    $mvId++
    # ~40% có thêm CODE128 nội bộ
    if ($RAND.NextDouble() -lt 0.4) {
        $c128 = MakeCODE128 "IS009" $t.id
        $lines.Add("INSERT INTO MaVachThuoc(id,thuoc_id,ma_vach,loai_ma_vach,la_ma_chinh) VALUES($mvId,$($t.id),N'$c128',N'CODE128',0);")
        $mvId++
    }
}
$lines.Add("SET IDENTITY_INSERT MaVachThuoc OFF;")

# ─── KHACH HANG (300 khách) ──────────────────────────────────
EmitSection "KhachHang (300 khách)"
$lines.Add("SET IDENTITY_INSERT KhachHang ON;")
$hoList  = @('Nguyễn','Trần','Lê','Phạm','Hoàng','Phan','Vũ','Đặng','Bùi','Đỗ','Hồ','Ngô','Dương','Lý')
$tenList = @('Văn An','Thị Bình','Văn Cường','Thị Dung','Văn Em','Thị Phương','Văn Giang','Thị Hoa','Văn Inh','Thị Lan','Văn Minh','Thị Nga','Văn Oanh','Thị Phúc','Văn Quang','Thị Rằng','Văn Sơn','Thị Thúy','Văn Uy','Thị Vân','Văn Xuân','Thị Yến')
$allKvQuan = $khuVuc | Where-Object { $_.cap -eq 'QUAN' } | ForEach-Object { $_.id }
$khachIds = 1..300
foreach ($kid in $khachIds) {
    $ho  = Pick $hoList; $ten = Pick $tenList
    $kv  = Pick $allKvQuan
    $gt  = Pick @('Nam','Nữ')
    $ns  = RandInt 1950 2005
    $sdt = "09{0:D9}" -f $RAND.Next(100000000, 999999999)
    $lines.Add("INSERT INTO KhachHang(id,ma,ho_ten,so_dien_thoai,gioi_tinh,nam_sinh,khu_vuc_id) VALUES($kid,N'KH{0:D4}',N'$(Q "$ho $ten")',N'$sdt',N'$gt',$ns,$kv);" -f $kid)
}
$lines.Add("SET IDENTITY_INSERT KhachHang OFF;")

# ─── DINH MUC TON ────────────────────────────────────────────
EmitSection "DinhMucTon"
$lines.Add("SET IDENTITY_INSERT DinhMucTon ON;")
$dmId = 1
# Mỗi nhà thuốc cài định mức cho ~30 thuốc phổ thông
$thuocPhoThong = $thuocs | Where-Object { $_.ked -eq 0 } | ForEach-Object { $_.id }
foreach ($nt in $nhaThuoc) {
    foreach ($tid in $thuocPhoThong) {
        $min = RandInt 10 30; $max = $min + RandInt 50 150; $ngay = RandInt 2 5
        $lines.Add("INSERT INTO DinhMucTon(id,nha_thuoc_id,thuoc_id,ton_toi_thieu,ton_toi_da,so_ngay_giao_hang) VALUES($dmId,$($nt.id),$tid,$min,$max,$ngay);")
        $dmId++
    }
}
$lines.Add("SET IDENTITY_INSERT DinhMucTon OFF;")

Write-Host "  Đã sinh: KhuVuc, NhaThuoc, NguoiDung, NhomThuoc, NhaCungCap, Thuoc, MaVachThuoc, KhachHang, DinhMucTon"
$lines.Add("GO")
$lines.Add("PRINT N'06_seed_p1: Dữ liệu tĩnh đã nạp xong.';")

# Lưu 06_seed_p1.sql (nếu chạy với -RunSql sẽ thực thi luôn chuỗi 01 -> 07)
if ($RunSql) {
    $sqlFiles = @(
        "01_schema.sql",
        "02_constraints_indexes.sql",
        "03a_functions_banhang.sql",
        "03b_procedures_kho_donthuoc.sql",
        "04_triggers.sql",
        "05_views_thongke.sql",
        "06_seed_p1.sql",
        "06_seed_p2.sql",
        "07_security.sql"
    )
    foreach ($f in $sqlFiles) {
        $p = Join-Path $PSScriptRoot $f
        Write-Host "Đang chạy $f trên $Server..." -ForegroundColor Cyan
        & sqlcmd -S $Server -E -b -i $p
        if ($LASTEXITCODE -ne 0) {
            throw "Lỗi khi thực thi $f (exit code $LASTEXITCODE)"
        }
    }
    Write-Host "Hoàn tất khởi tạo và nạp dữ liệu IS009!" -ForegroundColor Green
} else {
    Write-Host "Hoàn tất kiểm tra generator ($(($lines | Measure-Object).Count) dòng SQL tĩnh). Dùng -RunSql để nạp thẳng vào SQL Server." -ForegroundColor Green
}
