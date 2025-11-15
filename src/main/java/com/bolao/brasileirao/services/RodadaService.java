package com.bolao.brasileirao.services;

import com.bolao.brasileirao.dtos.RodadaResponse;
import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.repository.JogoRepository;
import com.bolao.brasileirao.services.mapper.RodadaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RodadaService {

    private final WebClient apiFutebolClient;
    private final JogoRepository jogoRepository;
    private final RodadaMapper rodadaMapper;

    public void importarRodada() {

        Integer rodadaAtual = obterProximaRodada();

        if (rodadaAtual == null) {
            System.out.println("❌ Não foi possível determinar a próxima rodada");
            return;
        }

        if (rodadaJaImportada(rodadaAtual)) {
            System.out.println("⚠ Rodada " + rodadaAtual + " já importada. Ignorando...");
            return;
        }

        System.out.println("➡ Buscando rodada " + rodadaAtual + " na API-Futebol...");

        // ========= CHAMADA API FUTEBOL ==========
        RodadaResponse apiResponse = apiFutebolClient.get()
                .uri("/campeonatos/10/rodadas/" + rodadaAtual)
                .retrieve()
                .bodyToMono(RodadaResponse.class)
                .block();

        if (apiResponse == null || apiResponse.getPartidas() == null) {
            System.out.println("❌ Nenhuma partida encontrada para rodada " + rodadaAtual);
            return;
        }

        // ========= CONVERTE PARTIDAS PARA ENTIDADE ==========
        List<Jogo> jogos = apiResponse.getPartidas().stream()
                .map(p -> this.rodadaMapper.converterParaJogo(p, rodadaAtual))
                .toList();

        jogoRepository.saveAll(jogos);

        System.out.println("✅ Rodada " + rodadaAtual + " importada com sucesso!");
    }


    public List<Jogo> buscarRodadaAtual() {
        return jogoRepository.findByRodadaOrderByDataJogoAsc(
                obterRodadaAtual()
        );
    }

    public Integer obterRodadaAtual() {
        Integer rodada = jogoRepository.findRodadaMaisAtual();

        return rodada != null ? rodada : 1; // fallback caso esteja vazio
    }

    public Integer obterProximaRodada() {
        final int RODADA_MAXIMA = 38;

        Integer rodada = jogoRepository.findRodadaMaisAtual();

        // Se não tem nenhuma rodada ainda → começa na 1
        if (rodada == null) {
            return 1;
        }

        // Próxima rodada
        int proxima = rodada + 1;

        // Não ultrapassa o limite
        if (proxima > RODADA_MAXIMA) {
            return RODADA_MAXIMA;
        }

        return proxima;
    }

    public boolean rodadaJaImportada(Integer rodada) {
        return jogoRepository.existsByRodada(rodada);
    }

}

