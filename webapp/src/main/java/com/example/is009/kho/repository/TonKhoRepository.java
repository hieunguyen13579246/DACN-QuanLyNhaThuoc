package com.example.is009.kho.repository;

import com.example.is009.kho.entity.TonKho;
import com.example.is009.kho.entity.TonKhoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TonKhoRepository extends JpaRepository<TonKho, TonKhoId> {
    List<TonKho> findByIdNhaThuocId(Integer nhaThuocId);
}
