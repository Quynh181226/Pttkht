package com.hospital.service.impl;

import com.hospital.dto.RegisterDTO;
import com.hospital.entity.User;
import com.hospital.enums.Role;
import com.hospital.repository.UserRepository;
import com.hospital.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public User registerUser(@Valid RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
//        user.setPassword(registerDTO.getPassword());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setFullName(registerDTO.getFullName());
        user.setRole(Role.patient);
        user.setCreatedAt(LocalDateTime.now());

        String patientCode = "PAT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        user.setPatientCode(patientCode);

        return userRepository.save(user);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public User findByUsernameOrPhoneOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrPhoneOrEmail(usernameOrEmail, usernameOrEmail, usernameOrEmail).orElse(null);
    }

//    @Override
//    public User findByUsernameOrEmail(String usernameOrEmail) {
//        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);
//    }
}