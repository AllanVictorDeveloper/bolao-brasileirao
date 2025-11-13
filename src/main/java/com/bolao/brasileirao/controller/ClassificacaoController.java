package com.bolao.brasileirao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClassificacaoController {

    @GetMapping("/classificacao")
    public String classificacao(Model model) {
        model.addAttribute("pagina", "classificacao");
        return "classificacao";
    }
}
