package com.bolao.brasileirao.repository;

import com.bolao.brasileirao.entity.EstatisticaJogadorRodada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstatisticaJogadorRodadaRepository extends JpaRepository<EstatisticaJogadorRodada, Long> {
    Optional<EstatisticaJogadorRodada> findByJogadorIdAndRodada(Long jogadorId, Integer rodada);
    List<EstatisticaJogadorRodada> findByRodada(Integer rodada);
    List<EstatisticaJogadorRodada> findByJogadorIdInAndRodadaAndJogouTrue(List<Long> jogadorIds, Integer rodada);
    boolean existsByJogadorIdAndRodada(Long jogadorId, Integer rodada);
}