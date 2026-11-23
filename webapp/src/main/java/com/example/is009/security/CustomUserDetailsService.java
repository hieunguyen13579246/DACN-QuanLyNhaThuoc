package com.example.is009.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SecurityAuthRepository authRepository;

    public CustomUserDetailsService(SecurityAuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authRepository.timTheoEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với email: " + username));
    }
}
