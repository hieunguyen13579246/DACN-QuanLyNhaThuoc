'use strict';

document.addEventListener('DOMContentLoaded', async () => {
    const root = document.getElementById('dashboardNtRoot');
    if (!root || !window.Chart) return;
    window.Chart.defaults.font.family = "'Times New Roman', Times, serif";

    const nhaThuocId = root.dataset.nhaThuocId || '';
    const tuNgay = root.dataset.tuNgay || '';
    const denNgay = root.dataset.denNgay || '';

    const qs = new URLSearchParams();
    if (nhaThuocId) qs.set('nhaThuocId', nhaThuocId);
    if (tuNgay) qs.set('tuNgay', tuNgay);
    if (denNgay) qs.set('denNgay', denNgay);

    try {
        const [resXh, resTop] = await Promise.all([
            IS009.apiGet(`/api/thongke/xu-huong?${qs.toString()}`),
            IS009.apiGet(`/api/thongke/top-thuoc?${qs.toString()}&top=8`)
        ]);

        const xh = resXh.duLieu || [];
        const top = resTop.duLieu || [];

        const ctxXh = document.getElementById('chartNtXuHuong');
        if (ctxXh) {
            new Chart(ctxXh, {
                type: 'bar',
                data: {
                    labels: xh.map(x => x.thangStr),
                    datasets: [
                        {
                            type: 'bar',
                            label: 'Doanh số (VND)',
                            data: xh.map(x => x.doanhSo),
                            backgroundColor: '#0d6efd',
                            yAxisID: 'y'
                        },
                        {
                            type: 'line',
                            label: 'Tăng trưởng (%)',
                            data: xh.map(x => x.tangTruongPct),
                            borderColor: '#198754',
                            backgroundColor: '#198754',
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

        const ctxTop = document.getElementById('chartNtTopThuoc');
        if (ctxTop) {
            // Gộp theo tên thuốc nếu có nhiều khu vực
            const mapTop = new Map();
            top.forEach(t => {
                mapTop.set(t.tenThuoc, (mapTop.get(t.tenThuoc) || 0) + Number(t.tongSoLuong || 0));
            });
            const labels = Array.from(mapTop.keys()).slice(0, 8);
            const values = labels.map(k => mapTop.get(k));

            new Chart(ctxTop, {
                type: 'bar',
                data: {
                    labels,
                    datasets: [{
                        label: 'Tổng số lượng bán',
                        data: values,
                        backgroundColor: '#0d6efd'
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    animation: false
                }
            });
        }
    } catch (err) {
        IS009.toast('Không tải được biểu đồ: ' + err.message, 'warning');
    }
});
