package com.example.is009.kho.repository;

import com.example.is009.kho.entity.DinhMucTon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DinhMucTonRepository extends JpaRepository<DinhMucTon, Integer> {
    Optional<DinhMucTon> findByNhaThuocIdAndThuocId(Integer nhaThuocId, Integer thuocId);
}
