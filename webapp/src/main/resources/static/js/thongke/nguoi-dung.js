'use strict';

document.addEventListener('DOMContentLoaded', async () => {
    const modalEl = document.getElementById('modalNguoiDung');
    const modal = modalEl ? new bootstrap.Modal(modalEl) : null;
    const selNhaThuoc = document.getElementById('ndNhaThuocId');

    // Tải danh sách nhà thuốc cho select
    try {
        const res = await IS009.apiGet('/api/nhathuoc');
        const list = res.duLieu || [];
        list.forEach(nt => {
            const opt = document.createElement('option');
            opt.value = nt.id;
            opt.textContent = `${nt.ma} - ${nt.ten}`;
            selNhaThuoc.appendChild(opt);
        });
    } catch (e) {
        // ignore
    }

    document.getElementById('btnMoThemNguoiDung')?.addEventListener('click', () => {
        document.getElementById('formNguoiDung').reset();
        document.getElementById('ndId').value = '';
        document.getElementById('ndKichHoat').checked = true;
        document.getElementById('modalNdTitle').textContent = 'Thêm người dùng mới';
        if (modal) modal.show();
    });

    document.querySelectorAll('.btn-sua-nd').forEach(btn => {
        btn.addEventListener('click', () => {
            document.getElementById('ndId').value = btn.dataset.id;
            document.getElementById('ndHoTen').value = btn.dataset.hoten || '';
            document.getElementById('ndEmail').value = btn.dataset.email || '';
            document.getElementById('ndMatKhau').value = '';
            document.getElementById('ndVaiTro').value = btn.dataset.vaitro || 'DUOCSI';
            selNhaThuoc.value = btn.dataset.ntid || '';
            document.getElementById('ndCchn').value = btn.dataset.cchn || '';
            document.getElementById('ndKichHoat').checked = btn.dataset.kichhoat === 'true';
            document.getElementById('modalNdTitle').textContent = 'Cập nhật thông tin người dùng';
            if (modal) modal.show();
        });
    });

    document.getElementById('formNguoiDung')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('ndId').value;
        const vaiTro = document.getElementById('ndVaiTro').value;
        const ntRaw = selNhaThuoc.value;
        const payload = {
            hoTen: document.getElementById('ndHoTen').value.trim(),
            email: document.getElementById('ndEmail').value.trim(),
            matKhau: document.getElementById('ndMatKhau').value || null,
            vaiTro,
            nhaThuocId: vaiTro === 'QUANTRI' ? null : (ntRaw ? parseInt(ntRaw, 10) : null),
            soChungChiHanhNghe: document.getElementById('ndCchn').value.trim() || null,
            kichHoat: document.getElementById('ndKichHoat').checked
        };

        try {
            const res = id
                ? await IS009.apiPut(`/api/nguoidung/${id}`, payload)
                : await IS009.apiPost('/api/nguoidung', payload);
            IS009.toast(res.thongBao || 'Đã lưu người dùng', 'success');
            if (modal) modal.hide();
            setTimeout(() => window.location.reload(), 600);
        } catch (err) {
            IS009.toast(err.message, 'danger');
        }
    });
});
