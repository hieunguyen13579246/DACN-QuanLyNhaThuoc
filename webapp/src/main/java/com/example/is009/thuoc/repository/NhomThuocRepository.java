package com.example.is009.thuoc.repository;

import com.example.is009.thuoc.entity.NhomThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NhomThuocRepository extends JpaRepository<NhomThuoc, Integer> {

    boolean existsByMaIgnoreCase(String ma);
}
