package com.bolao.brasileirao.controller;

import com.bolao.brasileirao.dtos.JogoView;
import com.bolao.brasileirao.dtos.PalpiteRequest;
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

        Map<Long, Palpite> mapPalpitesPorJogo = new HashMap<>();
        for (Palpite p : palpites) {
            mapPalpitesPorJogo.put(p.getJogo().getId(), p);
        }

        List<JogoView> jogosView = jogos.stream().map(JogoView::new).toList();
        boolean podeCriarRodada = rodadaService.podeCriarPalpite(rodada, jogos);
        for (JogoView j : jogosView) {
            j.setPodeCriarPalpite(podeCriarRodada);
            if (mapPalpitesPorJogo.containsKey(j.getJogo().getId())) {
                j.setPodeVerPalpite(true);
            }
        }

        model.addAttribute("jogos", jogosView);
        model.addAttribute("palpitesPorJogo", mapPalpitesPorJogo);
        model.addAttribute("rodada", rodada);
        model.addAttribute("semJogos", jogos.isEmpty());
        model.addAttribute("hasAnterior", jogoRepository.existsByRodada(rodada - 1));
        model.addAttribute("hasProxima", jogoRepository.existsByRodada(rodada + 1));
        model.addAttribute("pagina", "meus-palpites");
        model.addAttribute("titulo", "Meus Palpites");

        return "rodadas";
    }

    @GetMapping("/palpite/jogo/{id}")
    public String abrirModalCriarPalpite(@PathVariable Long id, Model model) {

        Jogo jogo = rodadaService.buscarPorId(id);

        jogadorService.sincronizarPorPartida(jogo.getPartidaId(), jogo.getMandanteId(), jogo.getVisitanteId());

        model.addAttribute("jogo", jogo);
        model.addAttribute("artilheirosMandante", jogadorService.buscarArtilheirosPorTime(jogo.getMandanteId()));
        model.addAttribute("artilheirosVisitante", jogadorService.buscarArtilheirosPorTime(jogo.getVisitanteId()));
        model.addAttribute("goleirosMandante", jogadorService.buscarGoleirosPorTime(jogo.getMandanteId()));
        model.addAttribute("goleirosVisitante", jogadorService.buscarGoleirosPorTime(jogo.getVisitanteId()));
        model.addAttribute("tecnicosMandante", jogadorService.buscarTecnicoPorTime(jogo.getMandanteId()));
        model.addAttribute("tecnicosVisitante", jogadorService.buscarTecnicoPorTime(jogo.getVisitanteId()));

        return "modal-criar-palpite :: modalPalpite";
    }


    @GetMapping("/palpite/jogo/{id}/ver")
    public String verModalPalpite(@PathVariable Long id,
                                  @AuthenticationPrincipal Usuario usuario,
                                  Model model) {

        Jogo jogo = rodadaService.buscarPorId(id);
        Palpite palpite = palpiteService.buscarPalpite(usuario.getId(), id)
                .orElseThrow(() -> new RuntimeException("Palpite não encontrado"));

        model.addAttribute("jogo", jogo);
        model.addAttribute("palpite", palpite);
        model.addAttribute("nomeArtilheiro", jogadorService.buscarNomePorAtletaId(palpite.getArtilheiroId()));
        model.addAttribute("nomeParedao",    jogadorService.buscarNomePorAtletaId(palpite.getParedaoId()));
        model.addAttribute("nomeTecnico",    jogadorService.buscarNomePorAtletaId(palpite.getTecnicoId()));

        return "modal-ver-palpite :: modalVerPalpite";
    }

    @PostMapping("/palpite/salvar")
    public String salvarPalpite(@ModelAttribute PalpiteRequest request,
                                @AuthenticationPrincipal Usuario usuario) {

        palpiteService.criarOuAtualizar(request, usuario);

        return "redirect:/meus-palpites?rodada=" + request.getRodada();
    }

}
