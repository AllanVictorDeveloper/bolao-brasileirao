package com.bolao.brasileirao.services;

import com.bolao.brasileirao.dtos.ElencoApiResponse;
import com.bolao.brasileirao.dtos.JogadorApiResponse;
import com.bolao.brasileirao.dtos.TecnicoApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ApiFutebolService {

    @Autowired
    private WebClient apiFutebolClient;

    // ========== ELENCO DO TIME ==========
    public List<JogadorApiResponse> buscarElenco(Long timeId) {

        var response = apiFutebolClient.get()
                .uri("/times/" + timeId + "/elenco")
                .retrieve()
                .bodyToMono(ElencoApiResponse.class)
                .block();

        return response != null ? response.getJogadores() : List.of();
    }

    // ========== TÉCNICO DO TIME ==========
    public JogadorApiResponse buscarTecnico(Long timeId) {

        var response = apiFutebolClient.get()
                .uri("/times/" + timeId + "/tecnico")
                .retrieve()
                .bodyToMono(TecnicoApiResponse.class)
                .block();

        return response != null ? response.getTecnico() : null;
    }

    // ========== GOLEIRO TITULAR ==========
    public JogadorApiResponse buscarGoleiroTitular(Long timeId) {

        var elenco = buscarElenco(timeId);

        return elenco.stream()
                .filter(j -> j.getPosicao().equalsIgnoreCase("goleiro"))
                .findFirst()
                .orElse(null);
    }
}

