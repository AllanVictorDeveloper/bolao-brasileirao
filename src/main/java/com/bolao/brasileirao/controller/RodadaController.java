package com.bolao.brasileirao.controller;

import com.bolao.brasileirao.dtos.JogoView;
import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.repository.JogoRepository;
import com.bolao.brasileirao.services.RodadaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RodadaController {

    private final RodadaService rodadaService;
    private final JogoRepository jogoRepository;

    public RodadaController(RodadaService rodadaService, JogoRepository jogoRepository) {
        this.rodadaService = rodadaService;
        this.jogoRepository = jogoRepository;
    }


    @GetMapping("/rodadas")
    public String listarRodadas(
            @RequestParam(name = "rodada", required = false) Integer rodadaParam,
            Model model) {

        int rodadaAtual = rodadaService.obterRodadaAtual();
        int rodada = (rodadaParam != null) ? rodadaParam : rodadaAtual;

        List<Jogo> jogos = jogoRepository.findByRodadaOrderByDataJogoAsc(rodada);

        // Converte para view
        List<JogoView> jogosView = jogos.stream()
                .map(JogoView::new)
                .toList();

        // Regra global da rodada: abre quando a rodada anterior terminar,
        // fecha 20 min antes do primeiro jogo desta rodada
        boolean podeCriarRodada = rodadaService.podeCriarPalpite(rodada, jogos);
        for (JogoView j : jogosView) {
            j.setPodeCriarPalpite(podeCriarRodada);
        }

        model.addAttribute("jogos", jogosView);
        model.addAttribute("rodada", rodada);
        model.addAttribute("semJogos", jogos.isEmpty());
        model.addAttribute("hasAnterior", jogoRepository.existsByRodada(rodada - 1));
        model.addAttribute("hasProxima", jogoRepository.existsByRodada(rodada + 1));
        model.addAttribute("pagina", "rodadas");

        return "rodadas";
    }


}
