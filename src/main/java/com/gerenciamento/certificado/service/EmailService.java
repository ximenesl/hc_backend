package com.gerenciamento.certificado.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${resend.api-key}")
    private String resendApiKey;

    public void enviarEmail(String para, String assunto, String conteudoHtml) {
        if (resendApiKey == null || resendApiKey.isEmpty()) {
            System.err.println("Chave da API do Resend não configurada. Email não enviado: " + assunto);
            return;
        }

        Resend resend = new Resend(resendApiKey);

        CreateEmailOptions sendEmailRequest = CreateEmailOptions.builder()
                .from("Sistema <onboarding@resend.dev>")
                .to(para)
                .subject(assunto)
                .html(conteudoHtml)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(sendEmailRequest);
            System.out.println("Email enviado com sucesso! ID: " + data.getId());
        } catch (ResendException e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
