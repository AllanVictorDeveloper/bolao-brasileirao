package com.bolao.brasileirao.services.scheduler;

import com.bolao.brasileirao.services.EstatisticasImportService;
import com.bolao.brasileirao.services.RodadaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FootballScheduler {

    private final RodadaService rodadaService;
    private final EstatisticasImportService estatisticasImportService;

    public FootballScheduler(RodadaService rodadaService,
                             EstatisticasImportService estatisticasImportService) {
        this.rodadaService = rodadaService;
        this.estatisticasImportService = estatisticasImportService;
    }

    /** Importa os jogos da próxima rodada toda madrugada às 3h */
    @Scheduled(cron = "0 0 3 * * *", zone = "America/Sao_Paulo")
    public void importarJogosDiariamente() {
        System.out.println("⏳ Importando jogos do Brasileirão...");
        rodadaService.importarRodada();
        System.out.println("✅ Jogos atualizados!");
    }

    /**
     * Importa estatísticas dos jogos finalizados a cada hora.
     * Só processa partidas com statsImportadas = false, então é seguro rodar com frequência.
     */
    @Scheduled(cron = "0 0 * * * *", zone = "America/Sao_Paulo")
    public void importarEstatisticas() {
        System.out.println("⏳ Verificando estatísticas pendentes...");
        estatisticasImportService.importarEstatisticasPendentes();
        System.out.println("✅ Verificação de estatísticas concluída!");
    }
}
