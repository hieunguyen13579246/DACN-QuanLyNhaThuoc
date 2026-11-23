package com.example.is009.thuoc.repository;

import com.example.is009.thuoc.entity.Thuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThuocRepository extends JpaRepository<Thuoc, Integer> {

    List<Thuoc> findByTenContainingIgnoreCaseOrMaContainingIgnoreCaseOrHoatChatContainingIgnoreCaseOrderByTenAsc(
            String ten, String ma, String hoatChat);

    boolean existsByMaIgnoreCase(String ma);
}
