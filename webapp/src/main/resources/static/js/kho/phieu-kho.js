'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const root = document.getElementById('phieuKhoRoot');
    const templateLoHtml = document.getElementById('pkTemplateLoOptions')?.innerHTML || '';

    const modalDcEl = document.getElementById('modalDieuChuyen');
    const modalDc = modalDcEl ? new bootstrap.Modal(modalDcEl) : null;

    const modalXhEl = document.getElementById('modalXuatHuy');
    const modalXh = modalXhEl ? new bootstrap.Modal(modalXhEl) : null;

    const modalCtEl = document.getElementById('modalChiTietPhieu');
    const modalCt = modalCtEl ? new bootstrap.Modal(modalCtEl) : null;

    function addLoRow(tbodyId, prefillLoId = '', prefillSl = 10) {
        const tbody = document.getElementById(tbodyId);
        if (!tbody) return;
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>
                <select class="form-select form-select-sm sel-pk-lo" required>
                    ${templateLoHtml}
                </select>
            </td>
            <td>
                <input type="number" class="form-control form-control-sm inp-pk-sl text-end"
                       min="1" value="${prefillSl}" required>
            </td>
            <td class="text-center">
                <button type="button" class="btn btn-sm btn-outline-danger btn-pk-xoa"><i class="bi bi-trash"></i></button>
            </td>
        `;
        if (prefillLoId) {
            tr.querySelector('.sel-pk-lo').value = String(prefillLoId);
        }
        tr.querySelector('.btn-pk-xoa').addEventListener('click', () => {
            if (tbody.querySelectorAll('tr').length > 1) {
                tr.remove();
            } else {
                IS009.toast('Phiếu phải có ít nhất 1 lô thuốc', 'warning');
            }
        });
        tbody.appendChild(tr);
    }

    document.getElementById('btnMoModalDieuChuyen')?.addEventListener('click', () => {
        document.getElementById('formDieuChuyen').reset();
        document.getElementById('tbodyDcLines').innerHTML = '';
        addLoRow('tbodyDcLines');
        if (modalDc) modalDc.show();
    });

    document.getElementById('btnDcThemDong')?.addEventListener('click', () => addLoRow('tbodyDcLines'));

    document.getElementById('btnMoModalXuatHuy')?.addEventListener('click', () => {
        document.getElementById('formXuatHuy').reset();
        document.getElementById('tbodyXhLines').innerHTML = '';
        addLoRow('tbodyXhLines');
        if (modalXh) modalXh.show();
    });

    document.getElementById('btnXhThemDong')?.addEventListener('click', () => addLoRow('tbodyXhLines'));

    // Nếu được mở từ nút Tạo nhanh phiếu ở màn Cảnh báo (M14)
    const moForm = root?.dataset.moForm;
    const prefillLo = root?.dataset.prefillLo;
    const prefillSl = root?.dataset.prefillSl || 10;
    const prefillLyDo = root?.dataset.prefillLydo;
    if (moForm === 'XUAT_HUY' && modalXh) {
        document.getElementById('tbodyXhLines').innerHTML = '';
        if (prefillLyDo) document.getElementById('xhLyDo').value = prefillLyDo;
        addLoRow('tbodyXhLines', prefillLo, prefillSl);
        modalXh.show();
    } else if (moForm === 'DIEU_CHUYEN' && modalDc) {
        document.getElementById('tbodyDcLines').innerHTML = '';
        addLoRow('tbodyDcLines', prefillLo, prefillSl);
        modalDc.show();
    }

    // Submit Điều chuyển
    document.getElementById('formDieuChuyen')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const nhaThuocDichId = parseInt(document.getElementById('dcNhaThuocDichId').value, 10);
        const ghiChu = document.getElementById('dcGhiChu').value.trim();
        const chiTiet = collectLoLines('tbodyDcLines');
        if (!nhaThuocDichId || !chiTiet) return;
        try {
            const res = await IS009.apiPost('/api/kho/dieu-chuyen', { nhaThuocDichId, ghiChu, chiTiet });
            IS009.toast(res.thongBao || 'Đã tạo phiếu điều chuyển', 'success');
            if (modalDc) modalDc.hide();
            setTimeout(() => window.location.href = '/kho/phieu-kho', 600);
        } catch (err) {
            IS009.toast(err.message, 'danger');
        }
    });

    // Submit Xuất hủy
    document.getElementById('formXuatHuy')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const lyDo = document.getElementById('xhLyDo').value;
        const ghiChu = document.getElementById('xhGhiChu').value.trim();
        const chiTiet = collectLoLines('tbodyXhLines');
        if (!lyDo || !chiTiet) return;
        try {
            const res = await IS009.apiPost('/api/kho/xuat-huy', { lyDo, ghiChu, chiTiet });
            IS009.toast(res.thongBao || 'Đã tạo phiếu xuất hủy', 'success');
            if (modalXh) modalXh.hide();
            setTimeout(() => window.location.href = '/kho/phieu-kho', 600);
        } catch (err) {
            IS009.toast(err.message, 'danger');
        }
    });

    function collectLoLines(tbodyId) {
        const rows = document.querySelectorAll(`#${tbodyId} tr`);
        const chiTiet = [];
        for (const r of rows) {
            const loThuocId = parseInt(r.querySelector('.sel-pk-lo').value, 10);
            const soLuong = parseInt(r.querySelector('.inp-pk-sl').value, 10);
            if (!loThuocId || !soLuong || soLuong <= 0) {
                IS009.toast('Vui lòng chọn lô thuốc và nhập số lượng > 0', 'warning');
                return null;
            }
            chiTiet.push({ loThuocId, soLuong });
        }
        return chiTiet;
    }

    // Xem chi tiết phiếu
    document.querySelectorAll('.btn-xem-phieu').forEach(btn => {
        btn.addEventListener('click', async () => {
            const id = btn.dataset.id;
            try {
                const res = await IS009.apiGet(`/api/kho/phieu/${id}`);
                const p = res.duLieu;
                document.getElementById('pkMaPhieu').textContent = p.maPhieu;
                document.getElementById('pkLoaiPhieu').textContent = p.loaiPhieu;
                document.getElementById('pkTrangThai').textContent = p.trangThai;
                document.getElementById('pkNhaThuoc').textContent = p.tenNhaThuoc;
                document.getElementById('pkDoiTac').textContent =
                    p.tenNhaThuocDich || p.tenNhaCungCap || (p.lyDoHuy ? `Lý do hủy: ${p.lyDoHuy}` : '-');
                document.getElementById('pkNguoiLap').textContent = `${p.tenNguoiLap || ''} (${p.thoiGianLap || ''})`;
                document.getElementById('pkNguoiDuyet').textContent = p.tenNguoiDuyet
                    ? `${p.tenNguoiDuyet} (${p.thoiGianDuyet || ''})` : 'Chưa duyệt';
                document.getElementById('pkGhiChu').textContent =
                    [p.ghiChu, p.lyDoTuChoi ? `Lý do từ chối: ${p.lyDoTuChoi}` : ''].filter(Boolean).join(' | ');

                const tbody = document.getElementById('tbodyChiTietPhieu');
                tbody.innerHTML = '';
                (p.chiTiet || []).forEach(ct => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td class="font-monospace">${IS009.escapeHtml(ct.maThuoc)}</td>
                        <td class="fw-semibold">${IS009.escapeHtml(ct.tenThuoc)} (${IS009.escapeHtml(ct.donViTinh)})</td>
                        <td class="font-monospace text-primary">${IS009.escapeHtml(ct.soLo)}</td>
                        <td>${ct.hanDung || ''}</td>
                        <td class="text-end fw-bold">${ct.soLuong}</td>
                        <td class="text-end">${IS009.formatVND(ct.donGia)}</td>
                        <td class="text-end fw-bold">${IS009.formatVND(ct.thanhTien)}</td>
                    `;
                    tbody.appendChild(tr);
                });
                if (modalCt) modalCt.show();
            } catch (err) {
                IS009.toast(err.message, 'danger');
            }
        });
    });

    // Hủy phiếu chờ duyệt
    document.querySelectorAll('.btn-huy-phieu').forEach(btn => {
        btn.addEventListener('click', async () => {
            const id = btn.dataset.id;
            const ma = btn.dataset.ma;
            if (!confirm(`Xác nhận hủy phiếu kho ${ma} (đang chờ duyệt) và giải phóng tồn giữ chỗ?`)) return;
            try {
                const res = await IS009.apiPost(`/api/kho/phieu/${id}/huy`, {});
                IS009.toast(res.thongBao || 'Đã hủy phiếu kho', 'success');
                setTimeout(() => window.location.reload(), 600);
            } catch (err) {
                IS009.toast(err.message, 'danger');
            }
        });
    });
});
