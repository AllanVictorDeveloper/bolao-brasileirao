package com.bolao.brasileirao.services;

import com.bolao.brasileirao.entity.EstatisticaJogadorRodada;
import com.bolao.brasileirao.entity.Jogador;
import com.bolao.brasileirao.enums.Posicao;
import com.bolao.brasileirao.repository.EstatisticaJogadorRodadaRepository;
import com.bolao.brasileirao.repository.JogadorRepository;
import com.bolao.brasileirao.services.interfaces.IEstatisticasService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class EstatisticasServiceImpl implements IEstatisticasService {

    private final EstatisticaJogadorRodadaRepository estatisticasRepo;
    private final JogadorRepository jogadorRepository;

    public EstatisticasServiceImpl(EstatisticaJogadorRodadaRepository estatisticasRepo,
                                   JogadorRepository jogadorRepository) {
        this.estatisticasRepo = estatisticasRepo;
        this.jogadorRepository = jogadorRepository;
    }

    @Override
    public EstatisticaJogadorRodada buscarPorJogadorERodada(Long jogadorId, Integer rodada) {
        return estatisticasRepo.findByJogadorIdAndRodada(jogadorId, rodada).orElse(null);
    }

    /**
     * Se o goleiro titular não jogou, busca outro goleiro do mesmo time que entrou em campo.
     * Caso do goleiro reserva que entrou por lesão/expulsão do titular.
     */
    @Override
    public EstatisticaJogadorRodada buscarReservaGoleiroDoMesmoTime(Long goleiroTitularId, Integer rodada) {
        Jogador titular = jogadorRepository.findById(goleiroTitularId).orElse(null);
        if (titular == null) return null;

        List<Long> outrosGoleirosIds = jogadorRepository
                .findByTimeIdAndPosicao(titular.getTimeId(), Posicao.GOLEIRO)
                .stream()
                .map(Jogador::getId)
                .filter(id -> !id.equals(goleiroTitularId))
                .toList();

        if (outrosGoleirosIds.isEmpty()) return null;

        return estatisticasRepo
                .findByJogadorIdInAndRodadaAndJogouTrue(outrosGoleirosIds, rodada)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Se o artilheiro escalado não jogou, busca o substituto do mesmo time que entrou em campo.
     * Prioriza quem mais participou (gols + assistências) como critério de relevância.
     */
    @Override
    public EstatisticaJogadorRodada buscarReservaArtilheiroDoMesmoTime(Long artilheiroTitularId, Integer rodada) {
        Jogador titular = jogadorRepository.findById(artilheiroTitularId).orElse(null);
        if (titular == null) return null;

        List<Long> outrosJogadoresIds = jogadorRepository
                .findByTimeIdAndPosicao(titular.getTimeId(), Posicao.JOGADOR_LINHA)
                .stream()
                .map(Jogador::getId)
                .filter(id -> !id.equals(artilheiroTitularId))
                .toList();

        if (outrosJogadoresIds.isEmpty()) return null;

        return estatisticasRepo
                .findByJogadorIdInAndRodadaAndJogouTrue(outrosJogadoresIds, rodada)
                .stream()
                .max(Comparator.comparingInt(s ->
                        nvl(s.getGols()) * 10 + nvl(s.getAssistencias()) * 5))
                .orElse(null);
    }

    private int nvl(Integer v) {
        return v == null ? 0 : v;
    }
}
