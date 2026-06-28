package com.example.controller;

import com.example.dto.PerguntaRequest;
import com.example.dto.RespostaResponse;
import com.example.service.TutorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") 
public class ChatController {

    private final TutorService tutorService;

    public ChatController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping
    public ResponseEntity<RespostaResponse> enviarPergunta(@RequestBody PerguntaRequest request) {
        String respostaFinal = tutorService.responderEstudante(request.pergunta());
        return ResponseEntity.ok(new RespostaResponse(respostaFinal));
    }
}