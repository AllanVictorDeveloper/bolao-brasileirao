package com.bolao.brasileirao.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public class PartidaDetalheResponse {

    private Long partida_id;
    private String status;
    private Integer placar_mandante;
    private Integer placar_visitante;
    private Escalacoes escalacoes;
    private Gols gols;
    private Substituicoes substituicoes;
    private Cartoes cartoes;

    // ── Escalações ──────────────────────────────────────────────────

    @Data
    public static class Escalacoes {
        private EscalacaoTime mandante;
        private EscalacaoTime visitante;
    }

    @Data
    public static class EscalacaoTime {
        private Tecnico tecnico;
        private List<JogadorEscalado> titulares;
        private List<JogadorEscalado> reservas;
    }

    @Data
    public static class Tecnico {
        private Long tecnico_id;
        private String nome_popular;
    }

    @Data
    public static class JogadorEscalado {
        private Atleta atleta;
        private String camisa;
        private Integer ordem;

        /**
         * A API retorna {"nome":"Goleiro","sigla":"GOL"} OU [] (array vazio).
         * Usamos JsonNode para tratar ambos sem erro de desserialização.
         */
        @JsonProperty("posicao")
        private JsonNode posicao;

        public boolean isGoleiro() {
            if (posicao == null || posicao.isArray()) return false;
            JsonNode sigla = posicao.get("sigla");
            return sigla != null && "GOL".equalsIgnoreCase(sigla.asText());
        }
    }

    // ── Referência de atleta (usada em gols, subs, cartões) ─────────

    @Data
    public static class Atleta {
        private Long atleta_id;
        private String nome_popular;
    }

    // ── Gols ────────────────────────────────────────────────────────

    @Data
    public static class Gols {
        private List<Gol> mandante;
        private List<Gol> visitante;
    }

    @Data
    public static class Gol {
        private Atleta atleta;
        private String minuto;
        private String periodo;
        private boolean penalti;
        private boolean gol_contra;
    }

    // ── Substituições ────────────────────────────────────────────────

    @Data
    public static class Substituicoes {
        private List<Substituicao> mandante;
        private List<Substituicao> visitante;
    }

    @Data
    public static class Substituicao {
        private Atleta saiu;
        private Atleta entrou;
        private String periodo;
        private String minuto;
    }

    // ── Cartões ──────────────────────────────────────────────────────

    @Data
    public static class Cartoes {
        private CartoesPorTime amarelo;
        private CartoesPorTime vermelho;
    }

    @Data
    public static class CartoesPorTime {
        private List<Cartao> mandante;
        private List<Cartao> visitante;
    }

    @Data
    public static class Cartao {
        private Long cartao_id;
        private Atleta atleta; // pode ser null (cartão para o banco/comissão)
        private String minuto;
        private String periodo;
    }
}
