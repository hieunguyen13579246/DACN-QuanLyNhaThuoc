'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const modalEl = document.getElementById('modalDinhMuc');
    const modal = modalEl ? new bootstrap.Modal(modalEl) : null;

    document.getElementById('btnMoModalDinhMuc')?.addEventListener('click', () => {
        document.getElementById('formDinhMuc').reset();
        if (modal) modal.show();
    });

    document.querySelectorAll('.btn-sua-dm').forEach(btn => {
        btn.addEventListener('click', () => {
            const ntSel = document.getElementById('dmNhaThuocId');
            if (ntSel && btn.dataset.ntId) {
                ntSel.value = btn.dataset.ntId;
            }
            document.getElementById('dmThuocId').value = btn.dataset.thuocId || '';
            document.getElementById('dmTonMin').value = btn.dataset.min || 20;
            document.getElementById('dmTonMax').value = btn.dataset.max || 150;
            document.getElementById('dmLeadTime').value = btn.dataset.lead || 3;
            if (modal) modal.show();
        });
    });

    document.getElementById('formDinhMuc')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const ntSel = document.getElementById('dmNhaThuocId');
        const nhaThuocId = ntSel ? parseInt(ntSel.value, 10) : null;
        const thuocId = parseInt(document.getElementById('dmThuocId').value, 10);
        const tonToiThieu = parseInt(document.getElementById('dmTonMin').value, 10);
        const tonToiDa = parseInt(document.getElementById('dmTonMax').value, 10);
        const soNgayGiaoHang = parseInt(document.getElementById('dmLeadTime').value, 10);

        try {
            const res = await IS009.apiPost('/api/kho/dinh-muc', {
                nhaThuocId, thuocId, tonToiThieu, tonToiDa, soNgayGiaoHang
            });
            IS009.toast(res.thongBao || 'Đã lưu định mức tồn kho', 'success');
            if (modal) modal.hide();
            setTimeout(() => window.location.reload(), 600);
        } catch (err) {
            IS009.toast(err.message, 'danger');
        }
    });
});
