'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const modalNt = document.getElementById('modalThemNhaThuoc') ? new bootstrap.Modal(document.getElementById('modalThemNhaThuoc')) : null;
    const modalKv = document.getElementById('modalThemKhuVuc') ? new bootstrap.Modal(document.getElementById('modalThemKhuVuc')) : null;
    const modalNcc = document.getElementById('modalThemNcc') ? new bootstrap.Modal(document.getElementById('modalThemNcc')) : null;

    document.getElementById('btnMoThemNhaThuoc')?.addEventListener('click', () => {
        document.getElementById('formThemNhaThuoc').reset();
        if (modalNt) modalNt.show();
    });

    document.getElementById('btnMoThemKhuVuc')?.addEventListener('click', () => {
        document.getElementById('formThemKhuVuc').reset();
        if (modalKv) modalKv.show();
    });

    document.getElementById('btnMoThemNcc')?.addEventListener('click', () => {
        document.getElementById('formThemNcc').reset();
        if (modalNcc) modalNcc.show();
    });

    document.getElementById('formThemNhaThuoc')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const payload = {
                ma: document.getElementById('ntMa').value.trim(),
                ten: document.getElementById('ntTen').value.trim(),
                khuVucId: parseInt(document.getElementById('ntKhuVucId').value, 10),
                diaChi: document.getElementById('ntDiaChi').value.trim(),
                soDienThoai: document.getElementById('ntSdt').value.trim(),
                ngayKhaiTruong: document.getElementById('ntNgayKt').value || null,
                trangThai: 'HOAT_DONG'
            };
            const res = await IS009.apiPost('/api/nhathuoc', payload);
            IS009.toast(res.thongBao || 'Đã thêm nhà thuốc', 'success');
            setTimeout(() => window.location.reload(), 600);
        } catch (err) {
            IS009.toast(err.message, 'danger');
        }
    });

    document.getElementById('formThemKhuVuc')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const chaRaw = document.getElementById('kvChaId').value;
            const payload = {
                ma: document.getElementById('kvMa').value.trim(),
                ten: document.getElementById('kvTen').value.trim(),
                cap: document.getElementById('kvCap').value,
                danSo: parseInt(document.getElementById('kvDanSo').value, 10),
                khuVucChaId: chaRaw ? parseInt(chaRaw, 10) : null
            };
            const res = await IS009.apiPost('/api/khuvuc', payload);
            IS009.toast(res.thongBao || 'Đã thêm khu vực', 'success');
            setTimeout(() => window.location.reload(), 600);
        } catch (err) {
            IS009.toast(err.message, 'danger');
        }
    });

    document.getElementById('formThemNcc')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const payload = {
                ma: document.getElementById('nccMa').value.trim(),
                ten: document.getElementById('nccTen').value.trim(),
                maSoThue: document.getElementById('nccMst').value.trim(),
                soDienThoai: document.getElementById('nccSdt').value.trim(),
                diaChi: document.getElementById('nccDiaChi').value.trim(),
                trangThai: 'HOAT_DONG'
            };
            const res = await IS009.apiPost('/api/nhacungcap', payload);
            IS009.toast(res.thongBao || 'Đã thêm nhà cung cấp', 'success');
            setTimeout(() => window.location.reload(), 600);
        } catch (err) {
            IS009.toast(err.message, 'danger');
        }
    });
});
