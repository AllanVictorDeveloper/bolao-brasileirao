package com.bolao.brasileirao.services;

import com.bolao.brasileirao.entity.EstatisticaJogadorRodada;
import com.bolao.brasileirao.repository.EstatisticaJogadorRodadaRepository;
import com.bolao.brasileirao.services.interfaces.IEstatisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstatisticasServiceImpl implements IEstatisticasService {

    @Autowired
    private EstatisticaJogadorRodadaRepository estatisticasRepo;

    @Override
    public EstatisticaJogadorRodada buscarPorJogadorERodada(Long jogadorId, Integer rodada) {
        return estatisticasRepo.findByJogadorIdAndRodada(jogadorId, rodada).orElse(null);
    }

    @Override
    public EstatisticaJogadorRodada buscarReservaGoleiroDoMesmoTime(Long goleiroTitularId, Integer rodada) {
        // Aqui você implementa sua regra real — por enquanto devolvo null
        return null;
    }
}
