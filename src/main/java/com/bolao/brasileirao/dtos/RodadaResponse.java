package com.bolao.brasileirao.dtos;

import lombok.Data;
import java.util.List;

@Data
public class RodadaResponse {

    private String nome;
    private String slug;
    private Integer rodada;
    private String status;

    private RodadaInfo proxima_rodada;
    private RodadaInfo rodada_anterior;

    private List<Partida> partidas;

    @Data
    public static class RodadaInfo {
        private String nome;
        private String slug;
        private Integer rodada;
        private String status;
    }

    @Data
    public static class Partida {
        private Long partida_id;

        private Campeonato campeonato;

        private String placar;

        private Time time_mandante;
        private Time time_visitante;

        private Integer placar_mandante;
        private Integer placar_visitante;

        private Boolean disputa_penalti;

        private String status;
        private String slug;

        private String data_realizacao;
        private String hora_realizacao;
        private String data_realizacao_iso;

        private Estadio estadio;

        private String _link;
    }

    @Data
    public static class Campeonato {
        private Long campeonato_id;
        private String nome;
        private String slug;
    }

    @Data
    public static class Time {
        private Long time_id;
        private String nome_popular;
        private String sigla;
        private String escudo;
    }

    @Data
    public static class Estadio {
        private Long estadio_id;
        private String nome_popular;
    }


}
