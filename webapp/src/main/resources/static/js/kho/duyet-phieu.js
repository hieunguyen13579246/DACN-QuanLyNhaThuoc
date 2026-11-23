'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const modalTcEl = document.getElementById('modalTuChoiPhieu');
    const modalTc = modalTcEl ? new bootstrap.Modal(modalTcEl) : null;

    const modalXemEl = document.getElementById('modalXemPhieuDuyet');
    const modalXem = modalXemEl ? new bootstrap.Modal(modalXemEl) : null;

    // Duyệt ngay
    document.querySelectorAll('.btn-duyet-ngay').forEach(btn => {
        btn.addEventListener('click', async () => {
            const id = btn.dataset.id;
            const ma = btn.dataset.ma;
            if (!confirm(`Xác nhận DUYỆT phiếu kho ${ma}? Tồn kho sẽ được cập nhật chính thức.`)) return;
            try {
                const res = await IS009.apiPost(`/api/kho/phieu/${id}/duyet`, { ketQua: 'DUYET', lyDo: '' });
                IS009.toast(res.thongBao || 'Đã duyệt phiếu kho', 'success');
                setTimeout(() => window.location.reload(), 600);
            } catch (err) {
                IS009.toast(err.message, 'danger');
            }
        });
    });

    // Mở modal từ chối
    document.querySelectorAll('.btn-mo-tuchoi').forEach(btn => {
        btn.addEventListener('click', () => {
            document.getElementById('tcPhieuId').value = btn.dataset.id;
            document.getElementById('tcMaPhieu').textContent = btn.dataset.ma;
            document.getElementById('tcLyDoInput').value = '';
            if (modalTc) modalTc.show();
        });
    });

    document.getElementById('formTuChoiPhieu')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('tcPhieuId').value;
        const lyDo = document.getElementById('tcLyDoInput').value.trim();
        if (!lyDo) {
            IS009.toast('Bắt buộc nhập lý do từ chối phiếu kho!', 'warning');
            return;
        }
        try {
            const res = await IS009.apiPost(`/api/kho/phieu/${id}/duyet`, { ketQua: 'TU_CHOI', lyDo });
            IS009.toast(res.thongBao || 'Đã từ chối phiếu kho', 'success');
            if (modalTc) modalTc.hide();
            setTimeout(() => window.location.reload(), 600);
        } catch (err) {
            IS009.toast(err.message, 'danger');
        }
    });

    // Xem chi tiết phiếu
    document.querySelectorAll('.btn-xem-duyet').forEach(btn => {
        btn.addEventListener('click', async () => {
            const id = btn.dataset.id;
            try {
                const res = await IS009.apiGet(`/api/kho/phieu/${id}`);
                const p = res.duLieu;
                document.getElementById('dpMaPhieu').textContent = p.maPhieu;
                document.getElementById('dpLoaiPhieu').textContent = p.loaiPhieu;
                document.getElementById('dpTrangThai').textContent = p.trangThai;
                document.getElementById('dpNhaThuoc').textContent = p.tenNhaThuoc;
                document.getElementById('dpDoiTac').textContent =
                    p.tenNhaThuocDich || p.tenNhaCungCap || (p.lyDoHuy ? `Lý do hủy: ${p.lyDoHuy}` : '-');
                document.getElementById('dpGhiChu').textContent =
                    [p.ghiChu, p.lyDoTuChoi ? `Lý do từ chối: ${p.lyDoTuChoi}` : ''].filter(Boolean).join(' | ');

                const tbody = document.getElementById('tbodyChiTietDuyet');
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
                if (modalXem) modalXem.show();
            } catch (err) {
                IS009.toast(err.message, 'danger');
            }
        });
    });
});
