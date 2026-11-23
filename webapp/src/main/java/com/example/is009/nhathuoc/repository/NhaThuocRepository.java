package com.example.is009.nhathuoc.repository;

import com.example.is009.nhathuoc.entity.NhaThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhaThuocRepository extends JpaRepository<NhaThuoc, Integer> {

    List<NhaThuoc> findByTrangThaiOrderByIdAsc(String trangThai);

    boolean existsByMaIgnoreCase(String ma);
}
