package com.example.is009.banhang.repository;

import com.example.is009.banhang.entity.GiaoDich;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiaoDichRepository extends JpaRepository<GiaoDich, Integer> {
    List<GiaoDich> findTop100ByNhaThuocIdOrderByThoiGianDesc(Integer nhaThuocId);
    List<GiaoDich> findTop100ByOrderByThoiGianDesc();
}
