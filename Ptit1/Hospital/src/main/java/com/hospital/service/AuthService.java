package com.hospital.service;

import com.hospital.dto.LoginDTO;
import jakarta.servlet.http.HttpSession;

public interface AuthService {
    boolean authenticate(LoginDTO loginDTO, HttpSession session);

    void logout(HttpSession session);
}