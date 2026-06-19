package com.bolao.brasileirao.dtos;

import lombok.Data;
import java.util.List;

@Data
public class TabelaItemResponse {

    private Integer posicao;
    private Integer pontos;
    private TimeInfo time;
    private Integer jogos;
    private Integer vitorias;
    private Integer empates;
    private Integer derrotas;
    private Integer gols_pro;
    private Integer gols_contra;
    private Integer saldo_gols;
    private Integer aproveitamento;
    private Integer variacao_posicao;
    private List<String> ultimos_jogos;
    private String faixa_classificacao;

    @Data
    public static class TimeInfo {
        private Long time_id;
        private String nome_popular;
        private String sigla;
        private String escudo;
    }
}
