package com.bolao.brasileirao.repository;

import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.entity.Palpite;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface PalpiteRepository extends JpaRepository<Palpite, Long> {
    List<Palpite> findByJogo(Jogo j);

    @Query("SELECT p FROM Palpite p JOIN FETCH p.jogo j WHERE p.usuario.id = :usuarioId AND j.rodada = :rodada")
    List<Palpite> findByUsuarioIdAndJogo_Rodada(@Param("usuarioId") Long usuarioId, @Param("rodada") Integer rodada);

    @Query("SELECT p FROM Palpite p JOIN FETCH p.jogo j WHERE j.rodada = :rodada")
    List<Palpite> findByJogo_Rodada(@Param("rodada") Integer rodada);

    Optional<Palpite> findByUsuarioIdAndJogoId(Long id, Long jogoId);
}