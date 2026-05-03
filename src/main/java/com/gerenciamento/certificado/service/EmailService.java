package com.gerenciamento.certificado.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    @Value("${brevo.api-key}")
    private String brevoApiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Async
    public void enviarEmail(String para, String assunto, String conteudoHtml) {
        try {
            System.out.println("Iniciando envio via API BREVO para: " + para);

            if (brevoApiKey == null || brevoApiKey.contains("sua_chave_do_brevo_aqui")) {
                System.err.println("ERRO: Chave do Brevo nao configurada no application.yml ou no Render!");
                return;
            }

            // Montando o JSON manualmente para evitar dependências extras
            String json = "{"
                    + "\"sender\":{\"name\":\"Sistema de Certificados\",\"email\":\"" + senderEmail + "\"},"
                    + "\"to\":[{\"email\":\"" + para + "\"}],"
                    + "\"subject\":\"" + assunto + "\","
                    + "\"htmlContent\":\"" + conteudoHtml.replace("\"", "\\\"").replace("\n", "").replace("\r", "") + "\""
                    + "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("api-key", brevoApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("Email enviado com sucesso via Brevo! Status: " + response.statusCode());
            } else {
                System.err.println("ERRO BREVO: Status " + response.statusCode() + " - Resposta: " + response.body());
            }

        } catch (Exception e) {
            System.err.println("ERRO CRITICO ao enviar e-mail via Brevo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
