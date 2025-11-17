package com.bolao.brasileirao.services;

import com.bolao.brasileirao.entity.EstatisticaJogadorRodada;
import com.bolao.brasileirao.entity.Palpite;
import com.bolao.brasileirao.services.interfaces.IEstatisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PontuacaoService  {

    @Autowired
    private IEstatisticasService IEstatisticasService;


    public int calcularPontuacao(Palpite palpite) {

        int pontos = 0;
        Integer rodada = palpite.getRodada();

        // 1) Cravar o placar → 10 pontos
        if (palpite.cravouPlacar()) {
            pontos += 10;
        }

        // 2) Acertar vencedor ou empate → 5 pontos
        if (palpite.acertouVencedorOuEmpate()) {
            pontos += 5;
        }

        // 3) Artilheiro
        if (palpite.getArtilheiroId() != null) {
            pontos += calcularArtilheiro(palpite.getArtilheiroId(), rodada);
        }

        // 4) Paredão
        pontos += calcularParedao(palpite.getParedaoId(), rodada);

        // 5) Técnico
        if (palpite.getTecnicoId() != null) {
            pontos += calcularTecnico(palpite.getTecnicoId(), rodada);
        }

        return pontos;
    }

    private int calcularArtilheiro(Long artilheiroId, Integer rodada) {
        int pontos = 0;

        EstatisticaJogadorRodada stats = IEstatisticasService.buscarPorJogadorERodada(artilheiroId, rodada);
        if (stats == null || !stats.isJogou()) {
            // não jogou → pontuação nula
            return 0;
        }

        // 10 pontos por gol
        pontos += (stats.getGols() != null ? stats.getGols() * 10 : 0);

        // vermelho → -10
        if (stats.isLevouVermelho()) {
            pontos -= 10;
        }

        return pontos;
    }

    private int calcularParedao(Long paredaoId, Integer rodada) {

        // não escalou paredão → -20
        if (paredaoId == null) {
            return -20;
        }

        EstatisticaJogadorRodada gkStats =
                IEstatisticasService.buscarPorJogadorERodada(paredaoId, rodada);

        // se não jogou, pega reserva automático
        if (gkStats == null || !gkStats.isJogou()) {
            gkStats = IEstatisticasService.buscarReservaGoleiroDoMesmoTime(paredaoId, rodada);
        }

        int pontos = 0;

        if (gkStats != null) {
            int golsSofridos = gkStats.getGolsSofridos() != null ? gkStats.getGolsSofridos() : 0;

            if (golsSofridos == 0) {
                // não sofreu gol → +10
                pontos += 10;
            } else {
                // sofreu gol (1 ou mais) → -10
                pontos -= 10;
            }

            // vermelho → -10
            if (gkStats.isLevouVermelho()) {
                pontos -= 10;
            }
        }

        return pontos;
    }

    private int calcularTecnico(Long tecnicoId, Integer rodada) {

        EstatisticaJogadorRodada stats =
                IEstatisticasService.buscarPorJogadorERodada(tecnicoId, rodada);

        if (stats == null || !stats.isJogou()) {
            return 0;
        }

        int pontos = 0;

        // vitória do time → +10
        if (stats.isTimeVenceu()) {
            pontos += 10;
        }

        // vermelho → -10
        if (stats.isLevouVermelho()) {
            pontos -= 10;
        }

        return pontos;
    }

}