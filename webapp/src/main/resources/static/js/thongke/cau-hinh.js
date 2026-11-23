'use strict';

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.btn-luu-ch').forEach(btn => {
        btn.addEventListener('click', async () => {
            const khoa = btn.dataset.khoa;
            const tr = btn.closest('tr');
            const giaTri = tr.querySelector('.inp-ch-giatri').value.trim();
            const moTa = tr.querySelector('.inp-ch-mota').value.trim();
            if (!giaTri) {
                IS009.toast('Giá trị cấu hình không được để trống', 'warning');
                return;
            }
            try {
                const res = await IS009.apiPut(`/api/cauhinh/${encodeURIComponent(khoa)}`, { giaTri, moTa });
                IS009.toast(res.thongBao || `Đã lưu cấu hình ${khoa}`, 'success');
            } catch (err) {
                IS009.toast(err.message, 'danger');
            }
        });
    });
});
