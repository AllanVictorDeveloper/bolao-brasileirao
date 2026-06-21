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
import java.util.Set;
import java.util.stream.Collectors;

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

    @Transactional
    public int recalcularTodasRodadas() {
        List<Palpite> todos = palpiteRepository.findAll();
        Set<Integer> rodadas = todos.stream()
                .map(Palpite::getRodada)
                .collect(Collectors.toSet());
        for (Integer rodada : rodadas) {
            recalcularPontuacaoRodada(rodada);
        }
        return rodadas.size();
    }


    public Optional<Palpite> buscarPalpite(Long usuarioId, Long jogoId) {
        return palpiteRepository.findByUsuarioIdAndJogoId(usuarioId, jogoId);
    }

    public void criarOuAtualizar(PalpiteRequest req, Usuario usuario) {

        Jogo jogo = jogoRepository.findById(req.getJogoId())
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

        validarJogadoresDuplicados(req);

        Optional<Palpite> existente =
                palpiteRepository.findByUsuarioIdAndJogoId(usuario.getId(), req.getJogoId());

        Palpite palpite = existente.orElse(new Palpite());
        palpite.setUsuario(usuario);
        palpite.setJogo(jogo);
        palpite.setGolsCasaPalpite(req.getPlacarMandante());
        palpite.setGolsForaPalpite(req.getPlacarVisitante());
        palpite.setArtilheiroId(req.getArtilheiroId());
        palpite.setArtilheiro2Id(req.getArtilheiro2Id());
        palpite.setParedaoId(req.getParedaoId());
        palpite.setTecnicoId(req.getTecnicoId());
        if (palpite.getCriadoPor() == null) palpite.setCriadoPor(usuario.getUsername());

        palpiteRepository.save(palpite);
    }

    private void validarJogadoresDuplicados(PalpiteRequest req) {
        Long art1    = req.getArtilheiroId();
        Long art2    = req.getArtilheiro2Id();
        Long paredao = req.getParedaoId();
        Long tecnico = req.getTecnicoId();

        if (art1 != null && art1.equals(art2)) {
            throw new IllegalArgumentException("Os dois artilheiros não podem ser o mesmo jogador.");
        }
        if (art1 != null && art1.equals(tecnico)) {
            throw new IllegalArgumentException("O artilheiro 1 não pode ser o mesmo que o técnico.");
        }
        if (art2 != null && art2.equals(tecnico)) {
            throw new IllegalArgumentException("O artilheiro 2 não pode ser o mesmo que o técnico.");
        }
        if (paredao != null && (paredao.equals(art1) || paredao.equals(art2))) {
            throw new IllegalArgumentException("O goleiro não pode ser selecionado como artilheiro.");
        }
        if (paredao != null && paredao.equals(tecnico)) {
            throw new IllegalArgumentException("O goleiro não pode ser o mesmo que o técnico.");
        }
    }

}
