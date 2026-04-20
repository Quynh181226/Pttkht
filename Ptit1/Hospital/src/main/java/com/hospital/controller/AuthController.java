package com.hospital.controller;

import com.hospital.dto.LoginDTO;
import com.hospital.dto.RegisterDTO;
import com.hospital.entity.User;
import com.hospital.service.AuthService;
import com.hospital.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    // ==================== LOGIN ====================
    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {
        // If already logged in, redirect to appropriate dashboard
        if (session.getAttribute("userId") != null) {
            String role = (String) session.getAttribute("role");
            return redirectByRole(role);
        }

        if (!model.containsAttribute("loginDTO")) {
            model.addAttribute("loginDTO", new LoginDTO());
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("loginDTO", loginDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginDTO", bindingResult);
            return "redirect:/login";
        }

        boolean authenticated = authService.authenticate(loginDTO, session);

        if (!authenticated) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập/Email hoặc mật khẩu không đúng!");
            redirectAttributes.addFlashAttribute("loginDTO", loginDTO);
            return "redirect:/login";
        }

        String role = (String) session.getAttribute("role");

        if (!"patient".equalsIgnoreCase(role)) {
            session.invalidate();
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập vào trang User!!");
            return "redirect:/login";
        }

        return redirectByRole(role);
    }

    // ==================== REGISTER ====================
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("registerDTO")) {
            model.addAttribute("registerDTO", new RegisterDTO());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {

        // Check password matching
        if (!registerDTO.isPasswordMatching()) {
            bindingResult.rejectValue("confirmPassword", "error.registerDTO", "Mật khẩu xác nhận không khớp");
        }

        // Check if username exists
        if (userService.isUsernameExists(registerDTO.getUsername())) {
            bindingResult.rejectValue("username", "error.registerDTO", "Tên đăng nhập đã tồn tại");
        }

        // Check if email exists
        if (userService.isEmailExists(registerDTO.getEmail())) {
            bindingResult.rejectValue("email", "error.registerDTO", "Email đã được đăng ký");
        }

        // Check if phone exists
        if (userService.isPhoneExists(registerDTO.getPhone())) {
            bindingResult.rejectValue("phone", "error.registerDTO", "Số điện thoại đã được đăng ký");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerDTO", registerDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerDTO", bindingResult);
            return "redirect:/register";
        }

        try {
            userService.registerUser(registerDTO);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("registerDTO", registerDTO);
            return "redirect:/register";
        }
    }

    // ==================== LOGOUT ====================
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    // ==================== HELPER ====================
    private String redirectByRole(String role) {
        if ("admin".equalsIgnoreCase(role)) {
            return "redirect:/admin/dashboard";
        } else if ("doctor".equalsIgnoreCase(role)) {
            return "redirect:/doctor/dashboard";
        } else if ("patient".equalsIgnoreCase(role)) {
            return "redirect:/patient/dashboard";
        } else {
            return "redirect:/login";
        }
    }

    // ==================== LOGIN FOR ADMIN & DOCTOR ====================
    @GetMapping("/login-mana")
    public String showLoginManaForm(Model model, HttpSession session) {
        if (session.getAttribute("userId") != null) {
            String role = (String) session.getAttribute("role");
            if ("admin".equals(role)) return "redirect:/admin/dashboard";
            if ("doctor".equals(role)) return "redirect:/doctor/dashboard";
        }
        if (!model.containsAttribute("loginDTO")) {
            model.addAttribute("loginDTO", new LoginDTO());
        }
        return "auth/login-mana";
    }

    @PostMapping("/login-mana")
    public String processLoginMana(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
                                   BindingResult bindingResult,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("loginDTO", loginDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginDTO", bindingResult);
            return "redirect:/login-mana";
        }

        boolean authenticated = authService.authenticate(loginDTO, session);

        if (!authenticated) {
            redirectAttributes.addFlashAttribute("error", "Email hoặc mật khẩu không đúng!");
            redirectAttributes.addFlashAttribute("loginDTO", loginDTO);
            return "redirect:/login-mana";
        }

        String role = (String) session.getAttribute("role");

        // Kiểm tra quyền truy cập cho login-mana
        if (!"admin".equalsIgnoreCase(role) && !"doctor".equalsIgnoreCase(role)) {
            // Xóa session vì không có quyền
            session.invalidate();
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập vào trang Mana!!");
            return "redirect:/login-mana";
        }

        if ("admin".equalsIgnoreCase(role)) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/doctor/dashboard";
        }
    }
}












//@PostMapping("/login")
//public String processLogin(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
//                           BindingResult bindingResult,
//                           HttpSession session,
//                           RedirectAttributes redirectAttributes) {
//
//    if (bindingResult.hasErrors()) {
//        redirectAttributes.addFlashAttribute("loginDTO", loginDTO);
//        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginDTO", bindingResult);
//        return "redirect:/login";
//    }
//
//    // Lưu usernameOrEmail để kiểm tra sau
//    String usernameOrEmail = loginDTO.getUsernameOrEmail();
//
//    boolean authenticated = authService.authenticate(loginDTO, session);
//
//    if (!authenticated) {
//        User user = userService.findByUsernameOrEmail(usernameOrEmail);
//
//        if (user == null) {
//            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc Email không tồn tại.");
//        } else {
//            redirectAttributes.addFlashAttribute("error", "Mật khẩu không chính xác.");
//        }
//
//        redirectAttributes.addFlashAttribute("loginDTO", loginDTO);
//        return "redirect:/login";
//    }
//
//    // Login thành công
//    String role = (String) session.getAttribute("role");
//
//    if (!"patient".equalsIgnoreCase(role)) {
//        session.invalidate();
//        redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập vào trang dành cho bệnh nhân.");
//        return "redirect:/login";
//    }
//
//    return redirectByRole(role);
//}