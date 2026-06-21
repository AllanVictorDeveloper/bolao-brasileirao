package com.bolao.brasileirao.dtos;

import lombok.Data;

@Data
public class PalpiteRequest {

    private Long jogoId;

    private Integer rodada;

    private Integer placarMandante;

    private Integer placarVisitante;

    private Long artilheiroId;

    private Long artilheiro2Id;

    private Long paredaoId;

    private Long tecnicoId;

}
