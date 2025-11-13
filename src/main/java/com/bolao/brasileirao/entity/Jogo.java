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

 private Long apiId;

 private Integer rodada;

 private String mandante;

 private String visitante;

 private Integer placarMandante;

 private Integer placarVisitante;

 private LocalDateTime dataJogo;

 @Enumerated(EnumType.STRING)
 private StatusJogo status;

}
