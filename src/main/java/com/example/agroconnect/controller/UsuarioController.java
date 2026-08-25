package com.example.agroconnect.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")

public class UsuarioController {

    @GetMapping
    public  String ConsutltaUsuario(){
        return "Helo Word";
    }
    @GetMapping("/{id}")
    public String ConsultaUsuarioPorId(@PathVariable long id){
        return "Usuario por ID" + id;
    }
    @GetMapping("/empresa/{empresaId}")
    public String ConsultaUsuarioPorEmpresaId(@PathVariable long empresaId){
        return " Empresa por empresa" + empresaId;
    }

    


}
