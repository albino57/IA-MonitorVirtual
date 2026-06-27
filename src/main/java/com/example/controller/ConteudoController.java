package com.example.controller;

import com.example.domain.ConteudoEntity;
import com.example.repository.ConteudoRepository;
import com.example.service.EmbeddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conteudo")
public class ConteudoController {

    private final ConteudoRepository repository;
    private final EmbeddingService embeddingService;

    public ConteudoController(ConteudoRepository repository, EmbeddingService embeddingService) {
        this.repository = repository;
        this.embeddingService = embeddingService;
    }

    // Recebe um texto cru (String), gera o vetor e salva no banco
    @PostMapping
    public ResponseEntity<String> adicionarParagrafo(@RequestBody String texto) {
        String vetor = embeddingService.gerarVetor(texto);
        
        ConteudoEntity entidade = new ConteudoEntity();
        entidade.setTexto(texto);
        entidade.setEmbedding(vetor); // O array gigante vai pro banco aqui
        
        repository.save(entidade);
        
        return ResponseEntity.ok("Parágrafo indexado e salvo com sucesso!");
    }
}