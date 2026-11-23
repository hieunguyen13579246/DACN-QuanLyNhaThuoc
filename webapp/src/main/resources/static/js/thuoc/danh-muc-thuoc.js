(function () {
    "use strict";

    const mvInput = document.getElementById("mvInputCode");
    const mvLoai = document.getElementById("mvLoai");
    const mvFeedback = document.getElementById("mvEan13Feedback");
    const btnLuuMaVach = document.getElementById("btnLuuMaVach");

    let validateEan13Fn = null;
    if (mvInput && mvFeedback) {
        validateEan13Fn = Is009Ean13.bindEan13Validation(mvInput, mvLoai, mvFeedback);
    }

    if (btnLuuMaVach) {
        btnLuuMaVach.addEventListener("click", async function () {
            const thuocId = document.getElementById("mvThuocId").value;
            const loai = mvLoai.value;
            const ma = mvInput.value.trim();
            const laMaChinh = document.getElementById("mvLaMaChinh").checked;

            if (!thuocId) {
                Is009Common.showAlert("warning", "Vui lòng chọn thuốc cần gán mã vạch.");
                return;
            }
            if (!ma) {
                Is009Common.showAlert("warning", "Vui lòng nhập mã vạch.");
                return;
            }
            if (validateEan13Fn && !validateEan13Fn()) {
                return;
            }

            try {
                await Is009Common.apiFetch("/api/thuoc/quan-ly/ma-vach", {
                    method: "POST",
                    body: JSON.stringify({
                        thuocId: Number(thuocId),
                        maVach: ma,
                        loaiMaVach: loai,
                        laMaChinh: laMaChinh
                    })
                });
                window.location.reload();
            } catch (err) {
                Is009Common.showAlert("danger", err.message, err.huongDanSua);
            }
        });
    }

    const modalThuocEl = document.getElementById("modalThuoc");
    const modalThuoc = modalThuocEl ? new bootstrap.Modal(modalThuocEl) : null;
    const modalNhomEl = document.getElementById("modalNhomThuoc");
    const modalNhom = modalNhomEl ? new bootstrap.Modal(modalNhomEl) : null;

    const btnMoModalThuoc = document.getElementById("btnMoModalThuoc");
    if (btnMoModalThuoc && modalThuoc) {
        btnMoModalThuoc.addEventListener("click", function () {
            document.getElementById("modalThuocTitle").textContent = "Thêm thuốc mới";
            document.getElementById("formThuocId").value = "";
            document.getElementById("formThuocMa").value = "";
            document.getElementById("formThuocTen").value = "";
            document.getElementById("formThuocHoatChat").value = "";
            document.getElementById("formThuocHamLuong").value = "";
            document.getElementById("formThuocDangBaoChe").value = "Viên nén";
            document.getElementById("formThuocDvt").value = "Viên";
            document.getElementById("formThuocGiaBanLe").value = "2000";
            document.getElementById("formThuocNsx").value = "";
            document.getElementById("formThuocTrangThai").value = "DANG_BAN";
            document.getElementById("formThuocCanKeDon").checked = false;
            modalThuoc.show();
        });
    }

    document.querySelectorAll(".js-btn-edit-thuoc").forEach(function (btn) {
        btn.addEventListener("click", function () {
            if (!modalThuoc) return;
            document.getElementById("modalThuocTitle").textContent = "Cập nhật thông tin thuốc";
            document.getElementById("formThuocId").value = btn.getAttribute("data-id") || "";
            document.getElementById("formThuocMa").value = btn.getAttribute("data-ma") || "";
            document.getElementById("formThuocTen").value = btn.getAttribute("data-ten") || "";
            document.getElementById("formThuocHoatChat").value = btn.getAttribute("data-hoatchat") || "";
            document.getElementById("formThuocHamLuong").value = btn.getAttribute("data-hamluong") || "";
            document.getElementById("formThuocDangBaoChe").value = btn.getAttribute("data-dangbaoche") || "";
            document.getElementById("formThuocDvt").value = btn.getAttribute("data-dvt") || "Viên";
            document.getElementById("formThuocNhomId").value = btn.getAttribute("data-nhomid") || "";
            document.getElementById("formThuocNsx").value = btn.getAttribute("data-nsx") || "";
            document.getElementById("formThuocGiaBanLe").value = btn.getAttribute("data-giabanle") || "1000";
            document.getElementById("formThuocTrangThai").value = btn.getAttribute("data-trangthai") || "DANG_BAN";
            document.getElementById("formThuocCanKeDon").checked = btn.getAttribute("data-cankedon") === "true";
            modalThuoc.show();
        });
    });

    const btnSubmitThuoc = document.getElementById("btnSubmitThuoc");
    if (btnSubmitThuoc) {
        btnSubmitThuoc.addEventListener("click", async function () {
            const id = document.getElementById("formThuocId").value;
            const payload = {
                ma: document.getElementById("formThuocMa").value.trim(),
                ten: document.getElementById("formThuocTen").value.trim(),
                hoatChat: document.getElementById("formThuocHoatChat").value.trim(),
                hamLuong: document.getElementById("formThuocHamLuong").value.trim(),
                dangBaoChe: document.getElementById("formThuocDangBaoChe").value.trim(),
                donViTinh: document.getElementById("formThuocDvt").value.trim(),
                nhomThuocId: Number(document.getElementById("formThuocNhomId").value),
                nhaSanXuat: document.getElementById("formThuocNsx").value.trim(),
                giaBanLe: Number(document.getElementById("formThuocGiaBanLe").value),
                trangThai: document.getElementById("formThuocTrangThai").value,
                canKeDon: document.getElementById("formThuocCanKeDon").checked
            };
            try {
                const url = id ? ("/api/thuoc/quan-ly/" + id) : "/api/thuoc/quan-ly";
                const method = id ? "PUT" : "POST";
                await Is009Common.apiFetch(url, {
                    method: method,
                    body: JSON.stringify(payload)
                });
                window.location.reload();
            } catch (err) {
                modalThuoc.hide();
                Is009Common.showAlert("danger", err.message, err.huongDanSua);
            }
        });
    }

    const btnMoModalNhom = document.getElementById("btnMoModalNhom");
    if (btnMoModalNhom && modalNhom) {
        btnMoModalNhom.addEventListener("click", function () {
            document.getElementById("formNhomMa").value = "";
            document.getElementById("formNhomTen").value = "";
            modalNhom.show();
        });
    }

    const btnSubmitNhom = document.getElementById("btnSubmitNhom");
    if (btnSubmitNhom) {
        btnSubmitNhom.addEventListener("click", async function () {
            try {
                await Is009Common.apiFetch("/api/thuoc/quan-ly/nhom", {
                    method: "POST",
                    body: JSON.stringify({
                        ma: document.getElementById("formNhomMa").value.trim(),
                        ten: document.getElementById("formNhomTen").value.trim()
                    })
                });
                window.location.reload();
            } catch (err) {
                modalNhom.hide();
                Is009Common.showAlert("danger", err.message, err.huongDanSua);
            }
        });
    }
})();
