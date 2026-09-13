package com.teteband.site_tete.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ContactController {
        private final JavaMailSender mailSender;

        @Value("${spring.mail.username}")
        private String adresaFormatiei;

        public ContactController(JavaMailSender mailSender) {
            this.mailSender = mailSender;
        }

        @PostMapping("/contact")
        public String trimiteMesaj(
            @RequestParam String nume,
            @RequestParam String prenume,
            @RequestParam(required = false) String email,
            @RequestParam String telefon,
            @RequestParam(required = false) String adresa,
            @RequestParam String mesaj
        ) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(adresaFormatiei);
            mailMessage.setSubject("Mesaj nou de pe site - " + nume + " " + prenume);
            mailMessage.setText(
                "Nume:" + nume + " " + prenume + "\n" +
                "Email:" + email + "\n" + 
                "Telefon:" + telefon + "\n" +
                "Adresa:" + (adresa != null ? adresa : "-") + "\n\n" +
                "Mesaj:\n" + mesaj
            );
            mailMessage.setReplyTo(email);

            mailSender.send(mailMessage);

            return "confirmare";
        }
}
