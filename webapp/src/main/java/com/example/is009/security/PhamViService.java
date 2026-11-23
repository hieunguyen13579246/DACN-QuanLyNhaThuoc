package com.example.is009.security;

import com.example.is009.exception.NghiepVuException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PhamViService {

    public CustomUserDetails layNguoiDungHienTai() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new NghiepVuException("50013",
                    "Phiên đăng nhập không hợp lệ hoặc đã hết hạn.",
                    "Vui lòng đăng nhập lại vào hệ thống.");
        }
        return user;
    }

    public int layNguoiDungId() {
        return layNguoiDungHienTai().getId();
    }

    public VaiTro layVaiTro() {
        return layNguoiDungHienTai().getVaiTro();
    }

    public Integer layNhaThuocIdHienTai() {
        return layNguoiDungHienTai().getNhaThuocId();
    }

    public boolean laQuanTri() {
        return layVaiTro() == VaiTro.QUANTRI;
    }

    public boolean laQuanLy() {
        return layVaiTro() == VaiTro.QUANLY;
    }

    public boolean laThuKho() {
        return layVaiTro() == VaiTro.THUKHO;
    }

    public boolean laDuocSi() {
        return layVaiTro() == VaiTro.DUOCSI;
    }

    /**
     * Trả về nha_thuoc_id của người dùng hiện tại (dùng cho DUOCSI, THUKHO, QUANLY).
     * Nếu là QUANTRI và truyền nhaThuocIdYeuCau != null thì dùng nhaThuocIdYeuCau.
     */
    public int batBuocNhaThuocCuaMinh(Integer nhaThuocIdYeuCau) {
        CustomUserDetails user = layNguoiDungHienTai();
        if (user.getVaiTro() == VaiTro.QUANTRI) {
            if (nhaThuocIdYeuCau != null) {
                return nhaThuocIdYeuCau;
            }
            return 1; // Mặc định NT001 khi QUANTRI tra cứu màn hình cấp nhà thuốc nếu chưa chọn
        }
        Integer ntId = user.getNhaThuocId();
        if (ntId == null) {
            throw new NghiepVuException("50011",
                    "Tài khoản chưa được gán nhà thuốc.",
                    "Liên hệ Quản trị viên để gán nhà thuốc làm việc.");
        }
        if (nhaThuocIdYeuCau != null && !ntId.equals(nhaThuocIdYeuCau)) {
            throw new NghiepVuException("50011",
                    "Bạn không có quyền truy cập hoặc thao tác dữ liệu của nhà thuốc khác (BR13).",
                    "Chỉ thao tác trên dữ liệu thuộc nhà thuốc của bạn (" + user.getTenNhaThuoc() + ").");
        }
        return ntId;
    }

    public int batBuocNhaThuocCuaMinh() {
        return batBuocNhaThuocCuaMinh(null);
    }

    /**
     * Dùng cho báo cáo/thống kê/cảnh báo (BR13):
     * - QUANTRI: trả về nhaThuocIdYeuCau (NULL = toàn chuỗi)
     * - Các vai trò khác: bắt buộc trả về nha_thuoc_id của chính họ; nếu truyền nhaThuocId khác sẽ báo lỗi 50011.
     */
    public Integer xacDinhPhamViNhaThuoc(Integer nhaThuocIdYeuCau) {
        CustomUserDetails user = layNguoiDungHienTai();
        if (user.getVaiTro() == VaiTro.QUANTRI) {
            return nhaThuocIdYeuCau;
        }
        Integer ntId = user.getNhaThuocId();
        if (ntId == null) {
            throw new NghiepVuException("50011", "Tài khoản không có phạm vi nhà thuốc hợp lệ.");
        }
        if (nhaThuocIdYeuCau != null && !ntId.equals(nhaThuocIdYeuCau)) {
            throw new NghiepVuException("50011",
                    "Bạn không được phép xem dữ liệu của nhà thuốc khác (BR13).",
                    "Vui lòng chọn đúng nhà thuốc của bạn.");
        }
        return ntId;
    }

    public void kiemTraQuyenNhaThuoc(int nhaThuocId) {
        CustomUserDetails user = layNguoiDungHienTai();
        if (user.getVaiTro() == VaiTro.QUANTRI) {
            return;
        }
        if (user.getNhaThuocId() == null || user.getNhaThuocId() != nhaThuocId) {
            throw new NghiepVuException("50011",
                    "Không có quyền thao tác dữ liệu của nhà thuốc khác (BR13).",
                    "Chỉ thao tác trong phạm vi nhà thuốc được phân công.");
        }
    }

    public void batBuocVaiTro(VaiTro... danhSachVaiTro) {
        VaiTro hienTai = layVaiTro();
        for (VaiTro vt : danhSachVaiTro) {
            if (vt == hienTai) {
                return;
            }
        }
        throw new NghiepVuException("50011",
                "Vai trò " + hienTai.getTenHienThi() + " không được phép thực hiện chức năng này.",
                "Đăng nhập bằng tài khoản có vai trò phù hợp.");
    }
}
