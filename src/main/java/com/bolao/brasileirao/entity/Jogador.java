package com.bolao.brasileirao.entity;

import com.bolao.brasileirao.enums.Posicao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Jogadores")
public class Jogador extends BaseEntity {

    @Column(name = "atleta_id", unique = true)
    private Long atletaId;

    private String nome;

    private Long timeId;

    @Enumerated(EnumType.STRING)
    private Posicao posicao;

}
