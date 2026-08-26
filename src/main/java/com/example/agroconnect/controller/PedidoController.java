package com.example.agroconnect.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/Pedido")

    public class PedidoController {
        @GetMapping
        public String ConsultaPedidoId(@PathVariable long id) {
            return "pedido" + id;
        }
    }