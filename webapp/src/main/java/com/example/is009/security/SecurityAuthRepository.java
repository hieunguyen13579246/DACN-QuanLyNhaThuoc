package com.example.is009.security;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class SecurityAuthRepository {

    private final JdbcTemplate jdbcTemplate;

    SecurityAuthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    Optional<CustomUserDetails> timTheoEmail(String email) {
        String clean = email != null ? email.trim() : "";
        if ("admin".equalsIgnoreCase(clean)) {
            clean = "qt01@nhathuoc.vn";
        } else if (!clean.contains("@")) {
            clean = clean + "@nhathuoc.vn";
        }
        String sql = """
                SELECT nd.id, nd.ho_ten, nd.email, nd.password_hash, nd.vai_tro,
                       nd.nha_thuoc_id, nt.ma AS ma_nha_thuoc, nt.ten AS ten_nha_thuoc, nd.kich_hoat
                FROM NguoiDung nd
                LEFT JOIN NhaThuoc nt ON nt.id = nd.nha_thuoc_id
                WHERE nd.email = ?
                """;
        List<CustomUserDetails> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Integer ntId = rs.getObject("nha_thuoc_id") != null ? rs.getInt("nha_thuoc_id") : null;
            return new CustomUserDetails(
                    rs.getInt("id"),
                    rs.getString("ho_ten"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    VaiTro.valueOf(rs.getString("vai_tro")),
                    ntId,
                    rs.getString("ma_nha_thuoc"),
                    rs.getString("ten_nha_thuoc"),
                    rs.getBoolean("kich_hoat")
            );
        }, clean);
        return list.stream().findFirst();
    }
}
