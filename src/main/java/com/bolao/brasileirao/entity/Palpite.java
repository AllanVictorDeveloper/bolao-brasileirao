package com.bolao.brasileirao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Palpites")
public class Palpite extends BaseEntity  {

 @ManyToOne
 private Usuario usuario;

 @ManyToOne
 private Jogo jogo;

 @Column(nullable = false, length = 50)
 private Integer placarMandantePalpite;

 @Column(nullable = false, length = 50)
 private Integer placarVisitantePalpite;

 private Integer pontos=0;

}
