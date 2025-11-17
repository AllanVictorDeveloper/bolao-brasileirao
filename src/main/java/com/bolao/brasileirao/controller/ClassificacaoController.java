package com.bolao.brasileirao.controller;

import com.bolao.brasileirao.services.ClassificacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClassificacaoController {

    private final ClassificacaoService classificacaoService;

    public ClassificacaoController(ClassificacaoService classificacaoService) {
        this.classificacaoService = classificacaoService;
    }

    @GetMapping("/classificacao")
    public String classificacao(Model model) {

        model.addAttribute("ranking", classificacaoService.rankingGeral());
        model.addAttribute("pagina", "classificacao");

        return "classificacao";
    }
}
