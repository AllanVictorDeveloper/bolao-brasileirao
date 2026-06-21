package com.bolao.brasileirao.dtos;

import com.bolao.brasileirao.entity.Jogo;

public class JogoView {

    private final Jogo jogo;
    private boolean podeCriarPalpite;
    private boolean temPalpite;

    // botões calculados explicitamente via calcularBotoes()
    private boolean mostrarCriar;
    private boolean mostrarEditar;
    private boolean mostrarVer;

    public JogoView(Jogo jogo) {
        this.jogo = jogo;
        this.podeCriarPalpite = false;
        this.temPalpite = false;
    }

    public void calcularBotoes() {
        if (podeCriarPalpite && !temPalpite) {
            mostrarCriar  = true;
            mostrarEditar = false;
            mostrarVer    = false;
        } else if (podeCriarPalpite && temPalpite) {
            mostrarCriar  = false;
            mostrarEditar = true;
            mostrarVer    = true;
        } else {
            mostrarCriar  = false;
            mostrarEditar = false;
            mostrarVer    = temPalpite;
        }
    }

    public Jogo getJogo() { return jogo; }

    public boolean isPodeCriarPalpite() { return podeCriarPalpite; }
    public boolean isTemPalpite()       { return temPalpite; }
    public boolean isMostrarCriar()     { return mostrarCriar; }
    public boolean isMostrarEditar()    { return mostrarEditar; }
    public boolean isMostrarVer()       { return mostrarVer; }

    public void setPodeCriarPalpite(boolean podeCriarPalpite) {
        this.podeCriarPalpite = podeCriarPalpite;
    }

    public void setTemPalpite(boolean temPalpite) {
        this.temPalpite = temPalpite;
    }
}
