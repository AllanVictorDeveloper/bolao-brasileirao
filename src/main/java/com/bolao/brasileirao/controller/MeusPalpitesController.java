package com.bolao.brasileirao.controller;

import com.bolao.brasileirao.dtos.PalpiteRequest;
import com.bolao.brasileirao.entity.Jogador;
import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.entity.Palpite;
import com.bolao.brasileirao.entity.Usuario;
import com.bolao.brasileirao.repository.JogoRepository;
import com.bolao.brasileirao.services.PalpiteService;
import com.bolao.brasileirao.services.RodadaService;
import com.bolao.brasileirao.services.interfaces.IJogadorService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MeusPalpitesController {

    private final PalpiteService palpiteService;
    private final RodadaService rodadaService;
    private final JogoRepository jogoRepository;
    private final IJogadorService jogadorService;

    public MeusPalpitesController(PalpiteService palpiteService,
                                  RodadaService rodadaService, JogoRepository jogoRepository, IJogadorService jogadorService) {
        this.palpiteService = palpiteService;
        this.rodadaService = rodadaService;
        this.jogoRepository = jogoRepository;
        this.jogadorService = jogadorService;
    }

    @GetMapping("/meus-palpites")
    public String listarMeusPalpites(@AuthenticationPrincipal Usuario usuarioLogado,
                                     @RequestParam(name = "rodada", required = false) Integer rodadaParam,
                                     Model model) {

        int rodadaAtual = rodadaService.obterRodadaAtual();
        int rodada = (rodadaParam != null) ? rodadaParam : rodadaAtual;

        List<Jogo> jogos = palpiteService.buscarJogosRodada(rodada);
        List<Palpite> palpites = palpiteService.buscarPalpitesUsuarioRodada(usuarioLogado.getId(), rodada);

        // mapear palpites por ID do jogo para facilitar no template
        Map<Long, Palpite> mapPalpitesPorJogo = new HashMap<>();
        for (Palpite p : palpites) {
            mapPalpitesPorJogo.put(p.getJogo().getId(), p);
        }

        model.addAttribute("jogos", jogos);
        model.addAttribute("palpitesPorJogo", mapPalpitesPorJogo);
        model.addAttribute("rodada", rodada);
        model.addAttribute("hasAnterior", jogoRepository.existsByRodada(rodada - 1));
        model.addAttribute("hasProxima", jogoRepository.existsByRodada(rodada + 1));
        model.addAttribute("pagina", "meus-palpites");

        return "rodadas/meus-palpites";
    }

    @GetMapping("/palpite/jogo/{id}")
    public String abrirModalCriarPalpite(@PathVariable Long id, Model model) {

        Jogo jogo = rodadaService.buscarPorId(id);

        jogadorService.sincronizarAmbosTimes(jogo.getMandanteId(), jogo.getVisitanteId());

        List<Jogador> artilheiros = jogadorService.buscarArtilheirosDoTime(jogo.getMandanteId(), jogo.getVisitanteId());
        List<Jogador> goleiros = jogadorService.buscarGoleirosDosTimes(jogo.getMandanteId(), jogo.getVisitanteId());
        List<Jogador> tecnicos = jogadorService.buscarTecnicosDosTimes(jogo.getMandanteId(), jogo.getVisitanteId());

        model.addAttribute("jogo", jogo);
        model.addAttribute("artilheiros", artilheiros);
        model.addAttribute("goleiros", goleiros);
        model.addAttribute("tecnicos", tecnicos);

        return "modal-criar-palpite :: modalPalpite";
    }


    @PostMapping("/palpite/salvar")
    public String salvarPalpite(@ModelAttribute PalpiteRequest request,
                                @AuthenticationPrincipal Usuario usuario) {

        palpiteService.criarOuAtualizar(request, usuario);

        return "redirect:/rodadas?rodada=" + request.getRodada();
    }

}
