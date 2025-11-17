package com.bolao.brasileirao.repository;

import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.entity.Palpite;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface PalpiteRepository extends JpaRepository<Palpite, Long> {
    List<Palpite> findByJogo(Jogo j);

    List<Palpite> findByUsuarioIdAndJogo_Rodada(Long usuarioId, Integer rodada);

    List<Palpite> findByJogo_Rodada(Integer rodada);

    Optional<Palpite> findByUsuarioIdAndJogoId(Long id, Long jogoId);
}