package com.example.is009.kho.repository;

import com.example.is009.kho.entity.LoThuoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoThuocRepository extends JpaRepository<LoThuoc, Integer> {
    List<LoThuoc> findByThuocIdOrderByHanDungAsc(Integer thuocId);
}
