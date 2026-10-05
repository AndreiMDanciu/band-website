package com.teteband.site_tete.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Controller
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${RESEND_API_KEY}")
    private String apiKey;

    @Value("${CONTACT_TO_EMAIL}")
    private String adresaFormatiei;

    @PostMapping("/contact")
    public String trimiteMesaj(
            @RequestParam String nume,
            @RequestParam String prenume,
            @RequestParam(required = false) String email,
            @RequestParam String telefon,
            @RequestParam(required = false) String adresa,
            @RequestParam String mesaj,
            Model model
    ) {
        String text = "Nume: " + nume + " " + prenume + "\n"
                + "Email: " + (email == null || email.isBlank() ? "-" : email) + "\n"
                + "Telefon: " + telefon + "\n"
                + "Adresă: " + (adresa == null || adresa.isBlank() ? "-" : adresa) + "\n\n"
                + "Mesaj:\n" + mesaj;

        StringBuilder json = new StringBuilder();
        json.append("{")
            .append("\"from\":\"Tete Band Site <onboarding@resend.dev>\",")
            .append("\"to\":[\"").append(escape(adresaFormatiei)).append("\"],")
            .append("\"subject\":\"").append(escape("Mesaj nou de pe site - " + nume + " " + prenume)).append("\",")
            .append("\"text\":\"").append(escape(text)).append("\"");
        if (email != null && !email.isBlank()) {
            json.append(",\"reply_to\":\"").append(escape(email)).append("\"");
        }
        json.append("}");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                model.addAttribute("succes", true);
            } else {
                log.error("Resend a raspuns cu status {}: {}", response.statusCode(), response.body());
                model.addAttribute("succes", false);
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.error("Eroare la trimiterea email-ului", e);
            model.addAttribute("succes", false);
        }

        return "confirmare";
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}