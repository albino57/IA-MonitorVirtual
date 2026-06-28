package com.example.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfService {

    public List<String> extrairParagrafos(MultipartFile arquivo) {
        try (InputStream inputStream = arquivo.getInputStream();
             PDDocument documento = PDDocument.load(inputStream)) {

            PDFTextStripper extrator = new PDFTextStripper();
            String textoCompleto = extrator.getText(documento);
          
            String[] blocos = textoCompleto.split("\\r?\\n\\r?\\n");

            return Arrays.stream(blocos)
                    .map(String::trim)
                    .filter(bloco -> bloco.length() > 50) 
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Falha ao processar o arquivo PDF: " + e.getMessage());
        }
    }
}
