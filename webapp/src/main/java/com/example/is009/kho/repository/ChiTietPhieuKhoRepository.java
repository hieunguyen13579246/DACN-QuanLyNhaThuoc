package com.example.is009.kho.repository;

import com.example.is009.kho.entity.ChiTietPhieuKho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietPhieuKhoRepository extends JpaRepository<ChiTietPhieuKho, Integer> {
    List<ChiTietPhieuKho> findByPhieuKhoId(Integer phieuKhoId);
}
