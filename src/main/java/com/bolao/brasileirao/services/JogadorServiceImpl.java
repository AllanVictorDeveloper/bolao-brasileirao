package com.bolao.brasileirao.services;

import com.bolao.brasileirao.dtos.PartidaDetalheResponse;
import com.bolao.brasileirao.dtos.PartidaDetalheResponse.EscalacaoTime;
import com.bolao.brasileirao.dtos.PartidaDetalheResponse.JogadorEscalado;
import com.bolao.brasileirao.entity.Jogador;
import com.bolao.brasileirao.enums.Posicao;
import com.bolao.brasileirao.repository.JogadorRepository;
import com.bolao.brasileirao.services.interfaces.IJogadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JogadorServiceImpl implements IJogadorService {

    private final JogadorRepository jogadorRepository;
    private final ApiFutebolService apiFutebolService;

    /**
     * Sincroniza titulares, reservas e técnico de ambos os times
     * a partir da escalação retornada pelo endpoint GET /partidas/{partidaId}.
     * Se a escalação ainda não estiver disponível (jogo não iniciado), mantém
     * os jogadores que já existem no banco para esses times.
     */
    @Override
    public void sincronizarPorPartida(Long partidaId, Long mandanteId, Long visitanteId) {
        PartidaDetalheResponse detalhe = apiFutebolService.buscarDetalhesPartida(partidaId);

        if (detalhe == null || detalhe.getEscalacoes() == null) return;

        if (detalhe.getEscalacoes().getMandante() != null) {
            sincronizarEscalacao(detalhe.getEscalacoes().getMandante(), mandanteId);
        }
        if (detalhe.getEscalacoes().getVisitante() != null) {
            sincronizarEscalacao(detalhe.getEscalacoes().getVisitante(), visitanteId);
        }
    }

    private void sincronizarEscalacao(EscalacaoTime escalacao, Long timeId) {
        List<Jogador> jogadores = new ArrayList<>();

        if (escalacao.getTitulares() != null) {
            for (JogadorEscalado j : escalacao.getTitulares()) {
                if (j.getAtleta() == null) continue;
                jogadores.add(mapear(j.getAtleta().getAtleta_id(),
                        j.getAtleta().getNome_popular(), timeId, posicaoDe(j)));
            }
        }

        if (escalacao.getReservas() != null) {
            for (JogadorEscalado j : escalacao.getReservas()) {
                if (j.getAtleta() == null) continue;
                jogadores.add(mapear(j.getAtleta().getAtleta_id(),
                        j.getAtleta().getNome_popular(), timeId, posicaoDe(j)));
            }
        }

        if (escalacao.getTecnico() != null && escalacao.getTecnico().getTecnico_id() != null) {
            jogadores.add(mapear(escalacao.getTecnico().getTecnico_id(),
                    escalacao.getTecnico().getNome_popular(), timeId, Posicao.TECNICO));
        }

        jogadorRepository.saveAll(jogadores);
    }

    private Jogador mapear(Long id, String nome, Long timeId, Posicao posicao) {
        Jogador j = jogadorRepository.findById(id).orElse(new Jogador());
        j.setId(id);
        j.setNome(nome);
        j.setTimeId(timeId);
        j.setPosicao(posicao);
        if (j.getCriadoPor() == null) j.setCriadoPor("sistema");
        return j;
    }

    private Posicao posicaoDe(JogadorEscalado j) {
        return j.isGoleiro() ? Posicao.GOLEIRO : Posicao.JOGADOR_LINHA;
    }

    @Override
    public List<Jogador> buscarArtilheirosDoTime(Long mandanteId, Long visitanteId) {
        return jogadorRepository.findByTimeIdInAndPosicao(
                List.of(mandanteId, visitanteId), Posicao.JOGADOR_LINHA);
    }

    @Override
    public List<Jogador> buscarGoleirosDosTimes(Long mandanteId, Long visitanteId) {
        return jogadorRepository.findByTimeIdInAndPosicao(
                List.of(mandanteId, visitanteId), Posicao.GOLEIRO);
    }

    @Override
    public List<Jogador> buscarTecnicosDosTimes(Long mandanteId, Long visitanteId) {
        return jogadorRepository.findByTimeIdInAndPosicao(
                List.of(mandanteId, visitanteId), Posicao.TECNICO);
    }
}
