package com.example.controller;

import com.example.domain.ConteudoEntity;
import com.example.repository.ConteudoRepository;
import com.example.service.EmbeddingService;
import com.example.service.PdfService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/conteudo")
public class ConteudoController {

    private final ConteudoRepository repository;
    private final EmbeddingService embeddingService;
    private final PdfService pdfService;

    public ConteudoController(ConteudoRepository repository, EmbeddingService embeddingService, PdfService pdfService) {
        this.repository = repository;
        this.embeddingService = embeddingService;
        this.pdfService = pdfService;
    }

    // MODO 1: Texto Puro (Útil para debugar ou inserir correções rápidas)
    @PostMapping("/texto")
    public ResponseEntity<String> adicionarParagrafo(@RequestBody String texto) {
        String vetor = embeddingService.gerarVetor(texto);
        
        ConteudoEntity entidade = new ConteudoEntity();
        entidade.setTexto(texto);
        entidade.setEmbedding(vetor);
        
        repository.save(entidade);
        return ResponseEntity.ok("Texto indexado e salvo com sucesso!");
    }

    // MODO 2: Upload de PDF (Vai processar a apostila inteira)
    @PostMapping("/pdf")
    public ResponseEntity<String> adicionarPdf(@RequestParam("arquivo") MultipartFile arquivo) {
        if (!arquivo.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Erro: O arquivo precisa ser um PDF.");
        }

        List<String> paragrafos = pdfService.extrairParagrafos(arquivo);
        int paragrafosSalvos = 0;

        for (String paragrafo : paragrafos) {
            try {
                String vetor = embeddingService.gerarVetor(paragrafo);
                
                ConteudoEntity entidade = new ConteudoEntity();
                entidade.setTexto(paragrafo);
                entidade.setEmbedding(vetor);
                
                repository.save(entidade);
                paragrafosSalvos++;
                
                // Dica: A API da OpenAI tem um limite de requisições por minuto. 
                // Um Thread.sleep() aqui ajuda a não tomar block (Rate Limit) caso o PDF seja enorme.
                Thread.sleep(500); 
                
            } catch (Exception e) {
                System.err.println("Erro ao salvar um dos parágrafos: " + e.getMessage());
            }
        }

        return ResponseEntity.ok("Leitura concluída! " + paragrafosSalvos + " parágrafos do PDF foram vetorizados no banco.");
    }
}