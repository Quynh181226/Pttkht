package com.hospital.service.impl;

import com.hospital.dto.LoginDTO;
import com.hospital.entity.User;
import com.hospital.repository.UserRepository;
import com.hospital.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean authenticate(LoginDTO loginDTO, HttpSession session) {
        String usernameOrEmail = loginDTO.getUsernameOrEmail();
        String password = loginDTO.getPassword();

        User user = userRepository.findByUsernameOrPhoneOrEmail(usernameOrEmail, usernameOrEmail, usernameOrEmail).orElse(null);

        if (user == null) {
            return false;
        }

        //if (!password.equals(user.getPassword())) {
        //    return false;
        //}

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return false;
        }

        // Store session
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("userName", user.getFullName());
        session.setAttribute("userPhone", user.getPhone());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("role", user.getRole().name());

        return true;
    }

    @Override
    public void logout(HttpSession session) {
        session.invalidate();
    }
}