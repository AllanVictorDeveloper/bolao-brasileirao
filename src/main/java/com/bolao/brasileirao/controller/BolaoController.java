package com.bolao.brasileirao.controller;

import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.entity.Palpite;
import com.bolao.brasileirao.repository.JogoRepository;
import com.bolao.brasileirao.repository.PalpiteRepository;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bolao")
public class BolaoController {

 @Autowired private JogoRepository jogoRepo;
 @Autowired private PalpiteRepository palpiteRepo;

 @GetMapping("/rodada/{rodada}")
 public String rodada(@PathVariable Integer rodada, Model model){
    model.addAttribute("jogos", jogoRepo.findByRodada(rodada));
    return "rodadas";
 }

}
