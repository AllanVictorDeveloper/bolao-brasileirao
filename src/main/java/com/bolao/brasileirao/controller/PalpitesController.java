package com.bolao.brasileirao.controller;

import com.bolao.brasileirao.entity.Palpite;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PalpitesController {

    @GetMapping("/meus-palpites")
    public String classificacao(Model model) {
        model.addAttribute("pagina", "palpites");
        return "palpites";
    }

//    @GetMapping("/meus-palpites")
//    public String meusPalpites(Model model) {
//        List<Palpite> lista = service.buscarPalpitesDoUsuario();
//        model.addAttribute("palpites", lista);
//        model.addAttribute("pagina", "palpites");
//        model.addAttribute("titulo", "Meus Palpites");
//        return "palpites";
//    }
}
