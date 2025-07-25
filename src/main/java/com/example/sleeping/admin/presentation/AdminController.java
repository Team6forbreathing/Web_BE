package com.example.sleeping.admin.presentation;

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
    ) {
        return "user.html";
    }

    @GetMapping
    public String mainPage(
    ) {
        return "main.html";
    }

    @GetMapping("/schedulerPage")
    public String schedule(
    ) {
        return "scheduler.html";
    }
}
