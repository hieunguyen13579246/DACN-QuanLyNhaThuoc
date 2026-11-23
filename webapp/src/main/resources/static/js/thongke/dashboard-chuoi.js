'use strict';

document.addEventListener('DOMContentLoaded', async () => {
    const root = document.getElementById('dashboardChuoiRoot');
    if (!root) return;

    const khuVucId = root.dataset.khuVucId || '';
    const nhomThuocId = root.dataset.nhomThuocId || '';
    const tuNgay = root.dataset.tuNgay || '';
    const denNgay = root.dataset.denNgay || '';

    const qs = new URLSearchParams();
    if (khuVucId) qs.set('khuVucId', khuVucId);
    if (nhomThuocId) qs.set('nhomThuocId', nhomThuocId);
    if (tuNgay) qs.set('tuNgay', tuNgay);
    if (denNgay) qs.set('denNgay', denNgay);

    try {
        const [resNc, resXh] = await Promise.all([
            IS009.apiGet(`/api/thongke/nhu-cau-khu-vuc?${qs.toString()}`),
            IS009.apiGet(`/api/thongke/xu-huong?${qs.toString()}`)
        ]);

        renderHeatmap(resNc.duLieu || []);
        renderTrendChart(resXh.duLieu || []);
    } catch (err) {
        IS009.toast('Không tải được dữ liệu phân tích: ' + err.message, 'danger');
    }

    function renderHeatmap(rows) {
        const container = document.getElementById('heatmapMatrixContainer');
        if (!container) return;
        if (rows.length === 0) {
            container.innerHTML = '<div class="p-4 text-center text-muted">Không có dữ liệu nhu cầu trong khoảng lọc đã chọn.</div>';
            return;
        }

        const regions = [];
        const regionSet = new Set();
        const groups = [];
        const groupSet = new Set();
        const cellMap = new Map();
        let maxQty = 1;

        rows.forEach(r => {
            if (!regionSet.has(r.khuVuc)) {
                regionSet.add(r.khuVuc);
                regions.push({ name: r.khuVuc, danSo: r.danSo });
            }
            if (!groupSet.has(r.nhomThuoc)) {
                groupSet.add(r.nhomThuoc);
                groups.push(r.nhomThuoc);
            }
            const key = `${r.khuVuc}__${r.nhomThuoc}`;
            cellMap.set(key, r);
            if (r.tongSoLuong > maxQty) maxQty = r.tongSoLuong;
        });

        let html = '<table class="table table-sm table-bordered align-middle mb-0 heatmap-table">';
        html += '<thead class="table-light"><tr><th>Khu vực \\ Nhóm thuốc</th>';
        groups.forEach(g => {
            html += `<th class="text-center small">${IS009.escapeHtml(g)}</th>`;
        });
        html += '<th class="text-end">Tổng SL khu vực</th></tr></thead><tbody>';

        regions.forEach(reg => {
            let rowTotal = 0;
            html += `<tr><td><div class="fw-bold">${IS009.escapeHtml(reg.name)}</div><small class="text-muted">Dân số: ${Number(reg.danSo || 0).toLocaleString('vi-VN')}</small></td>`;
            groups.forEach(g => {
                const cell = cellMap.get(`${reg.name}__${g}`);
                if (!cell) {
                    html += '<td class="text-center text-muted small">-</td>';
                } else {
                    rowTotal += Number(cell.tongSoLuong || 0);
                    const ratio = cell.tongSoLuong / maxQty;
                    const cls = ratio >= 0.7 ? 'heat-4' : ratio >= 0.4 ? 'heat-3' : ratio >= 0.15 ? 'heat-2' : 'heat-1';
                    html += `
                        <td class="text-center ${cls}">
                            <div class="fw-bold">${cell.tongSoLuong}</div>
                            <small style="font-size:0.72rem">${cell.sl1000Dan || 0}/1k dân</small>
                        </td>
                    `;
                }
            });
            html += `<td class="text-end fw-bold text-primary">${rowTotal.toLocaleString('vi-VN')}</td></tr>`;
        });

        html += '</tbody></table>';
        container.innerHTML = html;
    }

    function renderTrendChart(xh) {
        const ctx = document.getElementById('chartChuoiXuHuong');
        if (!ctx || !window.Chart) return;
        window.Chart.defaults.font.family = "'Times New Roman', Times, serif";
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: xh.map(x => x.thangStr),
                datasets: [
                    {
                        type: 'bar',
                        label: 'Doanh số chuỗi (VND)',
                        data: xh.map(x => x.doanhSo),
                        backgroundColor: '#0d6efd',
                        yAxisID: 'y'
                    },
                    {
                        type: 'line',
                        label: 'Tăng trưởng MoM (%)',
                        data: xh.map(x => x.tangTruongPct),
                        borderColor: '#dc3545',
                        backgroundColor: '#dc3545',
                        yAxisID: 'y1'
                    }
                ]
            },
            options: {
                responsive: true,
                animation: false,
                scales: {
                    y: { position: 'left', beginAtZero: true },
                    y1: { position: 'right', grid: { drawOnChartArea: false } }
                }
            }
        });
    }
});
