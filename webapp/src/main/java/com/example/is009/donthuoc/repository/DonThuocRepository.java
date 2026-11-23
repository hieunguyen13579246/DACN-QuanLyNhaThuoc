package com.example.is009.donthuoc.repository;

import com.example.is009.donthuoc.entity.DonThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonThuocRepository extends JpaRepository<DonThuoc, Integer> {
}
