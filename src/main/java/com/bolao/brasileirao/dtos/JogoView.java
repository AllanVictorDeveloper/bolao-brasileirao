package com.bolao.brasileirao.dtos;

import com.bolao.brasileirao.entity.Jogo;

public class JogoView {

    private final Jogo jogo;
    private boolean podeCriarPalpite;
    private boolean podeVerPalpite;

    public JogoView(Jogo jogo) {
        this.jogo = jogo;
        this.podeVerPalpite = "FINALIZADO".equals(jogo.getStatus().name());
        this.podeCriarPalpite = false; // definido pelo RodadaController via regra global
    }

    public Jogo getJogo() { return jogo; }
    public boolean isPodeCriarPalpite() { return podeCriarPalpite; }
    public boolean isPodeVerPalpite() { return podeVerPalpite; }
    public void setPodeCriarPalpite(boolean podeCriarPalpite) {
        this.podeCriarPalpite = podeCriarPalpite;
    }

    public void setPodeVerPalpite(boolean podeVerPalpite) {
        this.podeVerPalpite = podeVerPalpite;
    }
}
