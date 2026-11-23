'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const selLo = document.getElementById('temChonLoSelect');
    const inpTen = document.getElementById('temTenThuoc');
    const inpMaVach = document.getElementById('temMaVach');
    const selFormat = document.getElementById('temFormat');
    const inpSoLo = document.getElementById('temSoLo');
    const inpHanDung = document.getElementById('temHanDung');
    const inpGiaBan = document.getElementById('temGiaBan');
    const inpSoLuongIn = document.getElementById('temSoLuongIn');
    const grid = document.getElementById('labelPrintGrid');

    if (selLo) {
        selLo.addEventListener('change', () => {
            const opt = selLo.options[selLo.selectedIndex];
            if (!opt || !opt.value) return;
            inpTen.value = opt.dataset.ten || '';
            const bc = opt.dataset.barcode || opt.dataset.maThuoc || '';
            inpMaVach.value = bc;
            selFormat.value = /^\d{13}$/.test(bc) ? 'EAN13' : 'CODE128';
            inpSoLo.value = opt.dataset.soLo || '';
            inpHanDung.value = opt.dataset.hanDung || '';
            inpGiaBan.value = Math.round(Number(opt.dataset.gia || 0));
            renderLabels();
        });
    }

    function renderLabels() {
        const ten = inpTen.value.trim() || 'Thuốc IS009';
        const code = inpMaVach.value.trim() || '8934567890128';
        const format = selFormat.value || 'CODE128';
        const soLo = inpSoLo.value.trim();
        const hanDung = inpHanDung.value.trim();
        const gia = Number(inpGiaBan.value || 0);
        const count = Math.min(100, Math.max(1, parseInt(inpSoLuongIn.value, 10) || 1));

        grid.innerHTML = '';
        for (let i = 0; i < count; i++) {
            const card = document.createElement('div');
            card.className = 'barcode-label-card';
            const svgId = `bc_svg_${i}`;
            card.innerHTML = `
                <div class="label-drug-name">${IS009.escapeHtml(ten)}</div>
                <svg id="${svgId}" class="label-barcode-svg"></svg>
                <div class="label-meta-line">
                    <span>Lô: ${IS009.escapeHtml(soLo)} | HD: ${IS009.escapeHtml(hanDung)}</span>
                    <strong>${IS009.formatVND(gia)}</strong>
                </div>
            `;
            grid.appendChild(card);

            try {
                if (window.JsBarcode) {
                    window.JsBarcode(`#${svgId}`, code, {
                        format: format === 'EAN13' && /^\d{12,13}$/.test(code) ? 'EAN13' : 'CODE128',
                        width: 1.3,
                        height: 32,
                        displayValue: true,
                        font: 'Times New Roman',
                        fontSize: 10,
                        margin: 2
                    });
                }
            } catch (e) {
                IS009.toast('Mã vạch không hợp lệ với chuẩn đã chọn, tự động chuyển sang CODE128', 'warning');
                if (window.JsBarcode) {
                    window.JsBarcode(`#${svgId}`, code, {
                        format: 'CODE128',
                        width: 1.3,
                        height: 32,
                        displayValue: true,
                        font: 'Times New Roman',
                        fontSize: 10,
                        margin: 2
                    });
                }
            }
        }
    }

    document.getElementById('btnRenderLabels')?.addEventListener('click', renderLabels);
    document.getElementById('btnPrintLabels')?.addEventListener('click', () => {
        renderLabels();
        window.print();
    });

    renderLabels();
});
