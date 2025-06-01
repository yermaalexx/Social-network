package com.yermaalexx.socialnetwork.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class LoginController {

    @GetMapping({"/", "/login"})
    public String login(HttpServletRequest request) {
        log.info("Login page requested from IP={}", request.getRemoteAddr());

        return "login";
    }
}
