package com.example.is009.common;

public class ApiResponse<T> {

    private final boolean thanhCong;
    private final String maLoi;
    private final String thongBao;
    private final String huongDanSua;
    private final T duLieu;

    public ApiResponse(boolean thanhCong, String maLoi, String thongBao, String huongDanSua, T duLieu) {
        this.thanhCong = thanhCong;
        this.maLoi = maLoi;
        this.thongBao = thongBao;
        this.huongDanSua = huongDanSua;
        this.duLieu = duLieu;
    }

    public static <T> ApiResponse<T> thanhCong(String thongBao, T duLieu) {
        return new ApiResponse<>(true, null, thongBao, null, duLieu);
    }

    public static <T> ApiResponse<T> thanhCong(T duLieu) {
        return new ApiResponse<>(true, null, "Thành công", null, duLieu);
    }

    public static <T> ApiResponse<T> thatBai(String maLoi, String thongBao, String huongDanSua) {
        return new ApiResponse<>(false, maLoi, thongBao, huongDanSua, null);
    }

    public boolean isThanhCong() {
        return thanhCong;
    }

    public String getMaLoi() {
        return maLoi;
    }

    public String getThongBao() {
        return thongBao;
    }

    public String getHuongDanSua() {
        return huongDanSua;
    }

    public T getDuLieu() {
        return duLieu;
    }
}
