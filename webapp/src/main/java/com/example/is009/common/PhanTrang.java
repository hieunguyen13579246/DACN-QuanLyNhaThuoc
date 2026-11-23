package com.example.is009.common;

import java.util.Collections;
import java.util.List;

public class PhanTrang<T> {

    private final List<T> danhSach;
    private final int trangHienTai;
    private final int kichThuoc;
    private final long tongPhanTu;
    private final int tongTrang;

    public PhanTrang(List<T> danhSach, int trangHienTai, int kichThuoc, long tongPhanTu) {
        this.danhSach = danhSach != null ? danhSach : Collections.emptyList();
        this.trangHienTai = Math.max(1, trangHienTai);
        this.kichThuoc = Math.max(1, kichThuoc);
        this.tongPhanTu = Math.max(0, tongPhanTu);
        this.tongTrang = (int) Math.max(1, Math.ceil((double) this.tongPhanTu / this.kichThuoc));
    }

    public static <T> PhanTrang<T> catTrang(List<T> toanBo, int trang, int kichThuoc) {
        if (toanBo == null || toanBo.isEmpty()) {
            return new PhanTrang<>(Collections.emptyList(), 1, kichThuoc, 0);
        }
        int p = Math.max(1, trang);
        int size = Math.max(1, kichThuoc);
        int fromIndex = (p - 1) * size;
        if (fromIndex >= toanBo.size()) {
            fromIndex = Math.max(0, ((toanBo.size() - 1) / size) * size);
            p = (fromIndex / size) + 1;
        }
        int toIndex = Math.min(toanBo.size(), fromIndex + size);
        return new PhanTrang<>(toanBo.subList(fromIndex, toIndex), p, size, toanBo.size());
    }

    public List<T> getDanhSach() {
        return danhSach;
    }

    public int getTrangHienTai() {
        return trangHienTai;
    }

    public int getKichThuoc() {
        return kichThuoc;
    }

    public long getTongPhanTu() {
        return tongPhanTu;
    }

    public int getTongTrang() {
        return tongTrang;
    }

    public boolean isCoTrangTruoc() {
        return trangHienTai > 1;
    }

    public boolean isCoTrangSau() {
        return trangHienTai < tongTrang;
    }
}
