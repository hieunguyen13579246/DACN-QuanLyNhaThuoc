'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const modalChiTietEl = document.getElementById('modalChiTietGD');
    const modalChiTiet = modalChiTietEl ? new bootstrap.Modal(modalChiTietEl) : null;

    const modalHuyEl = document.getElementById('modalHuyGD');
    const modalHuy = modalHuyEl ? new bootstrap.Modal(modalHuyEl) : null;

    // Xem chi tiết giao dịch
    document.querySelectorAll('.btn-xem-gd').forEach(btn => {
        btn.addEventListener('click', async () => {
            const id = btn.dataset.id;
            try {
                const res = await IS009.apiGet(`/api/banhang/${id}`);
                const gd = res.duLieu;
                document.getElementById('mdMaGD').textContent = gd.maGiaoDich;
                document.getElementById('mdNhaThuoc').textContent = gd.tenNhaThuoc;
                document.getElementById('mdThoiGian').textContent = gd.thoiGian;
                document.getElementById('mdNguoiBan').textContent = gd.tenNguoiBan;
                document.getElementById('mdKhachHang').textContent = gd.tenKhachHang || 'Khách lẻ';
                document.getElementById('mdDonThuoc').textContent = gd.maDonThuoc || 'Không kèm đơn';
                document.getElementById('mdTrangThai').textContent = gd.trangThai === 'HOAN_THANH' ? 'Hoàn thành' : 'Đã hủy';

                const huyBox = document.getElementById('mdThongTinHuy');
                if (gd.trangThai === 'DA_HUY') {
                    huyBox.textContent = `Đã hủy bởi: ${gd.tenNguoiHuy || ''} lúc ${gd.thoiGianHuy || ''} — Lý do: ${gd.lyDoHuy || ''}`;
                    huyBox.classList.remove('d-none');
                } else {
                    huyBox.classList.add('d-none');
                }

                const tbody = document.getElementById('tbodyChiTietGD');
                tbody.innerHTML = '';
                (gd.chiTiet || []).forEach(ct => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td class="font-monospace">${IS009.escapeHtml(ct.maThuoc)}</td>
                        <td class="fw-semibold">${IS009.escapeHtml(ct.tenThuoc)} (${IS009.escapeHtml(ct.donViTinh)})</td>
                        <td class="font-monospace text-primary">${IS009.escapeHtml(ct.soLo)}</td>
                        <td>${ct.hanDung || ''}</td>
                        <td class="text-end">${ct.soLuong}</td>
                        <td class="text-end">${IS009.formatVND(ct.giaBan)}</td>
                        <td class="text-end fw-bold">${IS009.formatVND(ct.thanhTien)}</td>
                    `;
                    tbody.appendChild(tr);
                });

                document.getElementById('mdTongTienHang').textContent = IS009.formatVND(gd.tongTienHang);
                document.getElementById('mdGiamGia').textContent = IS009.formatVND(gd.giamGia);
                document.getElementById('mdThanhToan').textContent = IS009.formatVND(gd.thanhToan);
                document.getElementById('btnInHoaDonModal').href = `/banhang/hoa-don/${gd.id}`;

                if (modalChiTiet) modalChiTiet.show();
            } catch (e) {
                IS009.toast(e.message, 'danger');
            }
        });
    });

    // Mở modal hủy giao dịch (M13)
    document.querySelectorAll('.btn-mo-huy-gd').forEach(btn => {
        btn.addEventListener('click', () => {
            document.getElementById('huyGiaoDichId').value = btn.dataset.id;
            document.getElementById('huyMaGD').textContent = btn.dataset.ma;
            document.getElementById('huySoTienGD').textContent = btn.dataset.tien;
            document.getElementById('huyLyDoInput').value = '';
            if (modalHuy) modalHuy.show();
        });
    });

    const formHuyGD = document.getElementById('formHuyGD');
    if (formHuyGD) {
        formHuyGD.addEventListener('submit', async (e) => {
            e.preventDefault();
            const id = document.getElementById('huyGiaoDichId').value;
            const lyDo = document.getElementById('huyLyDoInput').value.trim();
            if (!lyDo) {
                IS009.toast('Vui lòng nhập lý do hủy giao dịch', 'warning');
                return;
            }
            try {
                const res = await IS009.apiPost(`/api/banhang/huy/${id}`, { lyDo });
                IS009.toast(res.thongBao || 'Hủy giao dịch thành công', 'success');
                if (modalHuy) modalHuy.hide();
                setTimeout(() => window.location.reload(), 600);
            } catch (err) {
                IS009.toast(err.message, 'danger');
            }
        });
    }
});
