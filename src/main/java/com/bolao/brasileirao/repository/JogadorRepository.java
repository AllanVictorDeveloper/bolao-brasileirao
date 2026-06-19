package com.bolao.brasileirao.repository;

import com.bolao.brasileirao.entity.Jogador;
import com.bolao.brasileirao.enums.Posicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JogadorRepository extends JpaRepository<Jogador, Long> {

    List<Jogador> findByTimeId(Long timeId);

    List<Jogador> findByTimeIdIn(List<Long> ids);

    List<Jogador> findByTimeIdInAndPosicao(List<Long> ids, Posicao posicao);

    boolean existsByTimeId(Long timeId);
    List<Jogador> findByTimeIdAndPosicao(Long timeId, Posicao posicao);
}
