package com.bolao.brasileirao.services;

import com.bolao.brasileirao.dtos.RodadaResponse;
import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.repository.JogoRepository;
import com.bolao.brasileirao.services.mapper.RodadaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RodadaService {

    private final RestTemplate apiFutebolClient;
    private final String apiFutebolBaseUrl;
    private final JogoRepository jogoRepository;
    private final RodadaMapper rodadaMapper;

    /** @return true se importou, false se já existia no banco */
    public boolean importarRodadaEspecifica(int rodada) {
        return buscarEPersistirRodada(rodada);
    }

    public void importarRodada() {
        Integer proxima = obterProximaRodada();
        if (proxima == null) {
            System.out.println("❌ Não foi possível determinar a próxima rodada");
            return;
        }
        buscarEPersistirRodada(proxima);
    }

    public List<Jogo> buscarRodadaAtual() {
        return jogoRepository.findByRodadaOrderByDataJogoAsc(obterRodadaAtual());
    }

    public Integer obterRodadaAtual() {
        Integer rodada = jogoRepository.findRodadaMaisAtual();
        return rodada != null ? rodada : 1;
    }

    public Integer obterProximaRodada() {
        final int RODADA_MAXIMA = 38;
        Integer rodada = jogoRepository.findRodadaMaisAtual();
        if (rodada == null) return 1;
        int proxima = rodada + 1;
        return proxima > RODADA_MAXIMA ? RODADA_MAXIMA : proxima;
    }

    public boolean rodadaJaImportada(Integer rodada) {
        return jogoRepository.existsByRodada(rodada);
    }

    public Jogo buscarPorId(Long id) {
        return jogoRepository.findJogoById(id)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado: " + id));
    }

    /** @return true se importou com sucesso, false se já existia */
    private boolean buscarEPersistirRodada(int rodada) {
        if (rodadaJaImportada(rodada)) {
            System.out.println("⚠ Rodada " + rodada + " já importada. Ignorando...");
            return false;
        }

        System.out.println("➡ Buscando rodada " + rodada + " na API-Futebol...");

        RodadaResponse apiResponse = apiFutebolClient.getForObject(
                apiFutebolBaseUrl + "/campeonatos/10/rodadas/" + rodada,
                RodadaResponse.class
        );

        if (apiResponse == null || apiResponse.getPartidas() == null) {
            System.out.println("❌ Nenhuma partida encontrada para rodada " + rodada);
            return false;
        }

        List<Jogo> jogos = apiResponse.getPartidas().stream()
                .map(p -> rodadaMapper.converterParaJogo(p, rodada))
                .toList();

        jogoRepository.saveAll(jogos);
        System.out.println("✅ Rodada " + rodada + " importada com sucesso!");
        return true;
    }
}
