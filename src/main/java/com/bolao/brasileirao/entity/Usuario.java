package com.bolao.brasileirao.entity;

import com.bolao.brasileirao.enums.Perfil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Usuarios")
public class Usuario extends BaseEntity implements UserDetails {

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String sobrenome;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "email", length = 512, nullable = false, unique = true)
    private String email;


    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Boolean mudouSenha = false;

    @Column(length = 100)
    @Enumerated(EnumType.STRING)
    private Perfil perfil;

    @Column(length = 100)
    @DateTimeFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
    private LocalDateTime ultimoLogin;


    public Usuario(String role) {
        this.perfil = Perfil.valueOf(role);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities;

        if (this.perfil == Perfil.ADMINISTRADOR) {
            authorities = List.of(
                    new SimpleGrantedAuthority(Perfil.ROLE_ADMINISTRADOR),
                    new SimpleGrantedAuthority(Perfil.ROLE_PARTICIPANTE)

            );
        } else {
            authorities = List.of(new SimpleGrantedAuthority(Perfil.ROLE_PARTICIPANTE));
        }

        return authorities;
    }


    public String getUsername() {
        return this.email;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.ativo != null && this.ativo;
    }
}
