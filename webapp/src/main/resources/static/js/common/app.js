window.Is009Common = (function () {
    "use strict";

    function formatVnd(amount) {
        const val = Number(amount || 0);
        return val.toLocaleString("vi-VN") + " đ";
    }

    function formatNumber(amount) {
        const val = Number(amount || 0);
        return val.toLocaleString("vi-VN");
    }

    function formatDate(dateStr) {
        if (!dateStr) return "—";
        const parts = String(dateStr).substring(0, 10).split("-");
        if (parts.length !== 3) return dateStr;
        return parts[2] + "/" + parts[1] + "/" + parts[0];
    }

    function showAlert(type, message, huongDanSua, containerId) {
        const targetId = containerId || "globalAlertBox";
        const box = document.getElementById(targetId);
        if (!box) return;
        const alertClass = type === "success" ? "alert-success"
            : type === "warning" ? "alert-warning"
            : type === "info" ? "alert-info" : "alert-danger";
        let html = `<div class="alert ${alertClass} alert-dismissible py-2 px-3 mb-3" role="alert">`;
        html += `<div class="fw-semibold">${escapeHtml(message || "")}</div>`;
        if (huongDanSua) {
            html += `<div class="small mt-1"><strong>Cách khắc phục:</strong> ${escapeHtml(huongDanSua)}</div>`;
        }
        html += `<button type="button" class="btn-close py-2" data-bs-dismiss="alert" aria-label="Đóng"></button></div>`;
        box.innerHTML = html;
    }

    function clearAlert(containerId) {
        const box = document.getElementById(containerId || "globalAlertBox");
        if (box) box.innerHTML = "";
    }

    function escapeHtml(str) {
        return String(str ?? "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    function renderMucCanhBaoBadge(mucCanhBao, soNgayConLai) {
        const suffix = (soNgayConLai !== undefined && soNgayConLai !== null)
            ? ` (${soNgayConLai} ngày)` : "";
        switch (mucCanhBao) {
            case "DA_HET_HAN":
                return `<span class="badge badge-het-han">Đã hết hạn${suffix}</span>`;
            case "MUC_30":
            case "CAN_HAN_30":
            case "MUC3_30_NGAY":
                return `<span class="badge badge-muc-30">Cận hạn ≤30 ngày${suffix}</span>`;
            case "MUC_60":
            case "CAN_HAN_60":
            case "MUC2_60_NGAY":
                return `<span class="badge badge-muc-60">Cận hạn ≤60 ngày${suffix}</span>`;
            case "MUC_90":
            case "CAN_HAN_90":
            case "MUC1_90_NGAY":
                return `<span class="badge badge-muc-90">Cận hạn ≤90 ngày${suffix}</span>`;
            default:
                return `<span class="badge bg-success">Còn hạn tốt${suffix}</span>`;
        }
    }

    async function apiFetch(url, options) {
        const opts = Object.assign({
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
        }, options || {});
        const response = await fetch(url, opts);
        let payload = null;
        try {
            payload = await response.json();
        } catch (err) {
            payload = null;
        }
        if (!response.ok || (payload && payload.thanhCong === false)) {
            const error = new Error((payload && payload.thongBao) || ("Lỗi HTTP " + response.status));
            error.maLoi = payload ? payload.maLoi : String(response.status);
            error.huongDanSua = payload ? payload.huongDanSua : null;
            throw error;
        }
        return payload;
    }

    async function safeJsonCall(url, method, bodyObj) {
        const opts = {
            method: method,
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
        };
        if (bodyObj !== undefined && bodyObj !== null) {
            opts.body = JSON.stringify(bodyObj);
        }
        const response = await fetch(url, opts);
        let payload = null;
        try {
            payload = await response.json();
        } catch (err) {
            payload = null;
        }
        if (payload && typeof payload.thanhCong === "boolean") {
            if (!payload.thanhCong && payload.huongDanSua && payload.thongBao && !payload.thongBao.includes(payload.huongDanSua)) {
                payload.thongBao = payload.thongBao + " — Hướng dẫn: " + payload.huongDanSua;
            }
            return payload;
        }
        if (!response.ok) {
            return {
                thanhCong: false,
                maLoi: String(response.status),
                thongBao: (payload && (payload.thongBao || payload.message)) || ("Lỗi máy chủ HTTP " + response.status)
            };
        }
        return {
            thanhCong: true,
            duLieu: payload
        };
    }

    function toast(message, type) {
        showAlert(type || "info", message, null, "globalAlertBox");
    }

    return {
        formatVnd,
        formatVND: formatVnd,
        formatNumber,
        formatDate,
        showAlert,
        clearAlert,
        escapeHtml,
        renderMucCanhBaoBadge,
        apiFetch,
        apiGet: function (url) { return safeJsonCall(url, "GET", null); },
        apiPost: function (url, body) { return safeJsonCall(url, "POST", body); },
        apiPut: function (url, body) { return safeJsonCall(url, "PUT", body); },
        toast
    };
})();

window.IS009 = window.Is009Common;
