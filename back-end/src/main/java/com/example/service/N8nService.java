package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

//Serviço de automação/entrega.

@Service
public class N8nService {

    @Value("${n8n.webhook.url}")
    private String n8nWebhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async // Faz o método rodar em uma Thread separada (não trava o chat!) 'Comentário de AI'
    public void registrarAuditoria(String pergunta, String resposta) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Monta o pacote de dados que será enviado para o n8n 'Comentário de AI'
            Map<String, String> payload = new HashMap<>();
            payload.put("pergunta", pergunta);
            payload.put("resposta", resposta);
            payload.put("data_hora", java.time.LocalDateTime.now().toString());

            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
            
            // Dispara o POST para o n8n 'Comentário de AI'
            restTemplate.postForEntity(n8nWebhookUrl, request, String.class);
            
            System.out.println("Log enviado ao n8n com sucesso!");
        } catch (Exception e) {
            
            System.err.println("Erro ao comunicar com o n8n: " + e.getMessage());
        }
    }
}