'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const cart = new Map();
    const drugNameMap = new Map();

    const barcodeInput = document.getElementById('posBarcodeInput');
    const btnPosScanManual = document.getElementById('btnPosScanManual');
    const searchInput = document.getElementById('posSearchDrugInput');
    const searchDropdown = document.getElementById('posSearchDropdown');
    const barcodeAlert = document.getElementById('posBarcodeNotFoundAlert');
    const tbodyCart = document.getElementById('tbodyPosCart');
    const tbodyFefo = document.getElementById('tbodyFefoPreview');
    const fefoErrorBox = document.getElementById('fefoErrorBox');
    const rxWarning = document.getElementById('posRxWarning');
    const donThuocSelect = document.getElementById('posDonThuocSelect');
    const giamGiaInput = document.getElementById('posGiamGiaInput');
    const phuongThucSelect = document.getElementById('posPhuongThucSelect');
    const cashPaymentBox = document.getElementById('posCashPaymentBox');
    const tienKhachDuaInput = document.getElementById('posTienKhachDuaInput');
    const tienThoiLabel = document.getElementById('posTienThoiLabel');
    const tienThoiDisplay = document.getElementById('posTienThoiDisplay');
    let currentThanhToan = 0;

    if (window.Is009Scanner && barcodeInput) {
        window.Is009Scanner.initScanInput(barcodeInput, { autoKeepFocus: true, clearOnScan: true });
    }

    async function handlePosBarcodeScan(rawCode, source) {
        const code = (rawCode || '').trim();
        if (!code) return;

        try {
            let lookupCode = code;
            if (code.includes('(') || code.length > 14) {
                const gs1Res = await IS009.apiGet(`/api/barcode/parse-gs1?raw=${encodeURIComponent(code)}`);
                if (gs1Res.thanhCong && gs1Res.duLieu && gs1Res.duLieu.maTraCuuUuTien) {
                    lookupCode = gs1Res.duLieu.maTraCuuUuTien;
                }
            }

            const res = await IS009.apiGet(`/api/thuoc/ma-vach/${encodeURIComponent(lookupCode)}`);
            if (!res.thanhCong || !res.duLieu || !res.duLieu.thuoc) {
                if (barcodeAlert) {
                    barcodeAlert.className = 'alert alert-warning mt-2 mb-0 py-2 small';
                    barcodeAlert.innerHTML = `<i class="bi bi-exclamation-triangle-fill me-1"></i><strong>Mã [${IS009.escapeHtml(code)}]:</strong> ${IS009.escapeHtml(res.thongBao || 'Không tìm thấy thuốc tương ứng')}`;
                    barcodeAlert.classList.remove('d-none');
                }
                return;
            }

            const data = res.duLieu;
            const thuoc = data.thuoc;
            const tenThuoc = thuoc.ten || thuoc.tenThuoc || '';
            const maThuoc = thuoc.ma || thuoc.maThuoc || '';
            addDrugToCart(thuoc, 1);

            if (barcodeAlert) {
                barcodeAlert.className = 'alert alert-success mt-2 mb-0 py-2 small';
                barcodeAlert.innerHTML = `<i class="bi bi-check-circle-fill me-1"></i><strong>Đã quét mã [${IS009.escapeHtml(code)}] (${IS009.escapeHtml(source || 'SCAN')}):</strong> Đã thêm <strong>${IS009.escapeHtml(maThuoc + ' - ' + tenThuoc)}</strong> — Đơn giá: <strong>${IS009.formatVND(thuoc.giaBanLe)}</strong> | Tồn khả dụng: <strong>${data.tongTonKhaDung} ${IS009.escapeHtml(thuoc.donViTinh || '')}</strong>`;
                barcodeAlert.classList.remove('d-none');
            }
            IS009.toast(`Đã thêm vào giỏ: ${tenThuoc} (Mã quét: ${code})`, 'success');
        } catch (err) {
            if (barcodeAlert) {
                barcodeAlert.className = 'alert alert-danger mt-2 mb-0 py-2 small';
                barcodeAlert.textContent = err.message;
                barcodeAlert.classList.remove('d-none');
            }
        }
    }

    document.addEventListener('barcode-scanned', (e) => {
        const detail = e.detail || {};
        if (detail.code) {
            handlePosBarcodeScan(detail.code, detail.source);
        }
    });

    if (btnPosScanManual && barcodeInput) {
        btnPosScanManual.addEventListener('click', () => {
            const val = barcodeInput.value.trim();
            if (val) {
                barcodeInput.value = '';
                handlePosBarcodeScan(val, 'MANUAL');
            }
        });
    }

    // Bật/tắt camera quét mã trên POS
    const btnToggleCamera = document.getElementById('btnToggleCameraPos');
    if (btnToggleCamera && window.Is009Scanner) {
        btnToggleCamera.addEventListener('click', () => {
            window.Is009Scanner.toggleCameraScanner('posCameraReader', btnToggleCamera, barcodeInput);
        });
    }

    // Tìm kiếm thuốc theo tên
    let searchTimer = null;
    if (searchInput) {
        searchInput.addEventListener('input', () => {
            clearTimeout(searchTimer);
            const kw = searchInput.value.trim();
            if (kw.length < 1) {
                searchDropdown.classList.add('d-none');
                return;
            }
            searchTimer = setTimeout(async () => {
                try {
                    const res = await IS009.apiGet(`/api/thuoc/tim-kiem?q=${encodeURIComponent(kw)}`);
                    const list = res.duLieu || [];
                    renderSearchDropdown(list);
                } catch (e) {
                    searchDropdown.classList.add('d-none');
                }
            }, 220);
        });
    }

    function renderSearchDropdown(list) {
        searchDropdown.innerHTML = '';
        if (list.length === 0) {
            searchDropdown.innerHTML = '<div class="list-group-item small text-muted">Không tìm thấy thuốc phù hợp</div>';
            searchDropdown.classList.remove('d-none');
            return;
        }
        list.slice(0, 12).forEach(t => {
            const ma = t.ma || t.maThuoc || '';
            const ten = t.ten || t.tenThuoc || '';
            const keDon = Boolean(t.canKeDon ?? t.thuocKeDon);
            const item = document.createElement('button');
            item.type = 'button';
            item.className = 'list-group-item list-group-item-action py-2 small';
            item.innerHTML = `
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="fw-bold text-primary">${IS009.escapeHtml(ma)}</span> -
                        <span class="fw-semibold">${IS009.escapeHtml(ten)}</span>
                        ${keDon ? '<span class="badge bg-danger ms-1">Kê đơn Rx</span>' : ''}
                        <div class="text-muted" style="font-size:0.78rem">${IS009.escapeHtml(t.hoatChat || '')} | ĐVT: ${IS009.escapeHtml(t.donViTinh || '')}</div>
                    </div>
                    <div class="fw-bold text-nowrap ms-2">${IS009.formatVND(t.giaBanLe)}</div>
                </div>
            `;
            item.addEventListener('click', () => {
                addDrugToCart(t, 1);
                searchDropdown.classList.add('d-none');
                searchInput.value = '';
                if (barcodeInput) barcodeInput.focus();
            });
            searchDropdown.appendChild(item);
        });
        searchDropdown.classList.remove('d-none');
    }

    document.addEventListener('click', (e) => {
        if (searchDropdown && !searchDropdown.contains(e.target) && e.target !== searchInput) {
            searchDropdown.classList.add('d-none');
        }
    });

    function addDrugToCart(thuoc, qtyToAdd) {
        const ma = thuoc.ma || thuoc.maThuoc || '';
        const ten = thuoc.ten || thuoc.tenThuoc || '';
        const keDon = Boolean(thuoc.canKeDon ?? thuoc.thuocKeDon);
        drugNameMap.set(thuoc.id, ten);
        if (cart.has(thuoc.id)) {
            const existing = cart.get(thuoc.id);
            existing.soLuong += qtyToAdd;
        } else {
            cart.set(thuoc.id, {
                thuocId: thuoc.id,
                maThuoc: ma,
                tenThuoc: ten,
                hoatChat: thuoc.hoatChat || '',
                donViTinh: thuoc.donViTinh || '',
                giaBanLe: Number(thuoc.giaBanLe || 0),
                thuocKeDon: keDon,
                soLuong: qtyToAdd
            });
        }
        renderCart();
        refreshFefoPreview();
    }

    function renderCart() {
        tbodyCart.innerHTML = '';
        document.getElementById('posCartCount').textContent = cart.size;

        if (cart.size === 0) {
            tbodyCart.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center text-muted py-4">
                        Chưa có thuốc trong giỏ. Quét mã vạch hoặc tìm tên thuốc để thêm vào giỏ.
                    </td>
                </tr>
            `;
            updateSummaryTotals();
            return;
        }

        cart.forEach(item => {
            const thanhTien = item.giaBanLe * item.soLuong;
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="small fw-semibold text-primary">${IS009.escapeHtml(item.maThuoc)}</td>
                <td>
                    <div class="fw-semibold">${IS009.escapeHtml(item.tenThuoc)}
                        ${item.thuocKeDon ? '<span class="badge bg-danger ms-1">Rx - Kê đơn</span>' : ''}
                    </div>
                    <small class="text-muted">${IS009.escapeHtml(item.hoatChat || '')}</small>
                </td>
                <td>${IS009.escapeHtml(item.donViTinh)}</td>
                <td class="text-end">${IS009.formatVND(item.giaBanLe)}</td>
                <td class="text-center">
                    <input type="number" class="form-control form-control-sm text-center inp-cart-qty"
                           min="1" value="${item.soLuong}" data-id="${item.thuocId}">
                </td>
                <td class="text-end fw-bold">${IS009.formatVND(thanhTien)}</td>
                <td class="text-end">
                    <button type="button" class="btn btn-sm btn-outline-danger btn-remove-item" data-id="${item.thuocId}">
                        <i class="bi bi-x-lg"></i>
                    </button>
                </td>
            `;
            tr.querySelector('.inp-cart-qty').addEventListener('change', (ev) => {
                const val = parseInt(ev.target.value, 10);
                if (!val || val <= 0) {
                    cart.delete(item.thuocId);
                } else {
                    item.soLuong = val;
                }
                renderCart();
                refreshFefoPreview();
            });
            tr.querySelector('.btn-remove-item').addEventListener('click', () => {
                cart.delete(item.thuocId);
                renderCart();
                refreshFefoPreview();
            });
            tbodyCart.appendChild(tr);
        });

        updateSummaryTotals();
    }

    function updateSummaryTotals() {
        let tongTien = 0;
        let hasRx = false;
        cart.forEach(item => {
            tongTien += item.giaBanLe * item.soLuong;
            if (item.thuocKeDon) hasRx = true;
        });

        const giamGia = Math.max(0, Number(giamGiaInput?.value || 0));
        const thanhToan = Math.max(0, tongTien - giamGia);
        currentThanhToan = thanhToan;

        document.getElementById('posTongTienHangDisplay').textContent = IS009.formatVND(tongTien);
        document.getElementById('posThanhToanDisplay').textContent = IS009.formatVND(thanhToan);

        const hasPrescription = Boolean(donThuocSelect?.value);
        if (hasRx && !hasPrescription) {
            rxWarning.classList.remove('d-none');
        } else {
            rxWarning.classList.add('d-none');
        }

        updateCashChange();
    }

    function updateCashChange() {
        const isCash = !phuongThucSelect || phuongThucSelect.value === 'TIEN_MAT';
        if (cashPaymentBox) {
            cashPaymentBox.classList.toggle('d-none', !isCash);
        }
        if (!isCash || !tienThoiDisplay || !tienThoiLabel) return;

        const rawKhachDua = tienKhachDuaInput ? tienKhachDuaInput.value.trim() : '';
        if (!rawKhachDua || currentThanhToan <= 0) {
            tienThoiLabel.textContent = 'Tiền thối lại khách:';
            tienThoiDisplay.className = 'fs-6 fw-bold text-success';
            tienThoiDisplay.textContent = '0 ₫';
            return;
        }

        const khachDua = Math.max(0, Number(rawKhachDua || 0));
        const chenhLech = khachDua - currentThanhToan;
        if (chenhLech >= 0) {
            tienThoiLabel.textContent = 'Tiền thối lại khách:';
            tienThoiDisplay.className = 'fs-6 fw-bold text-success';
            tienThoiDisplay.textContent = IS009.formatVND(chenhLech);
        } else {
            tienThoiLabel.textContent = 'Khách đưa còn thiếu:';
            tienThoiDisplay.className = 'fs-6 fw-bold text-danger';
            tienThoiDisplay.textContent = IS009.formatVND(Math.abs(chenhLech));
        }
    }

    if (giamGiaInput) {
        giamGiaInput.addEventListener('input', updateSummaryTotals);
    }
    if (donThuocSelect) {
        donThuocSelect.addEventListener('change', updateSummaryTotals);
    }
    if (phuongThucSelect) {
        phuongThucSelect.addEventListener('change', updateCashChange);
    }
    if (tienKhachDuaInput) {
        tienKhachDuaInput.addEventListener('input', updateCashChange);
    }
    document.querySelectorAll('.btn-quick-cash').forEach(btn => {
        btn.addEventListener('click', () => {
            if (!tienKhachDuaInput) return;
            if (btn.dataset.mode === 'exact') {
                tienKhachDuaInput.value = currentThanhToan > 0 ? String(currentThanhToan) : '';
            } else {
                const addAmt = Number(btn.dataset.amount || 0);
                tienKhachDuaInput.value = String(addAmt);
            }
            updateCashChange();
        });
    });

    async function refreshFefoPreview() {
        fefoErrorBox.classList.add('d-none');
        if (cart.size === 0) {
            tbodyFefo.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-muted py-3 small">
                        Hệ thống sẽ tự động gọi <code>sp_XemTruocFEFO</code> khi có thuốc trong giỏ.
                    </td>
                </tr>
            `;
            return;
        }

        const chiTiet = Array.from(cart.values()).map(i => ({
            thuoc_id: i.thuocId,
            so_luong: i.soLuong,
            lo_thuoc_id: null
        }));

        try {
            const res = await IS009.apiPost('/api/banhang/xem-truoc-fefo', { chiTiet });
            if (!res.thanhCong) {
                fefoErrorBox.textContent = res.thongBao || 'Không đủ tồn kho khả dụng';
                fefoErrorBox.classList.remove('d-none');
                tbodyFefo.innerHTML = '';
                return;
            }
            const list = res.duLieu || [];
            tbodyFefo.innerHTML = '';
            list.forEach(lo => {
                const badgeHtml = buildExpiryBadge(lo.soNgayConLai);
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-semibold small">${IS009.escapeHtml(drugNameMap.get(lo.thuocId) || ('Thuốc #' + lo.thuocId))}</td>
                    <td class="fw-bold text-primary">${IS009.escapeHtml(lo.soLo)}</td>
                    <td>${lo.hanDung || ''}</td>
                    <td class="text-center">${badgeHtml}</td>
                    <td class="text-end fw-bold">${lo.soLuong}</td>
                    <td class="text-end">${IS009.formatVND(lo.donGia)}</td>
                `;
                tbodyFefo.appendChild(tr);
            });
        } catch (err) {
            fefoErrorBox.textContent = err.message;
            fefoErrorBox.classList.remove('d-none');
            tbodyFefo.innerHTML = '';
        }
    }

    function buildExpiryBadge(days) {
        if (days === null || days === undefined) return '';
        if (days <= 0) return `<span class="badge badge-exp-expired">Hết hạn (${days} ngày)</span>`;
        if (days <= 30) return `<span class="badge badge-exp-30">Cận hạn ≤30 ngày (${days} ngày)</span>`;
        if (days <= 60) return `<span class="badge badge-exp-60">Cận hạn ≤60 ngày (${days} ngày)</span>`;
        if (days <= 90) return `<span class="badge badge-exp-90">Cận hạn ≤90 ngày (${days} ngày)</span>`;
        return `<span class="badge bg-success-subtle text-success border border-success-subtle">Còn ${days} ngày</span>`;
    }

    const btnRefreshFefo = document.getElementById('btnRefreshFefo');
    if (btnRefreshFefo) {
        btnRefreshFefo.addEventListener('click', refreshFefoPreview);
    }

    const btnClearCart = document.getElementById('btnClearCart');
    if (btnClearCart) {
        btnClearCart.addEventListener('click', () => {
            cart.clear();
            if (tienKhachDuaInput) tienKhachDuaInput.value = '';
            renderCart();
            refreshFefoPreview();
            if (barcodeAlert) barcodeAlert.classList.add('d-none');
        });
    }

    const btnPosTimKhach = document.getElementById('btnPosTimKhach');
    if (btnPosTimKhach) {
        btnPosTimKhach.addEventListener('click', async () => {
            const sdt = document.getElementById('posSdtKhach').value.trim();
            if (!sdt) return;
            try {
                const res = await IS009.apiGet(`/api/khachhang/tim-sdt?sdt=${encodeURIComponent(sdt)}`);
                const list = res.duLieu || [];
                if (list.length > 0) {
                    const kh = list[0];
                    document.getElementById('posKhachHangId').value = kh.id;
                    document.getElementById('posKhachHangInfo').textContent = `Khách hàng: ${kh.hoTen} (${kh.soDienThoai})`;
                } else {
                    document.getElementById('posKhachHangId').value = '';
                    document.getElementById('posKhachHangInfo').textContent = 'Không tìm thấy SĐT này — Sẽ lưu khách lẻ';
                }
            } catch (e) {
                IS009.toast(e.message, 'danger');
            }
        });
    }

    const btnPosXoaKhach = document.getElementById('btnPosXoaKhach');
    if (btnPosXoaKhach) {
        btnPosXoaKhach.addEventListener('click', () => {
            document.getElementById('posSdtKhach').value = '';
            document.getElementById('posKhachHangId').value = '';
            document.getElementById('posKhachHangInfo').textContent = 'Khách vãng lai (Khách lẻ)';
        });
    }

    async function napThuocTuDon(donId) {
        if (!donId) return;
        try {
            const res = await IS009.apiGet(`/api/donthuoc/${donId}`);
            const dt = res.duLieu;
            if (!dt) return;
            document.getElementById('posDonThuocInfo').textContent =
                `BS: ${dt.tenBacSi || ''} | Cơ sở: ${dt.coSoKham || ''} | Ngày kê: ${dt.ngayKeDon || ''}`;
            if (dt.khachHangId) {
                document.getElementById('posKhachHangId').value = dt.khachHangId;
                document.getElementById('posKhachHangInfo').textContent = `Khách theo đơn: ${dt.tenKhachHang || ''}`;
            }

            const resThuoc = await IS009.apiGet('/api/thuoc/tim-kiem?q=');
            const mapThuoc = new Map((resThuoc.duLieu || []).map(t => [t.id, t]));

            cart.clear();
            (dt.chiTiet || []).forEach(c => {
                if (c.soLuongConLai > 0 && mapThuoc.has(c.thuocId)) {
                    addDrugToCart(mapThuoc.get(c.thuocId), c.soLuongConLai);
                }
            });
            IS009.toast(`Đã nạp thuốc còn lại từ đơn ${dt.maDon}`, 'info');
        } catch (e) {
            IS009.toast(e.message, 'danger');
        }
    }

    const btnNapThuocTuDon = document.getElementById('btnNapThuocTuDon');
    if (btnNapThuocTuDon) {
        btnNapThuocTuDon.addEventListener('click', () => {
            const donId = donThuocSelect.value;
            if (!donId) {
                IS009.toast('Vui lòng chọn một đơn thuốc trong danh sách', 'warning');
                return;
            }
            napThuocTuDon(donId);
        });
    }

    if (donThuocSelect && donThuocSelect.dataset.preselected) {
        napThuocTuDon(donThuocSelect.dataset.preselected);
    }

    const btnPosThanhToan = document.getElementById('btnPosThanhToan');
    if (btnPosThanhToan) {
        btnPosThanhToan.addEventListener('click', submitPayment);
    }

    async function submitPayment() {
        if (cart.size === 0) {
            IS009.toast('Giỏ hàng đang trống, vui lòng quét mã hoặc chọn thuốc!', 'warning');
            return;
        }
        const khIdRaw = document.getElementById('posKhachHangId').value;
        const donIdRaw = donThuocSelect.value;
        const giamGia = Math.max(0, Number(giamGiaInput.value || 0));
        const phuongThucThanhToan = document.getElementById('posPhuongThucSelect').value;

        let khachDuaVal = currentThanhToan;
        let tienThoiVal = 0;
        if (phuongThucThanhToan === 'TIEN_MAT' && tienKhachDuaInput) {
            const rawKd = tienKhachDuaInput.value.trim();
            if (rawKd !== '') {
                khachDuaVal = Number(rawKd);
                if (khachDuaVal < currentThanhToan) {
                    IS009.toast(`Số tiền khách đưa (${IS009.formatVND(khachDuaVal)}) nhỏ hơn tổng thanh toán (${IS009.formatVND(currentThanhToan)})!`, 'warning');
                    tienKhachDuaInput.focus();
                    return;
                }
                tienThoiVal = khachDuaVal - currentThanhToan;
            }
        }

        const chiTiet = Array.from(cart.values()).map(i => ({
            thuoc_id: i.thuocId,
            so_luong: i.soLuong,
            lo_thuoc_id: null
        }));

        btnPosThanhToan.disabled = true;
        try {
            const res = await IS009.apiPost('/api/banhang/thanh-toan', {
                khachHangId: khIdRaw ? parseInt(khIdRaw, 10) : null,
                donThuocId: donIdRaw ? parseInt(donIdRaw, 10) : null,
                phuongThucThanhToan,
                giamGia,
                chiTiet
            });
            if (!res.thanhCong) {
                IS009.toast(res.thongBao || 'Không thể hoàn tất thanh toán', 'danger');
                return;
            }
            const gd = res.duLieu;
            IS009.toast(res.thongBao || 'Thanh toán thành công!', 'success');
            cart.clear();
            if (tienKhachDuaInput) tienKhachDuaInput.value = '';
            renderCart();
            refreshFefoPreview();
            if (gd && gd.id) {
                const printUrl = phuongThucThanhToan === 'TIEN_MAT'
                    ? `/banhang/hoa-don/${gd.id}/in?tienKhachDua=${encodeURIComponent(khachDuaVal)}&tienThoi=${encodeURIComponent(tienThoiVal)}`
                    : `/banhang/hoa-don/${gd.id}/in`;
                window.open(printUrl, '_blank');
            }
        } catch (err) {
            IS009.toast(err.message, 'danger');
        } finally {
            btnPosThanhToan.disabled = false;
        }
    }

    document.addEventListener('keydown', (e) => {
        if (e.key === 'F2') {
            e.preventDefault();
            if (barcodeInput) barcodeInput.focus();
        } else if (e.key === 'F3') {
            e.preventDefault();
            if (searchInput) searchInput.focus();
        } else if (e.key === 'F4') {
            e.preventDefault();
            if (btnClearCart) btnClearCart.click();
        } else if (e.key === 'F9') {
            e.preventDefault();
            submitPayment();
        }
    });
});
