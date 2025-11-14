package com.bolao.brasileirao.services.scheduler;

import com.bolao.brasileirao.services.RodadaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FootballScheduler {

    private final RodadaService rodadaService;

    public FootballScheduler(RodadaService rodadaService) {
        this.rodadaService = rodadaService;
    }

//    @Scheduled(cron = "0 0 3 * * *", zone = "America/Sao_Paulo")
//    @Scheduled(fixedRate = 10000)
    public void importarJogosDiariamente() {

        System.out.println("⏳ Importando jogos do Brasileirão...");

        rodadaService.importarRodada();

        System.out.println("✅ Jogos atualizados!");
    }
}
