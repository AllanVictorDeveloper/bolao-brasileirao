package com.bolao.brasileirao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Palpites")
public class Palpite extends BaseEntity {

    @ManyToOne(optional = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    private Jogo jogo;

    private Integer golsCasaPalpite;
    private Integer golsForaPalpite;

    // IDs do jogador na API (artilheiro, goleiro, técnico)
    private Long artilheiroId;
    private Long paredaoId;
    private Long tecnicoId;

    private Integer pontos; // pontos calculados após a rodada


    public boolean cravouPlacar() {
        if (jogo.getPlacarMandante() == null || jogo.getPlacarVisitante() == null) return false;
        return golsCasaPalpite != null && golsForaPalpite != null
                && golsCasaPalpite.equals(jogo.getPlacarMandante())
                && golsForaPalpite.equals(jogo.getPlacarVisitante());
    }

    public boolean acertouVencedorOuEmpate() {
        if (jogo.getPlacarMandante() == null || jogo.getPlacarVisitante() == null) return false;

        int real = Integer.compare(jogo.getPlacarMandante(), jogo.getPlacarVisitante());
        int palpiteCmp = Integer.compare(golsCasaPalpite, golsForaPalpite);

        return real == palpiteCmp;
    }

    public Integer getRodada() {
        return jogo.getRodada();
    }

}
