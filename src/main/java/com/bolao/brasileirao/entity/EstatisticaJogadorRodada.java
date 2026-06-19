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

    private Integer gols;
    private Integer golsSofridos;
    private boolean jogou;
    private boolean levouVermelho;
    private boolean timeVenceu;

    // Campos abaixo não são fornecidos pela API-Futebol — ficam null até integração manual ou outra fonte
    private Integer assistencias;
    private Integer penaltisDefendidos;
    private Integer penaltisPerdidos;

}
