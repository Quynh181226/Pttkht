package com.hospital.controller;

import com.hospital.dto.LoginDTO;
import com.hospital.dto.RegisterDTO;
import com.hospital.entity.User;
import com.hospital.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "/views/login";
    }

    @PostMapping("/login")
    public String login(@Valid LoginDTO loginDTO, BindingResult result,
                        HttpSession session, RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/views/login";
        }

        var userOpt = userService.login(loginDTO.getPhone(), loginDTO.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("userPhone", user.getPhone());
            session.setAttribute("userName", user.getFullname());
            session.setAttribute("role", user.getRole());

            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            } else if ("DOCTOR".equals(user.getRole())) {
                return "redirect:/doctor/patients";
            } else {
                return "redirect:/";
            }
        } else {
            ra.addFlashAttribute("error", "Số điện thoại hoặc mật khẩu không đúng");
            return "redirect:/views/login";
        }
    }

    @GetMapping("/register")
    public String registerForm() {
        return "/views/register";
    }

    @PostMapping("/register")
    public String register(@Valid RegisterDTO registerDTO, BindingResult result,
                           RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/views/register";
        }

        User user = User.builder()
                .fullname(registerDTO.getFullname())
                .phone(registerDTO.getPhone())
                .email(registerDTO.getEmail())
                .password(registerDTO.getPassword())
                .dob(registerDTO.getDob())
                .gender(registerDTO.getGender())
                .address(registerDTO.getAddress())
                .identityNo(registerDTO.getIdentityNo())
                .insuranceNo(registerDTO.getInsuranceNo())
                .build();

        if (userService.register(user)) {
            ra.addFlashAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/views/login";
        } else {
            ra.addFlashAttribute("error", "Số điện thoại đã được đăng ký");
            return "redirect:/views/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}