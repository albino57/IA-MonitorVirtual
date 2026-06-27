package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class EmbeddingService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final String OPENAI_API_URL = "https://api.openai.com/v1/embeddings";
    private final RestTemplate restTemplate = new RestTemplate();

    public String gerarVetor(String texto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("input", texto);
        body.put("model", "text-embedding-ada-002"); // Modelo de 1536 dimensões

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Navega no JSON de retorno da OpenAI para pegar o array de floats
                List data = (List) response.getBody().get("data");
                Map firstData = (Map) data.get(0);
                List<Double> embeddingArray = (List<Double>) firstData.get("embedding");
                
                // Converte o array do Java para o formato de String do pgvector: "[0.1, 0.2, ...]"
                return embeddingArray.toString();
            }
        } catch (Exception e) {
            System.err.println("Erro ao gerar embedding: " + e.getMessage());
        }
        throw new RuntimeException("Falha ao comunicar com a OpenAI para gerar o vetor.");
    }
}