package com.example.sleeping.admin.presentation;

import com.example.sleeping.global.annotation.AdminUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/login")
    public String adminLogin() {
        return "login.html";
    }

    @GetMapping
    public String admin(
            @AdminUser String userId
    ) {
        return "admin.html";
    }
}
