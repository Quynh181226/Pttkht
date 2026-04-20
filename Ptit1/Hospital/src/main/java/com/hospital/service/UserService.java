package com.hospital.service;

import com.hospital.dto.RegisterDTO;
import com.hospital.entity.User;
import jakarta.validation.Valid;

public interface UserService {
    User registerUser(@Valid RegisterDTO registerDTO);

    boolean isUsernameExists(String username);

    boolean isEmailExists(String email);

    boolean isPhoneExists(String phone);

    User findByUsernameOrPhoneOrEmail(String usernameOrEmail);
}