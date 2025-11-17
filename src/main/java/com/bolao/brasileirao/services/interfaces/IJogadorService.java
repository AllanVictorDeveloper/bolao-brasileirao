package com.bolao.brasileirao.services.interfaces;

import com.bolao.brasileirao.entity.Jogador;

import java.util.List;

public interface IJogadorService {

    List<Jogador> buscarArtilheirosDoTime(Long mandanteId, Long visitanteId);

    List<Jogador> buscarGoleirosDosTimes(Long mandanteId, Long visitanteId);

    List<Jogador> buscarTecnicosDosTimes(Long mandanteId, Long visitanteId);

    void sincronizarAmbosTimes(Long mandanteId, Long visitanteId);
}
