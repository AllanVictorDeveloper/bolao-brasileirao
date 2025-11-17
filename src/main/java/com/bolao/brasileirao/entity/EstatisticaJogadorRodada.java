package com.bolao.brasileirao.entity;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstatisticaJogadorRodada extends BaseEntity{


    private Long jogadorId;
    private Integer rodada;

    private Integer gols;          // gols marcados
    private Integer golsSofridos;  // para goleiros
    private boolean jogou;
    private boolean levouVermelho;
    private boolean timeVenceu;    // útil para técnico

}
