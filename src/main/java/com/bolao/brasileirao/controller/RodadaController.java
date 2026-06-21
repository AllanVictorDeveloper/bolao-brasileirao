package com.bolao.brasileirao.controller;

import com.bolao.brasileirao.dtos.JogoView;
import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.entity.Palpite;
import com.bolao.brasileirao.entity.Usuario;
import com.bolao.brasileirao.repository.JogoRepository;
import com.bolao.brasileirao.services.PalpiteService;
import com.bolao.brasileirao.services.RodadaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RodadaController {

    private final RodadaService rodadaService;
    private final JogoRepository jogoRepository;
    private final PalpiteService palpiteService;

    public RodadaController(RodadaService rodadaService, JogoRepository jogoRepository,
                            PalpiteService palpiteService) {
        this.rodadaService = rodadaService;
        this.jogoRepository = jogoRepository;
        this.palpiteService = palpiteService;
    }

    @GetMapping("/rodadas")
    public String listarRodadas(
            @RequestParam(name = "rodada", required = false) Integer rodadaParam,
            @AuthenticationPrincipal Usuario usuarioLogado,
            Model model) {

        int rodadaAtual = rodadaService.obterRodadaAtual();
        int rodada = (rodadaParam != null) ? rodadaParam : rodadaAtual;

        List<Jogo> jogos = jogoRepository.findByRodadaOrderByDataJogoAsc(rodada);

        Map<Long, Palpite> mapPalpitesPorJogo = new HashMap<>();
        if (usuarioLogado != null) {
            System.out.println(">>> [DEBUG] usuarioId=" + usuarioLogado.getId() + " rodada=" + rodada);
            List<Palpite> palpites = palpiteService.buscarPalpitesUsuarioRodada(usuarioLogado.getId(), rodada);
            System.out.println(">>> [DEBUG] palpites encontrados=" + palpites.size());
            for (Palpite p : palpites) {
                System.out.println(">>> [DEBUG] palpite jogoId=" + p.getJogo().getId());
                mapPalpitesPorJogo.put(p.getJogo().getId(), p);
            }
        } else {
            System.out.println(">>> [DEBUG] usuarioLogado é NULL");
        }

        List<JogoView> jogosView = jogos.stream().map(JogoView::new).toList();
        boolean podeCriarRodada = rodadaService.podeCriarPalpite(rodada, jogos);
        for (JogoView j : jogosView) {
            j.setPodeCriarPalpite(podeCriarRodada);
            if (mapPalpitesPorJogo.containsKey(j.getJogo().getId())) {
                j.setTemPalpite(true);
            }
            j.calcularBotoes();
        }

        model.addAttribute("jogos", jogosView);
        model.addAttribute("palpitesPorJogo", mapPalpitesPorJogo);
        model.addAttribute("rodada", rodada);
        model.addAttribute("semJogos", jogos.isEmpty());
        model.addAttribute("hasAnterior", jogoRepository.existsByRodada(rodada - 1));
        model.addAttribute("hasProxima", jogoRepository.existsByRodada(rodada + 1));
        model.addAttribute("pagina", "rodadas");

        return "rodadas";
    }
}
