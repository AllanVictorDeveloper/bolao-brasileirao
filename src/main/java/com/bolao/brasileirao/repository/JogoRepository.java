package com.bolao.brasileirao.repository;

import com.bolao.brasileirao.entity.Jogo;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface JogoRepository extends JpaRepository<Jogo, Long> {
    List<Jogo> findByRodada(Integer rodada);

    boolean existsById(Long id);

    List<Jogo> findByRodadaOrderByDataJogoAsc(Integer rodada);

    @Query("SELECT MAX(j.rodada) FROM Jogo j")
    Integer findRodadaMaisAtual();

    boolean existsByRodada(Integer rodada);

    Optional<Jogo> findJogoById(Long id);

    List<Jogo> findByStatusAndStatsImportadasFalse(com.bolao.brasileirao.entity.StatusJogo status);
}