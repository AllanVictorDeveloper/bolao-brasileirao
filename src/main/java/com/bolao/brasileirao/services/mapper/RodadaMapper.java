package com.bolao.brasileirao.services.mapper;

import com.bolao.brasileirao.dtos.RodadaResponse;
import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.entity.StatusJogo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RodadaMapper {

    private static final DateTimeFormatter ISO_FUTEBOL =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    public Jogo converterParaJogo(RodadaResponse.Partida p, Integer rodada) {

        Jogo jogo = new Jogo();
        jogo.setRodada(rodada);


        if (p.getTime_mandante() != null) {
            jogo.setMandante(p.getTime_mandante().getNome_popular());
            jogo.setMandanteSigla(p.getTime_mandante().getSigla());
            jogo.setMandanteEscudo(p.getTime_mandante().getEscudo());
        }

        if (p.getTime_visitante() != null) {
            jogo.setVisitante(p.getTime_visitante().getNome_popular());
            jogo.setVisitanteSigla(p.getTime_visitante().getSigla());
            jogo.setVisitanteEscudo(p.getTime_visitante().getEscudo());
        }

        if (p.getEstadio() != null) {
            jogo.setEstadio(p.getEstadio().getNome_popular());
        } else {
            jogo.setEstadio("A definir");
        }


        if (p.getData_realizacao_iso() != null) {
            try {
                jogo.setDataJogo(LocalDateTime.parse(p.getData_realizacao_iso(), ISO_FUTEBOL));
            } catch (Exception e) {
                System.out.println("⚠ Erro ao converter data: " + p.getData_realizacao_iso());
                jogo.setDataJogo(null);
            }
        }


        jogo.setPlacarMandante(p.getPlacar_mandante());
        jogo.setPlacarVisitante(p.getPlacar_visitante());

        jogo.setStatus(convertStatus(p.getStatus()));

        jogo.setCriadoPor("sistema");

        return jogo;
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
