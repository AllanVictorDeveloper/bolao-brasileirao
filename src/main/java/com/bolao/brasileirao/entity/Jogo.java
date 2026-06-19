package com.bolao.brasileirao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Jogos")
public class Jogo extends BaseEntity {

 @Column(name = "partida_id", unique = true)
 private Long partidaId;

 private Integer rodada;

 // Mandante
 private Long mandanteId;
 private String mandante;
 private String mandanteSigla;
 private String mandanteEscudo;


 // Visitante
 private Long visitanteId;
 private String visitante;
 private String visitanteSigla;
 private String visitanteEscudo;

 private LocalDateTime dataJogo;

 private Integer placarMandante;
 private Integer placarVisitante;

 private String estadio;

 private String slug;

 @Enumerated(EnumType.STRING)
 private StatusJogo status;

 private boolean statsImportadas = false;
}
