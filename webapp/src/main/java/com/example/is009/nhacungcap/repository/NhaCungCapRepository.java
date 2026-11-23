package com.example.is009.nhacungcap.repository;

import com.example.is009.nhacungcap.entity.NhaCungCap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, Integer> {

    List<NhaCungCap> findByTrangThaiOrderByTenAsc(String trangThai);

    boolean existsByMaIgnoreCase(String ma);
}
