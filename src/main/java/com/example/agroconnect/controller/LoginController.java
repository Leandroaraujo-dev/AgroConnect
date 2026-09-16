package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.LoginRequest;
import com.example.agroconnect.DTO.LoginResponse;
import com.example.agroconnect.entities.Usuario;
import com.example.agroconnect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/login")
@RestController

public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;


    @PostMapping
    public ResponseEntity<LoginResponse> logar(@RequestBody LoginRequest loginRequest){

       //Usuario usuarioBanco = usuarioRepository.findAll().stream().filter()


        if (usuarioRepository.existsUsuarioByCpfAndSenha(loginRequest.getLogin(),
                loginRequest.getSenha())){


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMensagem("Bem vindo! Ao sistema de alunos");

            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
