package com.example.agroconnect.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Colheita")

public class ColheitaController {
    @GetMapping
    public String ConsultaColheitaId(@PathVariable long id){
        return "colheita" + id;
    }
}
