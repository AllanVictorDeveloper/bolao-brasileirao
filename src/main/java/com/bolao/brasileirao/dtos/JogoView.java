package com.bolao.brasileirao.dtos;

import com.bolao.brasileirao.entity.Jogo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class JogoView {

    private final Logger log = LoggerFactory.getLogger(JogoView.class);

    private Jogo jogo;

    private boolean podeCriarPalpite;
    private boolean podeVerPalpite;

    public JogoView(Jogo jogo) {
        this.jogo = jogo;
        calcularRegras();
    }

    private void calcularRegras() {
        String status = jogo.getStatus().name();
        LocalDateTime inicio = jogo.getDataJogo();
        LocalDateTime agora = LocalDateTime.now();

        // ----------- VER PALPITE -----------
        this.podeVerPalpite = status.equals("FINALIZADO");

        // ----------- CRIAR PALPITE -----------

        // Se não tem data, bloqueia
        if (inicio == null) {
            this.podeCriarPalpite = false;
            return;
        }

        // Só pode criar se estiver AGENDADO
        if (!status.equals("AGENDADO")) {
            this.podeCriarPalpite = false;
            return;
        }

        // Janela: de 2h antes até 20min antes do início
        LocalDateTime abre  = inicio.minusHours(2);    // abre 2h antes
        LocalDateTime fecha = inicio.minusMinutes(20); // fecha 20min antes

        // true se estiver dentro do intervalo [abre, fecha]
        boolean depoisDeAbrir   = !agora.isBefore(abre);   // agora >= abre
        boolean antesDeFechar   = !agora.isAfter(fecha);   // agora <= fecha

        this.podeCriarPalpite = depoisDeAbrir && antesDeFechar;

        log.info("Jogo {} - inicio={}, abre={}, fecha={}, agora={}, podeCriar={}",
                jogo.getId(), inicio, abre, fecha, agora, this.podeCriarPalpite);

    }



    // GETTERS
    public Jogo getJogo() { return jogo; }
    public boolean isPodeCriarPalpite() { return podeCriarPalpite; }
    public boolean isPodeVerPalpite() { return podeVerPalpite; }

    public void setPodeCriarPalpite(boolean podeCriarPalpite) {
        this.podeCriarPalpite = podeCriarPalpite;
    }

}

