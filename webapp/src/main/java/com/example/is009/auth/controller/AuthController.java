package com.example.is009.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping({"/auth/login", "/login"})
    public String trangDangNhap() {
        return "auth/login";
    }
}
