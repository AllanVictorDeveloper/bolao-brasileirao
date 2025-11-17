package com.bolao.brasileirao.services;

import com.bolao.brasileirao.dtos.JogadorApiResponse;
import com.bolao.brasileirao.entity.Jogador;
import com.bolao.brasileirao.enums.Posicao;
import com.bolao.brasileirao.repository.JogadorRepository;
import com.bolao.brasileirao.services.interfaces.IJogadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JogadorServiceImpl implements IJogadorService {

    @Autowired
    private JogadorRepository jogadorRepository;

    @Autowired
    private ApiFutebolService api;

    public List<Jogador> buscarElencoDosTimes(Long mandanteId, Long visitanteId) {
        sincronizarTimeSeNecessario(mandanteId);
        sincronizarTimeSeNecessario(visitanteId);

        return jogadorRepository.findByTimeIdIn(List.of(mandanteId, visitanteId));
    }

    @Override
    public void sincronizarAmbosTimes(Long mandanteId, Long visitanteId) {
        sincronizarTimeSeNecessario(mandanteId);
        sincronizarTimeSeNecessario(visitanteId);
    }

    // ================== SINCRONIZAR TIME ==================
    private void sincronizarTimeSeNecessario(Long timeId) {

        if (jogadorRepository.existsByTimeId(timeId))
            return; // já existe no banco → não sincroniza

        // --- Busca elenco ---
        List<JogadorApiResponse> elenco = api.buscarElenco(timeId);

        // --- Busca técnico ---
        JogadorApiResponse tecnico = api.buscarTecnico(timeId);

        // --- Converte e salva elenco
        List<Jogador> jogadores = elenco.stream()
                .map(this::mapearParaEntidade)
                .toList();

        // salva elenco
        jogadorRepository.saveAll(jogadores);

        // salva técnico separado
        if (tecnico != null) {
            Jogador t = mapearParaEntidade(tecnico);
            t.setPosicao(Posicao.TECNICO);
            jogadorRepository.save(t);
        }
    }




    private Jogador mapearParaEntidade(JogadorApiResponse j) {
        Jogador jogador = new Jogador();
        jogador.setId(j.getId());
        jogador.setNome(j.getNome());
        jogador.setTimeId(j.getTime_id());

        jogador.setPosicao(
                switch (j.getPosicao().toLowerCase()) {
                    case "goleiro" -> Posicao.GOLEIRO;
                    case "tecnico" -> Posicao.TECNICO;
                    case "atacante" -> Posicao.JOGADOR_LINHA;
                    default -> Posicao.JOGADOR_LINHA; // fallback
                }
        );

        return jogador;
    }

    @Override
    public List<Jogador> buscarArtilheirosDoTime(Long mandanteId, Long visitanteId) {
        return jogadorRepository.findByTimeIdInAndPosicao(
                List.of(mandanteId, visitanteId),
                Posicao.JOGADOR_LINHA
        );
    }

    @Override
    public List<Jogador> buscarGoleirosDosTimes(Long mandanteId, Long visitanteId) {
        return jogadorRepository.findByTimeIdInAndPosicao(
                List.of(mandanteId, visitanteId),
                Posicao.GOLEIRO
        );
    }

    @Override
    public List<Jogador> buscarTecnicosDosTimes(Long mandanteId, Long visitanteId) {

        return jogadorRepository.findByTimeIdInAndPosicao(
                List.of(mandanteId, visitanteId),
                Posicao.TECNICO
        );
    }
}