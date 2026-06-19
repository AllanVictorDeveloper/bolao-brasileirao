package com.bolao.brasileirao.services;

import com.bolao.brasileirao.dtos.PartidaDetalheResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ApiFutebolService {

    private final RestTemplate apiFutebolClient;
    private final String apiFutebolBaseUrl;

    public PartidaDetalheResponse buscarDetalhesPartida(Long partidaId) {
        return apiFutebolClient.getForObject(
                apiFutebolBaseUrl + "/partidas/" + partidaId,
                PartidaDetalheResponse.class
        );
    }
}
