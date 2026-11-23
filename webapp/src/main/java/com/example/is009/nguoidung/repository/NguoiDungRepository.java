package com.example.is009.nguoidung.repository;

import com.example.is009.nguoidung.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {

    boolean existsByEmailIgnoreCase(String email);
}
