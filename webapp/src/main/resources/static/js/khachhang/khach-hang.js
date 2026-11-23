(function () {
    "use strict";

    const modalEl = document.getElementById("modalKhachHang");
    const modal = modalEl ? new bootstrap.Modal(modalEl) : null;
    const btnMo = document.getElementById("btnMoModalKhach");
    const btnLuu = document.getElementById("btnLuuKhachHang");

    if (btnMo && modal) {
        btnMo.addEventListener("click", function () {
            document.getElementById("modalKhachTitle").textContent = "Thêm khách hàng mới";
            document.getElementById("khId").value = "";
            document.getElementById("khMa").value = "";
            document.getElementById("khHoTen").value = "";
            document.getElementById("khSdt").value = "";
            document.getElementById("khGioiTinh").value = "Nam";
            document.getElementById("khNamSinh").value = "1985";
            document.getElementById("khKhuVucId").value = "";
            document.getElementById("khDiaChi").value = "";
            document.getElementById("khDiUng").value = "";
            modal.show();
        });
    }

    document.querySelectorAll(".js-edit-khach").forEach(function (btn) {
        btn.addEventListener("click", function () {
            if (!modal) return;
            document.getElementById("modalKhachTitle").textContent = "Cập nhật hồ sơ khách hàng";
            document.getElementById("khId").value = btn.getAttribute("data-id") || "";
            document.getElementById("khMa").value = btn.getAttribute("data-ma") || "";
            document.getElementById("khHoTen").value = btn.getAttribute("data-hoten") || "";
            document.getElementById("khSdt").value = btn.getAttribute("data-sdt") || "";
            document.getElementById("khGioiTinh").value = btn.getAttribute("data-gioitinh") || "Nam";
            document.getElementById("khNamSinh").value = btn.getAttribute("data-namsinh") || "";
            document.getElementById("khKhuVucId").value = btn.getAttribute("data-khuvucid") || "";
            document.getElementById("khDiaChi").value = btn.getAttribute("data-diachi") || "";
            document.getElementById("khDiUng").value = btn.getAttribute("data-diung") || "";
            modal.show();
        });
    });

    if (btnLuu) {
        btnLuu.addEventListener("click", async function () {
            const id = document.getElementById("khId").value;
            const kvVal = document.getElementById("khKhuVucId").value;
            const nsVal = document.getElementById("khNamSinh").value;
            const payload = {
                ma: document.getElementById("khMa").value.trim(),
                hoTen: document.getElementById("khHoTen").value.trim(),
                soDienThoai: document.getElementById("khSdt").value.trim(),
                gioiTinh: document.getElementById("khGioiTinh").value,
                namSinh: nsVal ? Number(nsVal) : null,
                khuVucId: kvVal ? Number(kvVal) : null,
                diaChi: document.getElementById("khDiaChi").value.trim(),
                ghiChuDiUng: document.getElementById("khDiUng").value.trim()
            };
            try {
                const url = id ? ("/api/khachhang/" + id) : "/api/khachhang";
                const method = id ? "PUT" : "POST";
                await Is009Common.apiFetch(url, {
                    method: method,
                    body: JSON.stringify(payload)
                });
                window.location.reload();
            } catch (err) {
                modal.hide();
                Is009Common.showAlert("danger", err.message, err.huongDanSua);
            }
        });
    }
})();
