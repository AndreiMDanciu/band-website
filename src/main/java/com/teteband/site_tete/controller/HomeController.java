package com.teteband.site_tete.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller  // 'biletel' - catre Spring, "aceasta clasa gestioneaza pagini web"
public class HomeController {
    @GetMapping("/") // cand cineva acceseaza adresa radacina (/) ruleaza metoda de dedesubt
    public String home() {
        return "home"; // cauta fisierul home.html in templates
    }
}
