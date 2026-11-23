(function () {
    "use strict";

    const inputEl = document.getElementById("inputMaVachTraCuu");
    const btnTraCuu = document.getElementById("btnTraCuuMaVach");
    const btnCamera = document.getElementById("btnToggleCameraTraCuu");
    const sourceBadge = document.getElementById("scanSourceBadge");
    const emptyState = document.getElementById("traCuuEmptyState");
    const contentBox = document.getElementById("traCuuContent");

    if (!inputEl) return;

    Is009Scanner.initScanInput(inputEl, { autoKeepFocus: true, clearOnScan: false });

    document.addEventListener("barcode-scanned", function (ev) {
        const detail = ev.detail || {};
        if (sourceBadge && detail.code) {
            sourceBadge.textContent = "Đã quét: " + detail.code + " (" + (detail.source || "MANUAL") + ")";
            sourceBadge.className = detail.source === "USB_SCANNER" || detail.source === "CAMERA"
                ? "badge bg-success" : "badge bg-primary";
        }
        if (detail.code) {
            inputEl.value = detail.code;
            thucHienTraCuu(detail.code);
        }
    });

    if (btnTraCuu) {
        btnTraCuu.addEventListener("click", function () {
            const val = inputEl.value.trim();
            if (val) thucHienTraCuu(val);
        });
    }

    if (btnCamera) {
        btnCamera.addEventListener("click", function () {
            Is009Scanner.toggleCameraScanner("cameraReaderTraCuu", btnCamera, inputEl);
        });
    }

    document.querySelectorAll(".js-sample-barcode").forEach(function (btn) {
        btn.addEventListener("click", function () {
            const code = btn.getAttribute("data-code");
            inputEl.value = code;
            thucHienTraCuu(code);
        });
    });

    async function thucHienTraCuu(rawCode) {
        Is009Common.clearAlert();
        try {
            let codeToQuery = rawCode.trim();
            if (codeToQuery.includes("(") || codeToQuery.length > 14) {
                const gs1Res = await Is009Common.apiFetch("/api/barcode/parse-gs1?raw=" + encodeURIComponent(codeToQuery));
                if (gs1Res.duLieu && gs1Res.duLieu.maTraCuuUuTien) {
                    codeToQuery = gs1Res.duLieu.maTraCuuUuTien;
                }
            }
            const res = await Is009Common.apiFetch("/api/thuoc/ma-vach/" + encodeURIComponent(codeToQuery));
            hienThiKetQua(res.duLieu);
        } catch (err) {
            contentBox.classList.add("d-none");
            emptyState.classList.remove("d-none");
            Is009Common.showAlert("danger", err.message, err.huongDanSua);
        }
    }

    function hienThiKetQua(data) {
        emptyState.classList.add("d-none");
        contentBox.classList.remove("d-none");
        const t = data.thuoc;
        Is009Common.showAlert("success", "Đã nhận diện mã vạch [" + data.maVachTraCuu + "] — Thuốc: " + t.ten + " (" + t.ma + ")", null);
        document.getElementById("tcTenThuoc").textContent = t.ten;
        document.getElementById("tcMaThuoc").textContent = t.ma;
        document.getElementById("tcMaVach").textContent = data.maVachTraCuu;
        document.getElementById("tcNhomThuoc").textContent = t.tenNhomThuoc || "—";
        document.getElementById("tcHoatChat").textContent = t.hoatChat || "—";
        document.getElementById("tcHamLuong").textContent = t.hamLuong || "—";
        document.getElementById("tcDangBaoChe").textContent = t.dangBaoChe || "—";
        document.getElementById("tcDonViTinh").textContent = t.donViTinh || "—";
        document.getElementById("tcGiaBanLe").textContent = Is009Common.formatVnd(t.giaBanLe);
        document.getElementById("tcTongTon").textContent = data.tongTonKhaDung + " " + t.donViTinh;

        const badgeKeDon = document.getElementById("tcBadgeKeDon");
        if (t.canKeDon) {
            badgeKeDon.className = "badge bg-danger";
            badgeKeDon.textContent = "Thuốc kê đơn (Bắt buộc đơn)";
        } else {
            badgeKeDon.className = "badge bg-success";
            badgeKeDon.textContent = "Thuốc không kê đơn (OTC)";
        }

        const tbody = document.getElementById("tcDanhSachLoBody");
        const dsLo = data.danhSachLo || [];
        if (dsLo.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-2">Hiện không có lô tồn kho tại nhà thuốc này.</td></tr>`;
            return;
        }
        tbody.innerHTML = dsLo.map(function (lo, idx) {
            const fefoTag = (idx === 0 && lo.mucCanhBao !== "DA_HET_HAN")
                ? `<span class="badge bg-primary">FEFO #1 (Xuất trước)</span>`
                : `<span class="text-muted small">#${idx + 1}</span>`;
            return `<tr>
                <td>${fefoTag}</td>
                <td class="fw-semibold">${Is009Common.escapeHtml(lo.soLo)}</td>
                <td>${Is009Common.formatDate(lo.hanDung)}</td>
                <td class="text-end fw-semibold">${Is009Common.formatNumber(lo.soLuongTon)}</td>
                <td>${Is009Common.renderMucCanhBaoBadge(lo.mucCanhBao, lo.soNgayConLai)}</td>
            </tr>`;
        }).join("");
    }

    if (inputEl.value && inputEl.value.trim()) {
        thucHienTraCuu(inputEl.value.trim());
    }
})();
