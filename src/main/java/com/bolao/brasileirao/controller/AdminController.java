package com.bolao.brasileirao.controller;

import com.bolao.brasileirao.services.EstatisticasImportService;
import com.bolao.brasileirao.services.PalpiteService;
import com.bolao.brasileirao.services.RodadaService;
import com.bolao.brasileirao.services.interfaces.IJogadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RodadaService rodadaService;
    private final EstatisticasImportService estatisticasImportService;
    private final IJogadorService jogadorService;
    private final PalpiteService palpiteService;

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("pagina", "admin");
        model.addAttribute("proximaRodada", rodadaService.obterProximaRodada());
        return "admin";
    }

    @GetMapping("/importar-rodada")
    @ResponseBody
    public Map<String, Object> importarRodada(@RequestParam(required = false) Integer rodada) {
        try {
            int alvo = (rodada != null) ? rodada : rodadaService.obterRodadaAtualDaApi();
            rodadaService.importarRodadaEspecifica(alvo);
            return Map.of("status", "ok", "rodada", alvo);
        } catch (Exception e) {
            return Map.of("status", "erro", "mensagem", e.getMessage());
        }
    }

    @GetMapping("/importar-todas-rodadas")
    @ResponseBody
    public Map<String, Object> importarTodasRodadas() {
        int sincronizadas = 0;
        int erros = 0;

        for (int r = 1; r <= 38; r++) {
            try {
                rodadaService.importarRodadaEspecifica(r);
                sincronizadas++;
            } catch (Exception e) {
                erros++;
                System.out.println("❌ Erro na rodada " + r + ": " + e.getMessage());
            }
        }

        return Map.of("status", "ok", "sincronizadas", sincronizadas, "erros", erros);
    }

    @GetMapping("/sincronizar-jogadores")
    @ResponseBody
    public Map<String, Object> sincronizarJogadores() {
        try {
            int total = jogadorService.sincronizarTodosJogadores();
            return Map.of("status", "ok", "partidasSincronizadas", total);
        } catch (Exception e) {
            return Map.of("status", "erro", "mensagem", e.getMessage());
        }
    }

    @GetMapping("/importar-stats")
    @ResponseBody
    public Map<String, Object> importarStats() {
        try {
            int rodadasRecalculadas = estatisticasImportService.importarEstatisticasPendentes();
            return Map.of("status", "ok", "rodadasRecalculadas", rodadasRecalculadas);
        } catch (Exception e) {
            return Map.of("status", "erro", "mensagem", e.getMessage());
        }
    }

    @GetMapping("/recalcular-pontuacao")
    @ResponseBody
    public Map<String, Object> recalcularPontuacao(@RequestParam(required = false) Integer rodada) {
        try {
            if (rodada != null) {
                palpiteService.recalcularPontuacaoRodada(rodada);
                return Map.of("status", "ok", "rodada", rodada);
            }
            // sem parâmetro: recalcula todas as rodadas que têm palpites
            int total = palpiteService.recalcularTodasRodadas();
            return Map.of("status", "ok", "rodadasRecalculadas", total);
        } catch (Exception e) {
            return Map.of("status", "erro", "mensagem", e.getMessage());
        }
    }
}
