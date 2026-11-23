'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const root = document.getElementById('nhapKhoRoot');
    const barcodeInput = document.getElementById('nkBarcodeInput');
    const btnToggleCameraNk = document.getElementById('btnToggleCameraNk');
    const gs1Alert = document.getElementById('nkGs1InfoAlert');
    const tbody = document.getElementById('tbodyNkLines');
    const templateOptionsHtml = document.getElementById('nkTemplateThuocOptions')?.innerHTML || '';

    if (window.Is009Scanner && barcodeInput) {
        window.Is009Scanner.initScanInput(barcodeInput, { autoKeepFocus: true, clearOnScan: true });
    }

    if (btnToggleCameraNk && window.Is009Scanner) {
        btnToggleCameraNk.addEventListener('click', () => {
            window.Is009Scanner.toggleCameraScanner('nkCameraReader', btnToggleCameraNk, barcodeInput);
        });
    }

    function addLine(prefill = {}) {
        const tr = document.createElement('tr');
        const today = new Date();
        const defaultNsx = prefill.ngaySanXuat || new Date(today.getFullYear(), today.getMonth() - 1, 1).toISOString().substring(0, 10);
        const defaultHd = prefill.hanDung || new Date(today.getFullYear() + 2, today.getMonth(), 1).toISOString().substring(0, 10);

        tr.innerHTML = `
            <td>
                <select class="form-select form-select-sm sel-nk-thuoc" required>
                    ${templateOptionsHtml}
                </select>
            </td>
            <td>
                <input type="text" class="form-control form-control-sm inp-nk-solo"
                       placeholder="VD: LO-2026-01" value="${IS009.escapeHtml(prefill.soLo || '')}" required>
            </td>
            <td>
                <input type="date" class="form-control form-control-sm inp-nk-nsx" value="${defaultNsx}" required>
            </td>
            <td>
                <input type="date" class="form-control form-control-sm inp-nk-hd" value="${defaultHd}" required>
            </td>
            <td>
                <input type="number" class="form-control form-control-sm inp-nk-sl text-end"
                       min="1" value="${prefill.soLuong || 50}" required>
            </td>
            <td>
                <input type="number" class="form-control form-control-sm inp-nk-gianhap text-end"
                       min="1" step="100" value="${prefill.giaNhap || 15000}" required>
            </td>
            <td class="text-center">
                <button type="button" class="btn btn-sm btn-outline-danger btn-nk-xoa"><i class="bi bi-trash"></i></button>
            </td>
        `;
        if (prefill.thuocId) {
            tr.querySelector('.sel-nk-thuoc').value = String(prefill.thuocId);
        }
        tr.querySelector('.btn-nk-xoa').addEventListener('click', () => {
            if (tbody.querySelectorAll('tr').length > 1) {
                tr.remove();
            } else {
                IS009.toast('Phiếu nhập phải có ít nhất 1 dòng lô thuốc', 'warning');
            }
        });
        tbody.appendChild(tr);
    }

    const prefillThuocId = root?.dataset.prefillThuocId;
    const prefillSoLuong = root?.dataset.prefillSoLuong;
    if (prefillThuocId) {
        addLine({ thuocId: prefillThuocId, soLuong: prefillSoLuong || 50 });
    } else {
        addLine();
    }

    document.getElementById('btnNkThemDong')?.addEventListener('click', () => addLine());

    async function handleScannedCode(rawCode, source) {
        const code = (rawCode || '').trim();
        if (!code) return;

        try {
            let lookupBarcode = code;
            let gs1SoLo = '';
            let gs1HanDung = '';
            if (code.length > 14 || code.includes('(') || code.startsWith('01')) {
                const resGs1 = await IS009.apiGet(`/api/barcode/parse-gs1?raw=${encodeURIComponent(code)}`);
                const gs1 = resGs1.duLieu;
                if (resGs1.thanhCong && gs1) {
                    lookupBarcode = gs1.maTraCuuUuTien || gs1.ean13 || gs1.gtin || code;
                    gs1SoLo = gs1.soLo || '';
                    gs1HanDung = gs1.hanDung || '';
                }
            }

            const resThuoc = await IS009.apiGet(`/api/thuoc/ma-vach/${encodeURIComponent(lookupBarcode)}`);
            if (!resThuoc.thanhCong || !resThuoc.duLieu || !resThuoc.duLieu.thuoc) {
                IS009.toast(resThuoc.thongBao || ('Không tìm thấy thuốc cho mã: ' + lookupBarcode), 'warning');
                return;
            }

            const data = resThuoc.duLieu;
            const thuoc = data.thuoc;
            const tenThuoc = thuoc.ten || thuoc.tenThuoc || '';
            const maThuoc = thuoc.ma || thuoc.maThuoc || '';
            const autoSoLo = gs1SoLo || ('LO-' + maThuoc + '-' + new Date().toISOString().slice(2, 7).replace('-', ''));

            const rows = tbody.querySelectorAll('tr');
            const lastRow = rows[rows.length - 1];
            const lastSel = lastRow ? lastRow.querySelector('.sel-nk-thuoc') : null;
            const suggestedPrice = Math.max(500, Math.round(Number(thuoc.giaBanLe || 15000) * 0.65));

            if (lastSel && !lastSel.value) {
                lastSel.value = String(thuoc.id);
                lastRow.querySelector('.inp-nk-solo').value = autoSoLo;
                if (gs1HanDung) lastRow.querySelector('.inp-nk-hd').value = gs1HanDung;
                lastRow.querySelector('.inp-nk-gianhap').value = suggestedPrice;
            } else {
                addLine({
                    thuocId: thuoc.id,
                    soLo: autoSoLo,
                    hanDung: gs1HanDung,
                    soLuong: 50,
                    giaNhap: suggestedPrice
                });
            }

            if (gs1Alert) {
                gs1Alert.className = 'alert alert-success py-2 small mb-2';
                gs1Alert.innerHTML = `<i class="bi bi-check-circle-fill me-1"></i><strong>Đã quét mã [${IS009.escapeHtml(code)}] (${IS009.escapeHtml(source || 'SCAN')}):</strong> Đã chọn thuốc <strong>${IS009.escapeHtml(maThuoc + ' - ' + tenThuoc)}</strong> | Số lô: <strong>${IS009.escapeHtml(autoSoLo)}</strong> | Giá nhập gợi ý: <strong>${IS009.formatVND(suggestedPrice)}</strong>`;
                gs1Alert.classList.remove('d-none');
            }
            IS009.toast(`Đã điền thuốc: ${tenThuoc}`, 'success');
        } catch (err) {
            IS009.toast(err.message, 'danger');
        }
    }

    document.addEventListener('barcode-scanned', (e) => {
        const detail = e.detail || {};
        if (detail.code) {
            handleScannedCode(detail.code, detail.source);
        }
    });

    document.getElementById('btnNkParseBarcode')?.addEventListener('click', () => {
        const val = barcodeInput.value.trim();
        if (val) {
            barcodeInput.value = '';
            handleScannedCode(val, 'MANUAL');
        }
    });

    document.getElementById('btnLuuPhieuNhap')?.addEventListener('click', async () => {
        const nhaCungCapId = parseInt(document.getElementById('nkNhaCungCapId').value, 10);
        if (!nhaCungCapId) {
            IS009.toast('Vui lòng chọn nhà cung cấp!', 'warning');
            return;
        }
        const ghiChu = document.getElementById('nkGhiChu').value.trim();
        const rows = tbody.querySelectorAll('tr');
        const chiTiet = [];
        for (const r of rows) {
            const thuocId = parseInt(r.querySelector('.sel-nk-thuoc').value, 10);
            const soLo = r.querySelector('.inp-nk-solo').value.trim();
            const ngaySanXuat = r.querySelector('.inp-nk-nsx').value;
            const hanDung = r.querySelector('.inp-nk-hd').value;
            const soLuong = parseInt(r.querySelector('.inp-nk-sl').value, 10);
            const giaNhap = Number(r.querySelector('.inp-nk-gianhap').value);
            if (!thuocId || !soLo || !ngaySanXuat || !hanDung || !soLuong || soLuong <= 0 || !giaNhap || giaNhap <= 0) {
                IS009.toast('Vui lòng điền đầy đủ thông tin thuốc, số lô, NSX, hạn dùng, số lượng và giá nhập!', 'warning');
                return;
            }
            chiTiet.push({ thuocId, soLo, ngaySanXuat, hanDung, soLuong, giaNhap });
        }

        try {
            const ntSelect = document.getElementById('nkNhaThuocId');
            const nhaThuocId = ntSelect && ntSelect.value ? parseInt(ntSelect.value, 10) : null;
            const res = await IS009.apiPost('/api/kho/nhap-kho', { nhaThuocId, nhaCungCapId, ghiChu, chiTiet });
            if (!res.thanhCong) {
                IS009.toast(res.thongBao || 'Không thể lưu phiếu nhập kho', 'danger');
                return;
            }
            IS009.toast(res.thongBao || 'Đã tạo phiếu nhập kho (Chờ duyệt)!', 'success');
            setTimeout(() => window.location.reload(), 800);
        } catch (err) {
            IS009.toast(err.message, 'danger');
        }
    });
});
