package com.gerenciamento.certificado.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailRemetente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(String para, String assunto, String conteudoHtml) {
        try {
            System.out.println("Iniciando tentativa de envio de e-mail via GMAIL para: " + para);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailRemetente);
            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(conteudoHtml, true); // true indica que o conteúdo é HTML

            mailSender.send(message);
            
            System.out.println("Email enviado com sucesso para [" + para + "] via Gmail!");
        } catch (MessagingException e) {
            System.err.println("ERRO ao enviar para [" + para + "]: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERRO GENERICO ao enviar e-mail: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
