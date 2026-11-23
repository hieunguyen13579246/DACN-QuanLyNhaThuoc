param(
    [string]$ServerInstance = "localhost",
    [switch]$RunTests
)

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$ErrorActionPreference = "Stop"

if (-not (Get-Command sqlcmd -ErrorAction SilentlyContinue)) {
    Write-Error "Không tìm thấy lệnh 'sqlcmd' trong PATH. Vui lòng cài đặt SQL Server Command Line Utilities (sqlcmd) trước khi chạy."
    exit 1
}

$scripts = @(
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

if ($RunTests) {
    $scripts += "08_kiem_thu_kich_ban.sql"
}

foreach ($file in $scripts) {
    $path = Join-Path $PSScriptRoot $file
    if (-not (Test-Path $path)) {
        Write-Error "Không tìm thấy file script: $path"
        exit 1
    }

    Write-Host ">>> Đang thực thi $file trên [$ServerInstance]..." -ForegroundColor Cyan
    & sqlcmd -S $ServerInstance -E -f 65001 -b -i $path
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Lỗi khi thực thi $file (ExitCode: $LASTEXITCODE)."
        exit $LASTEXITCODE
    }
}

Write-Host ">>> Khởi tạo cơ sở dữ liệu IS009 hoàn tất thành công!" -ForegroundColor Green
