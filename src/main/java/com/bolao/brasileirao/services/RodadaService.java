package com.bolao.brasileirao.services;

import com.bolao.brasileirao.dtos.CampeonatoResponse;
import com.bolao.brasileirao.dtos.RodadaResponse;
import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.entity.StatusJogo;
import com.bolao.brasileirao.repository.JogoRepository;
import com.bolao.brasileirao.services.mapper.RodadaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RodadaService {

    private static final int BRASILEIRAO_ID = 10;
    private static final int RODADA_MAXIMA  = 38;

    private final ApiFutebolService apiFutebolService;
    private final JogoRepository jogoRepository;
    private final RodadaMapper rodadaMapper;

    // ── Importação ────────────────────────────────────────────────────

    /**
     * Sincroniza uma rodada com a API: insere novos jogos e atualiza os existentes.
     * @return true se pelo menos um jogo novo foi inserido
     */
    public boolean importarRodadaEspecifica(int rodada) {
        return sincronizarRodada(rodada);
    }

    /** Sincroniza a rodada atual (detectada via API). */
    public void importarRodada() {
        int rodadaAtual = obterRodadaAtualDaApi();
        sincronizarRodada(rodadaAtual);
    }

    // ── Consulta de rodada ────────────────────────────────────────────

    public List<Jogo> buscarRodadaAtual() {
        return jogoRepository.findByRodadaOrderByDataJogoAsc(obterRodadaAtual());
    }

    /** Rodada atual: a rodada do próximo jogo a partir de hoje. Fallback: última rodada. */
    public Integer obterRodadaAtual() {
        return jogoRepository
                .findFirstByDataJogoGreaterThanEqualOrderByDataJogoAsc(LocalDateTime.now())
                .map(Jogo::getRodada)
                .orElseGet(() -> {
                    Integer max = jogoRepository.findRodadaMaxima();
                    return max != null ? max : 1;
                });
    }

    /**
     * Rodada atual via API-Futebol (fonte de verdade).
     * Cai no banco se a API não responder.
     */
    public int obterRodadaAtualDaApi() {
        try {
            CampeonatoResponse camp = apiFutebolService.buscarCampeonatoBrasileiro();
            if (camp != null && camp.getRodada_atual() != null
                    && camp.getRodada_atual().getRodada() != null) {
                return camp.getRodada_atual().getRodada();
            }
        } catch (Exception e) {
            System.out.println("⚠ Falha ao buscar rodada atual da API: " + e.getMessage());
        }
        return obterRodadaAtual();

    }

    public Integer obterProximaRodada() {
        Integer rodada = jogoRepository.findRodadaMaxima();
        if (rodada == null) return 1;
        int proxima = rodada + 1;
        return proxima > RODADA_MAXIMA ? RODADA_MAXIMA : proxima;
    }

    // ── Regra de negócio ──────────────────────────────────────────────

    /**
     * Regra de palpite da rodada:
     *  - Abre quando o último jogo da rodada anterior for FINALIZADO
     *  - Fecha 20 minutos antes do primeiro jogo da rodada atual
     *  - Bloqueia se qualquer jogo da rodada já não estiver AGENDADO
     */
    public boolean podeCriarPalpite(int rodada, List<Jogo> jogosRodada) {
        if (jogosRodada.isEmpty()) return false;

        boolean todosAgendados = jogosRodada.stream()
                .allMatch(j -> j.getStatus() == StatusJogo.AGENDADO);
        if (!todosAgendados) return false;

        LocalDateTime primeiroInicio = jogosRodada.stream()
                .map(Jogo::getDataJogo)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        if (primeiroInicio == null) return false;

        if (LocalDateTime.now().isAfter(primeiroInicio.minusMinutes(20))) return false;

        if (rodada <= 1) return true;

        List<Jogo> jogosAnteriores = jogoRepository.findByRodadaOrderByDataJogoAsc(rodada - 1);
        if (jogosAnteriores.isEmpty()) return true;

        return jogosAnteriores.stream()
                .allMatch(j -> j.getStatus() == StatusJogo.FINALIZADO);
    }

    public Jogo buscarPorId(Long id) {
        return jogoRepository.findJogoById(id)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado: " + id));
    }

    // ── Privados ──────────────────────────────────────────────────────

    /**
     * Sincroniza a rodada com a API: insere novos e atualiza os existentes.
     * @return true se pelo menos um jogo novo foi inserido
     */
    private boolean sincronizarRodada(int rodada) {
        System.out.println("➡ Sincronizando rodada " + rodada + " com a API-Futebol...");

        RodadaResponse apiResponse = apiFutebolService.buscarRodada(BRASILEIRAO_ID, rodada);

        if (apiResponse == null || apiResponse.getPartidas() == null || apiResponse.getPartidas().isEmpty()) {
            System.out.println("❌ Nenhuma partida encontrada para rodada " + rodada);
            return false;
        }

        int novos = 0;
        int atualizados = 0;

        for (RodadaResponse.Partida p : apiResponse.getPartidas()) {
            var existente = jogoRepository.findByPartidaId(p.getPartida_id());
            if (existente.isPresent()) {
                rodadaMapper.atualizarJogo(existente.get(), p);
                jogoRepository.save(existente.get());
                atualizados++;
            } else {
                Jogo novo = rodadaMapper.converterParaJogo(p, rodada);
                jogoRepository.save(novo);
                novos++;
            }
        }

        System.out.println("✅ Rodada " + rodada + " | novos=" + novos + " atualizados=" + atualizados);
        return novos > 0;
    }
}
