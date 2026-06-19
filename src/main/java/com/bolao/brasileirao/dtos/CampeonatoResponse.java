package com.bolao.brasileirao.dtos;

import lombok.Data;

@Data
public class CampeonatoResponse {

    private Integer campeonato_id;
    private String nome;
    private String slug;
    private String nome_popular;
    private String status;
    private String tipo;
    private String logo;
    private RodadaAtual rodada_atual;
    private EdicaoAtual edicao_atual;

    @Data
    public static class RodadaAtual {
        private String nome;
        private String slug;
        private Integer rodada;
        private String status;
    }

    @Data
    public static class EdicaoAtual {
        private Integer edicao_id;
        private String temporada;
        private String nome;
        private String nome_popular;
        private String slug;
    }
}
