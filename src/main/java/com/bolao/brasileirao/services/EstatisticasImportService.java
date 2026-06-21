package com.bolao.brasileirao.services;

import com.bolao.brasileirao.dtos.PartidaDetalheResponse;
import com.bolao.brasileirao.dtos.PartidaDetalheResponse.*;
import com.bolao.brasileirao.entity.EstatisticaJogadorRodada;
import com.bolao.brasileirao.entity.Jogo;
import com.bolao.brasileirao.entity.StatusJogo;
import com.bolao.brasileirao.repository.EstatisticaJogadorRodadaRepository;
import com.bolao.brasileirao.repository.JogoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Importa estatísticas de cada partida finalizada chamando GET /partidas/{id}.
 *
 * Limitações da API-Futebol:
 *  - Assistências não são fornecidas → assistencias permanece null
 *  - Pênaltis perdidos não são fornecidos → penaltisPerdidos permanece null
 *  - Pênaltis defendidos não são fornecidos → penaltisDefendidos permanece null
 */
@Service
@RequiredArgsConstructor
public class EstatisticasImportService {

    private final ApiFutebolService apiFutebolService;
    private final EstatisticaJogadorRodadaRepository estatisticasRepo;
    private final JogoRepository jogoRepository;
    private final PalpiteService palpiteService;

    @Transactional
    public int importarEstatisticasPendentes() {
        List<Jogo> pendentes = jogoRepository.findByStatusAndStatsImportadasFalse(StatusJogo.FINALIZADO);

        Set<Integer> rodadasAfetadas = new java.util.HashSet<>();

        for (Jogo jogo : pendentes) {
            try {
                importarEstatisticasDaPartida(jogo);
                rodadasAfetadas.add(jogo.getRodada());
            } catch (Exception e) {
                System.out.println("❌ Erro ao importar stats da partida " + jogo.getPartidaId() + ": " + e.getMessage());
            }
        }

        for (Integer rodada : rodadasAfetadas) {
            try {
                palpiteService.recalcularPontuacaoRodada(rodada);
                System.out.println("✅ Pontuação recalculada para rodada " + rodada);
            } catch (Exception e) {
                System.out.println("❌ Erro ao recalcular pontuação rodada " + rodada + ": " + e.getMessage());
            }
        }

        return rodadasAfetadas.size();
    }

    @Transactional
    public void importarEstatisticasDaPartida(Jogo jogo) {
        PartidaDetalheResponse detalhe = apiFutebolService.buscarDetalhesPartida(jogo.getPartidaId());

        if (detalhe == null || detalhe.getEscalacoes() == null) {
            System.out.println("⚠ Escalação indisponível para partida " + jogo.getPartidaId());
            return;
        }

        Map<Long, EstatisticaJogadorRodada> statsMap = new HashMap<>();

        boolean mandanteVenceu = venceu(detalhe.getPlacar_mandante(), detalhe.getPlacar_visitante());
        boolean visitanteVenceu = venceu(detalhe.getPlacar_visitante(), detalhe.getPlacar_mandante());

        EscalacaoTime escMandante = detalhe.getEscalacoes().getMandante();
        EscalacaoTime escVisitante = detalhe.getEscalacoes().getVisitante();

        // Rastreia IDs dos goleiros de cada time para atribuir golsSofridos ao final
        Set<Long> goleirosMandate = new HashSet<>();
        Set<Long> goleirosVisitante = new HashSet<>();

        // 1) Titulares → jogou = true
        registrarTitulares(escMandante, jogo.getRodada(), mandanteVenceu, statsMap, goleirosMandate);
        registrarTitulares(escVisitante, jogo.getRodada(), visitanteVenceu, statsMap, goleirosVisitante);

        // 2) Técnicos
        registrarTecnico(escMandante, jogo.getRodada(), mandanteVenceu, statsMap);
        registrarTecnico(escVisitante, jogo.getRodada(), visitanteVenceu, statsMap);

        // 3) Substituições → quem entrou também jogou
        if (detalhe.getSubstituicoes() != null) {
            registrarSubstituicoes(detalhe.getSubstituicoes().getMandante(),
                    escMandante, jogo.getRodada(), mandanteVenceu, statsMap, goleirosMandate);
            registrarSubstituicoes(detalhe.getSubstituicoes().getVisitante(),
                    escVisitante, jogo.getRodada(), visitanteVenceu, statsMap, goleirosVisitante);
        }

        // 4) Gols → incrementa gols do marcador (ignora gol_contra)
        if (detalhe.getGols() != null) {
            processarGols(detalhe.getGols().getMandante(), jogo.getRodada(), statsMap);
            processarGols(detalhe.getGols().getVisitante(), jogo.getRodada(), statsMap);
        }

        // 5) Cartões vermelhos
        if (detalhe.getCartoes() != null && detalhe.getCartoes().getVermelho() != null) {
            processarVermelhos(detalhe.getCartoes().getVermelho().getMandante(), jogo.getRodada(), statsMap);
            processarVermelhos(detalhe.getCartoes().getVermelho().getVisitante(), jogo.getRodada(), statsMap);
        }

        // 6) GolsSofridos → total de gols concedidos pelo time, atribuído a todos goleiros que jogaram
        int golsSofridosMandante = nvl(detalhe.getPlacar_visitante());
        int golsSofridosVisitante = nvl(detalhe.getPlacar_mandante());
        atribuirGolsSofridos(goleirosMandate, golsSofridosMandante, jogo.getRodada(), statsMap);
        atribuirGolsSofridos(goleirosVisitante, golsSofridosVisitante, jogo.getRodada(), statsMap);

        // 7) Persiste (upsert)
        for (EstatisticaJogadorRodada stats : statsMap.values()) {
            estatisticasRepo.findByJogadorIdAndRodada(stats.getJogadorId(), stats.getRodada())
                    .ifPresentOrElse(
                            existente -> salvarAtualizado(existente, stats),
                            () -> estatisticasRepo.save(stats)
                    );
        }

        jogo.setStatsImportadas(true);
        jogoRepository.save(jogo);

        System.out.println("✅ Stats importadas: partida " + jogo.getPartidaId()
                + " | " + statsMap.size() + " jogadores processados");
    }

    // ─────────────────────────────────────────────────────────────────
    // PRIVADOS
    // ─────────────────────────────────────────────────────────────────

    private void registrarTitulares(EscalacaoTime esc, Integer rodada, boolean timeVenceu,
                                    Map<Long, EstatisticaJogadorRodada> statsMap,
                                    Set<Long> goleirosSet) {
        if (esc == null || esc.getTitulares() == null) return;

        for (JogadorEscalado j : esc.getTitulares()) {
            if (j.getAtleta() == null) continue;
            Long id = j.getAtleta().getAtleta_id();
            EstatisticaJogadorRodada s = obterOuCriar(id, rodada, statsMap);
            s.setJogou(true);
            s.setTimeVenceu(timeVenceu);
            if (j.isGoleiro()) goleirosSet.add(id);
        }
    }

    private void registrarTecnico(EscalacaoTime esc, Integer rodada, boolean timeVenceu,
                                  Map<Long, EstatisticaJogadorRodada> statsMap) {
        if (esc == null || esc.getTecnico() == null || esc.getTecnico().getTecnico_id() == null) return;

        Long tecnicoId = esc.getTecnico().getTecnico_id();
        EstatisticaJogadorRodada s = obterOuCriar(tecnicoId, rodada, statsMap);
        s.setJogou(true);
        s.setTimeVenceu(timeVenceu);
    }

    private void registrarSubstituicoes(List<Substituicao> substituicoes, EscalacaoTime esc,
                                        Integer rodada, boolean timeVenceu,
                                        Map<Long, EstatisticaJogadorRodada> statsMap,
                                        Set<Long> goleirosSet) {
        if (substituicoes == null) return;

        for (Substituicao sub : substituicoes) {
            if (sub.getEntrou() == null) continue;
            Long entrou_id = sub.getEntrou().getAtleta_id();
            EstatisticaJogadorRodada s = obterOuCriar(entrou_id, rodada, statsMap);
            s.setJogou(true);
            s.setTimeVenceu(timeVenceu);

            // Verifica se o substituto é goleiro (olha nas reservas da escalação)
            if (esc != null && esc.getReservas() != null) {
                boolean isGoleiro = esc.getReservas().stream()
                        .filter(r -> r.getAtleta() != null)
                        .anyMatch(r -> r.isGoleiro()
                                && entrou_id.equals(r.getAtleta().getAtleta_id()));
                if (isGoleiro) goleirosSet.add(entrou_id);
            }
        }
    }

    private void processarGols(List<Gol> gols, Integer rodada,
                                Map<Long, EstatisticaJogadorRodada> statsMap) {
        if (gols == null) return;

        for (Gol gol : gols) {
            // Gol contra não conta para o artilheiro
            if (gol.isGol_contra() || gol.getAtleta() == null) continue;
            Long id = gol.getAtleta().getAtleta_id();
            EstatisticaJogadorRodada s = obterOuCriar(id, rodada, statsMap);
            s.setGols(nvl(s.getGols()) + 1);
        }
    }

    private void processarVermelhos(List<Cartao> cartoes, Integer rodada,
                                    Map<Long, EstatisticaJogadorRodada> statsMap) {
        if (cartoes == null) return;

        for (Cartao c : cartoes) {
            // atleta pode ser null (cartão para o banco/comissão técnica)
            if (c.getAtleta() == null) continue;
            Long id = c.getAtleta().getAtleta_id();
            EstatisticaJogadorRodada s = obterOuCriar(id, rodada, statsMap);
            s.setLevouVermelho(true);
        }
    }

    private void atribuirGolsSofridos(Set<Long> goleirosIds, int totalGolsSofridos,
                                      Integer rodada,
                                      Map<Long, EstatisticaJogadorRodada> statsMap) {
        for (Long golId : goleirosIds) {
            EstatisticaJogadorRodada s = obterOuCriar(golId, rodada, statsMap);
            s.setGolsSofridos(totalGolsSofridos);
        }
    }

    private EstatisticaJogadorRodada obterOuCriar(Long jogadorId, Integer rodada,
                                                   Map<Long, EstatisticaJogadorRodada> statsMap) {
        return statsMap.computeIfAbsent(jogadorId, id -> {
            EstatisticaJogadorRodada s = new EstatisticaJogadorRodada();
            s.setJogadorId(id);
            s.setRodada(rodada);
            s.setCriadoPor("sistema");
            return s;
        });
    }

    private void salvarAtualizado(EstatisticaJogadorRodada destino, EstatisticaJogadorRodada origem) {
        destino.setJogou(origem.isJogou());
        destino.setGols(origem.getGols());
        destino.setGolsSofridos(origem.getGolsSofridos());
        destino.setLevouVermelho(origem.isLevouVermelho());
        destino.setTimeVenceu(origem.isTimeVenceu());
        estatisticasRepo.save(destino);
    }

    private boolean venceu(Integer golsA, Integer golsB) {
        if (golsA == null || golsB == null) return false;
        return golsA > golsB;
    }

    private int nvl(Integer v) {
        return v == null ? 0 : v;
    }
}
