package com.bolao.brasileirao.services.interfaces;

import com.bolao.brasileirao.entity.EstatisticaJogadorRodada;

public interface IEstatisticasService {

    EstatisticaJogadorRodada buscarPorJogadorERodada(Long jogadorId, Integer rodada);

    EstatisticaJogadorRodada buscarReservaGoleiroDoMesmoTime(Long goleiroTitularId, Integer rodada);

    EstatisticaJogadorRodada buscarReservaArtilheiroDoMesmoTime(Long artilheiroTitularId, Integer rodada);

}
