package com.bolao.brasileirao.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ArtilhariaItemResponse {

    private AtletaInfo atleta;
    private TimeInfo time;
    private Integer gols;

    @Data
    public static class AtletaInfo {
        private Long atleta_id;
        private String nome_popular;
        /** Pode ser [] (array vazio) ou {nome, sigla} */
        private JsonNode posicao;
    }

    @Data
    public static class TimeInfo {
        private Long time_id;
        private String nome_popular;
        private String sigla;
        private String escudo;
    }
}
