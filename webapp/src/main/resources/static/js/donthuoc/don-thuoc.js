'use strict';

document.addEventListener('DOMContentLoaded', () => {
    let danhSachThuocCache = [];

    const modalChiTietEl = document.getElementById('modalChiTietDon');
    const modalChiTiet = modalChiTietEl ? new bootstrap.Modal(modalChiTietEl) : null;

    const modalTaoDonEl = document.getElementById('modalTaoDon');
    const modalTaoDon = modalTaoDonEl ? new bootstrap.Modal(modalTaoDonEl) : null;

    // Xem chi tiết đơn
    document.querySelectorAll('.btn-xem-don').forEach(btn => {
        btn.addEventListener('click', async () => {
            const id = btn.dataset.id;
            try {
                const res = await IS009.apiGet(`/api/donthuoc/${id}`);
                const dt = res.duLieu;
                document.getElementById('ctMaDon').textContent = dt.maDon;
                document.getElementById('ctBacSi').textContent = dt.tenBacSi || '';
                document.getElementById('ctCoSo').textContent = dt.coSoKham || '';
                document.getElementById('ctKhachHang').textContent = dt.tenKhachHang
                    ? `${dt.tenKhachHang} (${dt.soDienThoaiKhach || ''})` : 'Khách lẻ';
                document.getElementById('ctNgayKe').textContent = `${dt.ngayKeDon || ''} (Hết hạn: ${dt.ngayHetHieuLuc || ''})`;
                document.getElementById('ctChanDoan').textContent = dt.chanDoan || '';

                const tbody = document.getElementById('tbodyChiTietDon');
                tbody.innerHTML = '';
                (dt.chiTiet || []).forEach(c => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td class="font-monospace">${IS009.escapeHtml(c.maThuoc)}</td>
                        <td class="fw-semibold">${IS009.escapeHtml(c.tenThuoc)}</td>
                        <td>${IS009.escapeHtml(c.donViTinh)}</td>
                        <td class="text-end">${c.soLuongKe}</td>
                        <td class="text-end text-primary fw-semibold">${c.soLuongDaBan}</td>
                        <td class="text-end ${c.soLuongConLai > 0 ? 'text-success fw-bold' : 'text-muted'}">${c.soLuongConLai}</td>
                        <td>${IS009.escapeHtml(c.lieuDung || '')}</td>
                    `;
                    tbody.appendChild(tr);
                });
                if (modalChiTiet) modalChiTiet.show();
            } catch (e) {
                IS009.toast(e.message, 'danger');
            }
        });
    });

    // Hủy đơn thuốc
    document.querySelectorAll('.btn-huy-don').forEach(btn => {
        btn.addEventListener('click', async () => {
            const id = btn.dataset.id;
            const ma = btn.dataset.ma;
            if (!confirm(`Xác nhận hủy đơn thuốc ${ma}? (Chỉ đơn chưa bán mới được hủy)`)) return;
            try {
                const res = await IS009.apiPost(`/api/donthuoc/${id}/huy`, {});
                IS009.toast(res.thongBao || 'Đã hủy đơn thuốc', 'success');
                setTimeout(() => window.location.reload(), 600);
            } catch (e) {
                IS009.toast(e.message, 'danger');
            }
        });
    });

    // Mở modal tạo đơn mới
    const btnMoModalTaoDon = document.getElementById('btnMoModalTaoDon');
    if (btnMoModalTaoDon) {
        btnMoModalTaoDon.addEventListener('click', async () => {
            if (danhSachThuocCache.length === 0) {
                try {
                    const res = await IS009.apiGet('/api/thuoc/tim-kiem?q=');
                    danhSachThuocCache = res.duLieu || [];
                } catch (e) {
                    IS009.toast('Không tải được danh mục thuốc: ' + e.message, 'danger');
                }
            }
            document.getElementById('formTaoDon').reset();
            document.getElementById('donKhachHangId').value = '';
            document.getElementById('donTenKhachHienThi').textContent = '';
            document.getElementById('donNgayKe').value = new Date().toISOString().substring(0, 10);
            const tbody = document.getElementById('tbodyDongDonMoi');
            tbody.innerHTML = '';
            themDongThuocDon();
            if (modalTaoDon) modalTaoDon.show();
        });
    }

    function themDongThuocDon() {
        const tbody = document.getElementById('tbodyDongDonMoi');
        if (!tbody) return;
        const tr = document.createElement('tr');
        const optionsHtml = danhSachThuocCache.map(t =>
            `<option value="${t.id}">${IS009.escapeHtml(t.maThuoc)} - ${IS009.escapeHtml(t.tenThuoc)} (${IS009.escapeHtml(t.donViTinh)})${t.thuocKeDon ? ' [Rx]' : ''}</option>`
        ).join('');
        tr.innerHTML = `
            <td>
                <select class="form-select form-select-sm sel-thuoc-don" required>
                    <option value="">-- Chọn thuốc --</option>
                    ${optionsHtml}
                </select>
            </td>
            <td>
                <input type="number" class="form-control form-control-sm inp-sl-ke" min="1" value="10" required>
            </td>
            <td>
                <input type="text" class="form-control form-control-sm inp-lieu-dung" placeholder="VD: Ngày 2 lần, mỗi lần 1 viên sau ăn">
            </td>
            <td class="text-center">
                <button type="button" class="btn btn-sm btn-outline-danger btn-xoa-dong-don"><i class="bi bi-trash"></i></button>
            </td>
        `;
        tr.querySelector('.btn-xoa-dong-don').addEventListener('click', () => {
            if (tbody.querySelectorAll('tr').length > 1) {
                tr.remove();
            } else {
                IS009.toast('Đơn thuốc phải có ít nhất 1 dòng thuốc', 'warning');
            }
        });
        tbody.appendChild(tr);
    }

    const btnThemDongDon = document.getElementById('btnThemDongDon');
    if (btnThemDongDon) {
        btnThemDongDon.addEventListener('click', themDongThuocDon);
    }

    // Tìm khách hàng theo SĐT trong modal tạo đơn
    const btnTimKhachDon = document.getElementById('btnTimKhachDon');
    if (btnTimKhachDon) {
        btnTimKhachDon.addEventListener('click', async () => {
            const sdt = document.getElementById('donSdtKhach').value.trim();
            if (!sdt) return;
            try {
                const res = await IS009.apiGet(`/api/khachhang/tim-sdt?sdt=${encodeURIComponent(sdt)}`);
                const list = res.duLieu || [];
                if (list.length > 0) {
                    const kh = list[0];
                    document.getElementById('donKhachHangId').value = kh.id;
                    document.getElementById('donTenKhachHienThi').textContent = `Đã chọn: ${kh.hoTen} (${kh.soDienThoai})`;
                } else {
                    document.getElementById('donKhachHangId').value = '';
                    document.getElementById('donTenKhachHienThi').textContent = 'Không tìm thấy SĐT này — vui lòng thêm khách hàng trước tại M05';
                }
            } catch (e) {
                IS009.toast(e.message, 'danger');
            }
        });
    }

    // Lưu đơn thuốc
    const formTaoDon = document.getElementById('formTaoDon');
    if (formTaoDon) {
        formTaoDon.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            const khIdRaw = document.getElementById('donKhachHangId').value;
            if (!khIdRaw) {
                IS009.toast('Vui lòng nhập SĐT và nhấn "Tìm SĐT" để chọn khách hàng của đơn thuốc!', 'warning');
                return;
            }
            const rows = document.querySelectorAll('#tbodyDongDonMoi tr');
            const items = [];
            for (const r of rows) {
                const thuocId = parseInt(r.querySelector('.sel-thuoc-don').value, 10);
                const soLuongKe = parseInt(r.querySelector('.inp-sl-ke').value, 10);
                const lieuDung = r.querySelector('.inp-lieu-dung').value.trim();
                if (!thuocId || !soLuongKe || soLuongKe <= 0) {
                    IS009.toast('Vui lòng chọn thuốc và nhập số lượng kê > 0', 'warning');
                    return;
                }
                items.push({ thuocId, soLuongKe, lieuDung });
            }

            const soNgayRaw = document.getElementById('donSoNgayHieuLuc')?.value;
            const payload = {
                khachHangId: parseInt(khIdRaw, 10),
                coSoKham: document.getElementById('donCoSoKham').value.trim(),
                tenBacSi: document.getElementById('donBacSi').value.trim(),
                chanDoan: document.getElementById('donChanDoan').value.trim(),
                ngayKeDon: document.getElementById('donNgayKe').value,
                soNgayHieuLuc: soNgayRaw ? parseInt(soNgayRaw, 10) : 5,
                items
            };

            try {
                const res = await IS009.apiPost('/api/donthuoc', payload);
                IS009.toast(res.thongBao || 'Đã tiếp nhận đơn thuốc thành công', 'success');
                if (modalTaoDon) modalTaoDon.hide();
                setTimeout(() => window.location.reload(), 600);
            } catch (e) {
                IS009.toast(e.message, 'danger');
            }
        });
    }
});
