package com.bolao.brasileirao.services.mapper;

import com.bolao.brasileirao.dtos.JogadorApiResponse;
import com.bolao.brasileirao.dtos.RodadaResponse;
import com.bolao.brasileirao.entity.Jogador;
import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.entity.StatusJogo;
import com.bolao.brasileirao.enums.Posicao;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JogadorMapper {

    private static final DateTimeFormatter ISO_FUTEBOL =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    private Jogador mapearParaEntidade(JogadorApiResponse j) {
        Jogador jogador = new Jogador();
        jogador.setId(j.getId());
        jogador.setNome(j.getNome());
        jogador.setTimeId(j.getTime_id()); // <-- AJUSTE AQUI

        jogador.setPosicao(
                switch (j.getPosicao().toLowerCase()) {
                    case "goleiro" -> Posicao.GOLEIRO;
                    case "tecnico" -> Posicao.TECNICO;
                    case "atacante" -> Posicao.JOGADOR_LINHA;
                    case "meia" -> Posicao.JOGADOR_LINHA;
                    case "zagueiro" -> Posicao.JOGADOR_LINHA;
                    default -> Posicao.JOGADOR_LINHA;
                }
        );

        return jogador;
    }

    private StatusJogo convertStatus(String status) {

        if (status == null) return StatusJogo.DESCONHECIDO;

        return switch (status.toLowerCase()) {
            case "agendado" -> StatusJogo.AGENDADO;
            case "andamento", "intervalo" -> StatusJogo.ANDAMENTO;
            case "finalizado" -> StatusJogo.FINALIZADO;
            default -> StatusJogo.DESCONHECIDO;
        };
    }
}
