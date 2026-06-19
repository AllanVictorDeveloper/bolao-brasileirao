package com.bolao.brasileirao.services.interfaces;

import com.bolao.brasileirao.entity.Jogador;

import java.util.List;

public interface IJogadorService {

    List<Jogador> buscarArtilheirosDoTime(Long mandanteId, Long visitanteId);

    List<Jogador> buscarGoleirosDosTimes(Long mandanteId, Long visitanteId);

    List<Jogador> buscarTecnicosDosTimes(Long mandanteId, Long visitanteId);

    List<Jogador> buscarArtilheirosPorTime(Long timeId);

    List<Jogador> buscarGoleirosPorTime(Long timeId);

    List<Jogador> buscarTecnicoPorTime(Long timeId);

    void sincronizarPorPartida(Long partidaId, Long mandanteId, Long visitanteId);

    String buscarNomePorAtletaId(Long atletaId);

    /** Varre todos os jogos importados e sincroniza jogadores a partir das escalações */
    int sincronizarTodosJogadores();
}
