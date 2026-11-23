package com.example.is009.banhang.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HuyGiaoDichRequest {

    @NotBlank(message = "Phải nhập lý do hủy giao dịch")
    @Size(max = 300, message = "Lý do hủy tối đa 300 ký tự")
    private String lyDo;

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
}
