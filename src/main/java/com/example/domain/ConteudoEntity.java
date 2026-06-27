package com.example.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "apostila_conteudo")
public class ConteudoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;

    @Column(name = "embedding", columnDefinition = "vector")
    private String embedding;
}