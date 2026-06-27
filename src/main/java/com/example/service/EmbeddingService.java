package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class EmbeddingService {

    @Value("${cohere.api.key}")
    private String cohereApiKey;

    private final String COHERE_API_URL = "https://api.cohere.com/v1/embed";
    private final RestTemplate restTemplate = new RestTemplate();

    public String gerarVetor(String texto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(cohereApiKey);

        // Estrutura exigida pela documentação da Cohere
        Map<String, Object> body = new HashMap<>();
        body.put("texts", List.of(texto)); // Eles pedem uma lista de textos
        body.put("model", "embed-multilingual-v3.0"); // Modelo otimizado para português
        body.put("input_type", "search_document"); // Obrigatório na versão 3 da Cohere

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(COHERE_API_URL, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Navega no JSON de retorno da Cohere para pegar o array
                List<List<Double>> embeddings = (List<List<Double>>) response.getBody().get("embeddings");
                
                // Pega o primeiro vetor (pois mandamos só um texto por vez) e transforma em String
                return embeddings.get(0).toString();
            }
        } catch (Exception e) {
            System.err.println("Erro ao gerar embedding com Cohere: " + e.getMessage());
        }
        throw new RuntimeException("Falha ao comunicar com a Cohere para gerar o vetor.");
    }
}