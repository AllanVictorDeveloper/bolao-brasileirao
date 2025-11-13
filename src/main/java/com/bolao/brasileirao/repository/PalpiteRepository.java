package com.bolao.brasileirao.repository;

import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.entity.Palpite;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface PalpiteRepository extends JpaRepository<Palpite, Long> {
    List<Palpite> findByJogo(Jogo j);
}