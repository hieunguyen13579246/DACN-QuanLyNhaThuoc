package com.example.is009.kho.dto;

import jakarta.validation.constraints.NotBlank;

public class DuyetPhieuRequest {

    @NotBlank(message = "Phải chọn kết quả duyệt (DUYET hoặc TU_CHOI)")
    private String ketQua;

    private String lyDo;

    public String getKetQua() { return ketQua; }
    public void setKetQua(String ketQua) { this.ketQua = ketQua; }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
}
