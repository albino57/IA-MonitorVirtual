package com.example.service;

import com.example.repository.ConteudoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class TutorService {

    private final ConteudoRepository repository;
    private final RestTemplate restTemplate;
    private final EmbeddingService embeddingService; // Injetando o novo serviço

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    // Atualize o construtor para receber o EmbeddingService
    public TutorService(ConteudoRepository repository, EmbeddingService embeddingService) {
        this.repository = repository;
        this.embeddingService = embeddingService;
        this.restTemplate = new RestTemplate();
    }

    public String responderEstudante(String pergunta) {
        // 1. Agora geramos o vetor real da pergunta usando a OpenAI!
        String vetorDaPergunta = embeddingService.gerarVetor(pergunta); 

        // 2. Busca o contexto no banco de dados (o restante continua igual)
        List<String> contextos = repository.buscarContextoSemelhante(vetorDaPergunta, 3);
        String contextoConsolidado = String.join("\n", contextos);

        // 3. Monta o prompt e chama o Groq
        return chamarGroq(pergunta, contextoConsolidado);
    }

    private String chamarGroq(String pergunta, String contexto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.1-8b-instant"); // Nome exato do modelo na API do Groq

        List<Map<String, String>> messages = new ArrayList<>();
        
        // Comportamento do Monitor
        messages.add(Map.of(
            "role", "system",
            "content", "Você é um Monitor Virtual de Programação focado em ajudar estudantes. Use estritamente o contexto fornecido do material didático para responder a dúvida. Se não encontrar a resposta, avise educadamente."
        ));

        // Dúvida + Contexto Injetado
        messages.add(Map.of(
            "role", "user",
            "content", "Contexto extraído do material:\n" + contexto + "\n\nPergunta do aluno: " + pergunta
        ));

        body.put("messages", messages);
        body.put("temperature", 0.3);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_API_URL, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List choices = (List) response.getBody().get("choices");
                Map firstChoice = (Map) choices.get(0);
                Map message = (Map) firstChoice.get("message");
                return (String) message.get("content");
            }
        } catch (Exception e) {
            return "Erro ao processar a comunicação com a IA: " + e.getMessage();
        }
        return "Não foi possível obter uma resposta do monitor.";
    }
}