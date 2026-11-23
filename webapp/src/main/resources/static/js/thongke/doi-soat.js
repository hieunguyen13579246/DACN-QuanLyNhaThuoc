'use strict';

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('btnChayDoiSoat')?.addEventListener('click', async () => {
        const ntId = document.getElementById('dsNhaThuocSelect')?.value || '';
        const url = ntId ? `/api/kho/doi-soat?nhaThuocId=${encodeURIComponent(ntId)}` : '/api/kho/doi-soat';
        try {
            const res = await IS009.apiGet(url);
            const list = res.duLieu || [];
            if (list.length === 0) {
                IS009.toast('Đối soát hoàn tất: 100% tồn kho khớp với sổ cái BienDongKho (0 chênh lệch)!', 'success');
            } else {
                IS009.toast(`Phát hiện ${list.length} lô có chênh lệch tồn kho!`, 'danger');
            }
            setTimeout(() => window.location.reload(), 700);
        } catch (err) {
            IS009.toast(err.message, 'danger');
        }
    });
});
