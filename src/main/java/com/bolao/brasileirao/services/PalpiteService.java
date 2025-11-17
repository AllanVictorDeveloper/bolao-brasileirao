package com.bolao.brasileirao.services;

import com.bolao.brasileirao.dtos.PalpiteRequest;
import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.entity.Palpite;
import com.bolao.brasileirao.entity.Usuario;
import com.bolao.brasileirao.repository.JogoRepository;
import com.bolao.brasileirao.repository.PalpiteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PalpiteService {

    private final PalpiteRepository palpiteRepository;
    private final JogoRepository jogoRepository;
    private final PontuacaoService pontuacaoService;

    public PalpiteService(PalpiteRepository palpiteRepository,
                          JogoRepository jogoRepository,
                          PontuacaoService pontuacaoService) {
        this.palpiteRepository = palpiteRepository;
        this.jogoRepository = jogoRepository;
        this.pontuacaoService = pontuacaoService;
    }

    public List<Palpite> buscarPalpitesUsuarioRodada(Long usuarioId, Integer rodada) {
        return palpiteRepository.findByUsuarioIdAndJogo_Rodada(usuarioId, rodada);
    }

    public List<Jogo> buscarJogosRodada(Integer rodada) {
        return jogoRepository.findByRodadaOrderByDataJogoAsc(rodada);
    }

    @Transactional
    public void recalcularPontuacaoRodada(Integer rodada) {
        List<Palpite> palpites = palpiteRepository.findByJogo_Rodada(rodada);
        for (Palpite palpite : palpites) {
            int pontos = pontuacaoService.calcularPontuacao(palpite);
            palpite.setPontos(pontos);
        }
        palpiteRepository.saveAll(palpites);
    }


    public void criarOuAtualizar(PalpiteRequest req, Usuario usuario) {

        Jogo jogo = jogoRepository.findById(req.getJogoId())
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

        Optional<Palpite> existente =
                palpiteRepository.findByUsuarioIdAndJogoId(usuario.getId(), req.getJogoId());

        Palpite palpite = existente.orElse(new Palpite());
        palpite.setUsuario(usuario);
        palpite.setJogo(jogo);
//        palpite.setRodada(jogo.getRodada());
//
//        palpite.setPlacarMandante(req.getPlacarMandante());
//        palpite.setPlacarVisitante(req.getPlacarVisitante());
        palpite.setArtilheiroId(req.getArtilheiroId());
        palpite.setParedaoId(req.getParedaoId());
        palpite.setTecnicoId(req.getTecnicoId());

        palpiteRepository.save(palpite);
    }

}
