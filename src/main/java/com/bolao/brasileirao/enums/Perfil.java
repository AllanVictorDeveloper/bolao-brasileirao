package com.bolao.brasileirao.enums;

public enum Perfil {
    ADMINISTRADOR("ADMINISTRADOR"),
    PARTICIPANTE("PARTICIPANTE"),;

    private String role;

    Perfil(String role) {
        this.role = role;
    }

    public String getRole(){
        return this.role;
    }

    public static final String ROLE_ADMINISTRADOR = "ROLE_ADMINISTRADOR";
    public static final String ROLE_PARTICIPANTE = "ROLE_PARTICIPANTE";

}
