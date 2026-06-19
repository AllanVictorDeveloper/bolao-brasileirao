package com.bolao.brasileirao.controller;

import com.bolao.brasileirao.services.EstatisticasImportService;
import com.bolao.brasileirao.services.RodadaService;
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
            if (rodada != null) {
                rodadaService.importarRodadaEspecifica(rodada);
            } else {
                rodadaService.importarRodada();
            }
            int importada = rodada != null ? rodada : rodadaService.obterRodadaAtual();
            return Map.of("status", "ok", "rodada", importada);
        } catch (Exception e) {
            return Map.of("status", "erro", "mensagem", e.getMessage());
        }
    }

    @GetMapping("/importar-todas-rodadas")
    @ResponseBody
    public Map<String, Object> importarTodasRodadas() {
        int importadas = 0;
        int ignoradas = 0;
        int erros = 0;

        for (int r = 1; r <= 38; r++) {
            try {
                boolean importou = rodadaService.importarRodadaEspecifica(r);
                if (importou) importadas++;
                else ignoradas++;
            } catch (Exception e) {
                erros++;
                System.out.println("❌ Erro na rodada " + r + ": " + e.getMessage());
            }
        }

        return Map.of(
            "status", "ok",
            "importadas", importadas,
            "ignoradas", ignoradas,
            "erros", erros
        );
    }

    @GetMapping("/importar-stats")
    @ResponseBody
    public Map<String, String> importarStats() {
        try {
            estatisticasImportService.importarEstatisticasPendentes();
            return Map.of("status", "ok");
        } catch (Exception e) {
            return Map.of("status", "erro", "mensagem", e.getMessage());
        }
    }
}
