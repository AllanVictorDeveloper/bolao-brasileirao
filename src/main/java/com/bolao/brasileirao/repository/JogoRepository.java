package com.bolao.brasileirao.repository;

import com.bolao.brasileirao.entity.Jogo;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface JogoRepository extends JpaRepository<Jogo, Long> {
    List<Jogo> findByRodada(Integer r);

    boolean existsByApiId(Long id);
}