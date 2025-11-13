package com.bolao.brasileirao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/esqueci-senha")
    public String forgotPassword() {
        return "forgot-password";
    }


    @PostMapping("/esqueci-senha")
    public String sendRecoveryEmail(@RequestParam String email) {
        // TODO: enviar email real
        System.out.println("Enviar link para: " + email);
        return "redirect:/login?resetSent=true";
    }
}
