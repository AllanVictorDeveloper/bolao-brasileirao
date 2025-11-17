package com.bolao.brasileirao.dtos;

import lombok.Data;

@Data
public class JogadorApiResponse {
    private Long id;
    private String nome;
    private String posicao; // goleiro, tecnico, atacante, meia, defensor
    private Long time_id;
}

