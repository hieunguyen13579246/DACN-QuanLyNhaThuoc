package com.example.is009.thuoc.repository;

import com.example.is009.thuoc.entity.MaVachThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaVachThuocRepository extends JpaRepository<MaVachThuoc, Integer> {

    Optional<MaVachThuoc> findByMaVach(String maVach);

    List<MaVachThuoc> findByThuocIdOrderByLaMaChinhDescIdAsc(Integer thuocId);
}
