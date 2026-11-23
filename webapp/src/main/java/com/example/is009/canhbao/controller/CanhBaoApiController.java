package com.example.is009.canhbao.controller;

import com.example.is009.canhbao.dto.CanhBaoHanDungDTO;
import com.example.is009.canhbao.dto.CanhBaoTonThapDTO;
import com.example.is009.canhbao.dto.TonChamDTO;
import com.example.is009.canhbao.dto.TongHopCanhBaoDTO;
import com.example.is009.canhbao.service.CanhBaoService;
import com.example.is009.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/canhbao")
public class CanhBaoApiController {

    private final CanhBaoService canhBaoService;

    public CanhBaoApiController(CanhBaoService canhBaoService) {
        this.canhBaoService = canhBaoService;
    }

    @GetMapping("/tong-hop")
    public ApiResponse<TongHopCanhBaoDTO> tongHop(@RequestParam(required = false) Integer nhaThuocId) {
        return ApiResponse.thanhCong(canhBaoService.layTongHopCanhBao(nhaThuocId));
    }

    @GetMapping("/han-dung")
    public ApiResponse<List<CanhBaoHanDungDTO>> hanDung(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) String mucCanhBao) {
        return ApiResponse.thanhCong(canhBaoService.layCanhBaoHanDung(nhaThuocId, mucCanhBao));
    }

    @GetMapping("/ton-thap")
    public ApiResponse<List<CanhBaoTonThapDTO>> tonThap(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) String mucCanhBao) {
        return ApiResponse.thanhCong(canhBaoService.layCanhBaoTonThap(nhaThuocId, mucCanhBao));
    }

    @GetMapping("/ton-cham")
    public ApiResponse<List<TonChamDTO>> tonCham(@RequestParam(required = false) Integer nhaThuocId) {
        return ApiResponse.thanhCong(canhBaoService.layTonChamLuanChuyen(nhaThuocId));
    }
}
