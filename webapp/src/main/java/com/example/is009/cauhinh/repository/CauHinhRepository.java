package com.example.is009.cauhinh.repository;

import com.example.is009.cauhinh.entity.CauHinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CauHinhRepository extends JpaRepository<CauHinh, String> {
}
