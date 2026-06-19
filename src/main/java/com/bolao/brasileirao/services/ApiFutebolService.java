package com.bolao.brasileirao.services;

import com.bolao.brasileirao.dtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiFutebolService {

    private static final int BRASILEIRAO_ID = 10;

    private final RestTemplate apiFutebolClient;
    private final String apiFutebolBaseUrl;

    // ── Campeonato ──────────────────────────────────────────────────────

    public CampeonatoResponse buscarCampeonato(int campeonatoId) {
        return apiFutebolClient.getForObject(
                apiFutebolBaseUrl + "/campeonatos/" + campeonatoId,
                CampeonatoResponse.class
        );
    }

    public CampeonatoResponse buscarCampeonatoBrasileiro() {
        return buscarCampeonato(BRASILEIRAO_ID);
    }

    // ── Tabela ──────────────────────────────────────────────────────────

    public List<TabelaItemResponse> buscarTabela(int campeonatoId) {
        return apiFutebolClient.exchange(
                apiFutebolBaseUrl + "/campeonatos/" + campeonatoId + "/tabela",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TabelaItemResponse>>() {}
        ).getBody();
    }

    public List<TabelaItemResponse> buscarTabelaBrasileiro() {
        return buscarTabela(BRASILEIRAO_ID);
    }

    // ── Artilharia ──────────────────────────────────────────────────────

    public List<ArtilhariaItemResponse> buscarArtilharia(int campeonatoId) {
        return apiFutebolClient.exchange(
                apiFutebolBaseUrl + "/campeonatos/" + campeonatoId + "/artilharia",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ArtilhariaItemResponse>>() {}
        ).getBody();
    }

    public List<ArtilhariaItemResponse> buscarArtilhariaBrasileiro() {
        return buscarArtilharia(BRASILEIRAO_ID);
    }

    // ── Rodadas ─────────────────────────────────────────────────────────

    public List<RodadaResponse> buscarTodasRodadas(int campeonatoId) {
        return apiFutebolClient.exchange(
                apiFutebolBaseUrl + "/campeonatos/" + campeonatoId + "/rodadas",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RodadaResponse>>() {}
        ).getBody();
    }

    public RodadaResponse buscarRodada(int campeonatoId, int rodada) {
        return apiFutebolClient.getForObject(
                apiFutebolBaseUrl + "/campeonatos/" + campeonatoId + "/rodadas/" + rodada,
                RodadaResponse.class
        );
    }

    // ── Partidas ─────────────────────────────────────────────────────────

    public PartidaDetalheResponse buscarDetalhesPartida(Long partidaId) {
        return apiFutebolClient.getForObject(
                apiFutebolBaseUrl + "/partidas/" + partidaId,
                PartidaDetalheResponse.class
        );
    }

    public List<PartidaDetalheResponse> buscarPartidasAoVivo() {
        return apiFutebolClient.exchange(
                apiFutebolBaseUrl + "/ao-vivo",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PartidaDetalheResponse>>() {}
        ).getBody();
    }
}
