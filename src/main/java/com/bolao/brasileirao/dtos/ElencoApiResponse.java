package com.bolao.brasileirao.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ElencoApiResponse {
    private List<JogadorApiResponse> jogadores;
}
