package com.example.is009.khachhang.repository;

import com.example.is009.khachhang.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    List<KhachHang> findTop50ByHoTenContainingIgnoreCaseOrSoDienThoaiContainingOrMaContainingIgnoreCaseOrderByIdDesc(
            String hoTen, String soDienThoai, String ma);

    List<KhachHang> findTop100ByOrderByIdDesc();

    List<KhachHang> findTop10BySoDienThoaiContainingOrderByIdDesc(String soDienThoai);
}
