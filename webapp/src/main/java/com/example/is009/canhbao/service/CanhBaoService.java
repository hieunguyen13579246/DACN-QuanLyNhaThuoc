package com.example.is009.canhbao.service;

import com.example.is009.canhbao.dto.CanhBaoHanDungDTO;
import com.example.is009.canhbao.dto.CanhBaoTonThapDTO;
import com.example.is009.canhbao.dto.TonChamDTO;
import com.example.is009.canhbao.dto.TongHopCanhBaoDTO;
import com.example.is009.canhbao.repository.CanhBaoRepository;
import com.example.is009.security.PhamViService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanhBaoService {

    private final CanhBaoRepository canhBaoRepository;
    private final PhamViService phamViService;

    public CanhBaoService(CanhBaoRepository canhBaoRepository, PhamViService phamViService) {
        this.canhBaoRepository = canhBaoRepository;
        this.phamViService = phamViService;
    }

    public List<CanhBaoHanDungDTO> layCanhBaoHanDung(Integer nhaThuocIdLoc, String mucCanhBao) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        return canhBaoRepository.truyVanCanhBaoHanDung(phamViId, mucCanhBao);
    }

    public List<CanhBaoTonThapDTO> layCanhBaoTonThap(Integer nhaThuocIdLoc, String mucCanhBao) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        return canhBaoRepository.truyVanCanhBaoTonThap(phamViId, mucCanhBao);
    }

    public List<TonChamDTO> layTonChamLuanChuyen(Integer nhaThuocIdLoc) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        return canhBaoRepository.truyVanTonChamLuanChuyen(phamViId);
    }

    public TongHopCanhBaoDTO layTongHopCanhBao(Integer nhaThuocIdLoc) {
        List<CanhBaoHanDungDTO> hd = layCanhBaoHanDung(nhaThuocIdLoc, null);
        List<CanhBaoTonThapDTO> tt = layCanhBaoTonThap(nhaThuocIdLoc, null);
        List<TonChamDTO> tc = layTonChamLuanChuyen(nhaThuocIdLoc);

        TongHopCanhBaoDTO th = new TongHopCanhBaoDTO();
        th.setSoLoHetHan((int) hd.stream().filter(x -> "DA_HET_HAN".equals(x.getMucCanhBao())).count());
        th.setSoLoCanHan30((int) hd.stream().filter(x -> "MUC_30".equals(x.getMucCanhBao()) || "CAN_HAN_30".equals(x.getMucCanhBao())).count());
        th.setSoLoCanHan60((int) hd.stream().filter(x -> "MUC_60".equals(x.getMucCanhBao()) || "CAN_HAN_60".equals(x.getMucCanhBao())).count());
        th.setSoLoCanHan90((int) hd.stream().filter(x -> "MUC_90".equals(x.getMucCanhBao()) || "CAN_HAN_90".equals(x.getMucCanhBao())).count());
        th.setSoThuocHetHang((int) tt.stream().filter(x -> "HET_HANG".equals(x.getMucCanhBao())).count());
        th.setSoThuocTonThap((int) tt.stream().filter(x -> "TON_THAP".equals(x.getMucCanhBao())).count());
        th.setSoLoTonCham(tc.size());
        return th;
    }
}
