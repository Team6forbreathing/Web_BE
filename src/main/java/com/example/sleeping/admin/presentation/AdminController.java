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

    @GetMapping("/userPage")
    public String user(
            @AdminUser String userId
    ) {
        return "user.html";
    }

    @GetMapping
    public String main(
            @AdminUser String userId
    ) {
        return "main.html";
    }

    @GetMapping("/schedulerPage")
    public String schedule(
            @AdminUser String userId
    ) {
        return "scheduler.html";
    }
}
