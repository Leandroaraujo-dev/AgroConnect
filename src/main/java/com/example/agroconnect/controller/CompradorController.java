package com.example.agroconnect.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/Comprador")

public class CompradorController {
    @GetMapping
    public String ConsultaCompradorId(@PathVariable long id){
        return "comprador" + id;
    }
}

