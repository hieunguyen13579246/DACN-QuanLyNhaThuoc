package com.example.is009.khuvuc.repository;

import com.example.is009.khuvuc.entity.KhuVuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhuVucRepository extends JpaRepository<KhuVuc, Integer> {

    List<KhuVuc> findByCapOrderByTenAsc(String cap);

    List<KhuVuc> findByKhuVucChaIdOrderByTenAsc(Integer khuVucChaId);

    boolean existsByMaIgnoreCase(String ma);
}
