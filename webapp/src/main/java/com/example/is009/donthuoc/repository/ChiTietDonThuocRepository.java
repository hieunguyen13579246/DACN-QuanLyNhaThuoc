package com.example.is009.donthuoc.repository;

import com.example.is009.donthuoc.entity.ChiTietDonThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDonThuocRepository extends JpaRepository<ChiTietDonThuoc, Integer> {

    List<ChiTietDonThuoc> findByDonThuocId(Integer donThuocId);
}
