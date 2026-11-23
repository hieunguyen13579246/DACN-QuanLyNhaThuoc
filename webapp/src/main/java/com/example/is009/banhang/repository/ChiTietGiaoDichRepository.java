package com.example.is009.banhang.repository;

import com.example.is009.banhang.entity.ChiTietGiaoDich;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietGiaoDichRepository extends JpaRepository<ChiTietGiaoDich, Integer> {
    List<ChiTietGiaoDich> findByGiaoDichId(Integer giaoDichId);
}
