package com.bolao.brasileirao.services.interfaces;

import com.bolao.brasileirao.entity.EstatisticaJogadorRodada;

public interface IEstatisticasService {

    EstatisticaJogadorRodada buscarPorJogadorERodada(Long jogadorId, Integer rodada);

    /**
     * Busca estatística do goleiro reserva caso o titular não jogue.
     */
    EstatisticaJogadorRodada buscarReservaGoleiroDoMesmoTime(Long goleiroTitularId, Integer rodada);

}
